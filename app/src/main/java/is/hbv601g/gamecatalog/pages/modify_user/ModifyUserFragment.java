package is.hbv601g.gamecatalog.pages.modify_user;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.yalantis.ucrop.UCrop;

import java.io.File;

import is.hbv601g.gamecatalog.R;
import is.hbv601g.gamecatalog.databinding.FragmentModifyUserBinding;
import is.hbv601g.gamecatalog.entities.user.SimpleUserEntity;
import is.hbv601g.gamecatalog.services.NetworkService;
import is.hbv601g.gamecatalog.services.UserService;

public class ModifyUserFragment extends Fragment {

    private FragmentModifyUserBinding binding;
    private ModifyUserViewModel viewModel;

    // URI of the cropped image ready to upload; null if user has not picked one
    private Uri croppedImageUri = null;

    private Uri cameraOutputUri = null;


    // Launcher for the system photo picker (no permission needed on API 33+)
    private final ActivityResultLauncher<PickVisualMediaRequest> imagePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                if (uri != null) {
                    launchCrop(uri);
                }
            });

    // Launcher for requesting camera permission at runtime
    private final ActivityResultLauncher<String> cameraPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), granted -> {
                if (granted) {
                    startCamera();
                } else {
                    Toast.makeText(requireContext(),
                            "Camera permission is required to take a photo",
                            Toast.LENGTH_SHORT).show();
                }
            });

    private final ActivityResultLauncher<Uri> cameraLauncher =
            registerForActivityResult(new ActivityResultContracts.TakePicture(), success -> {
                if (success && cameraOutputUri != null) {
                    launchCrop(cameraOutputUri);
                }
            });

    // Launcher that receives the result from UCropActivity
    private final ActivityResultLauncher<android.content.Intent> cropLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == android.app.Activity.RESULT_OK && result.getData() != null) {
                    Uri outputUri = UCrop.getOutput(result.getData());
                    if (outputUri != null) {
                        croppedImageUri = outputUri;
                        // Show preview of the cropped image immediately
                        Glide.with(this)
                                .load(croppedImageUri)
                                .circleCrop()
                                .into(binding.profileImage);
                    }
                } else if (result.getResultCode() == UCrop.RESULT_ERROR && result.getData() != null) {
                    Throwable cropError = UCrop.getError(result.getData());
                    Toast.makeText(requireContext(),
                            "Crop failed: " + (cropError != null ? cropError.getMessage() : "unknown error"),
                            Toast.LENGTH_SHORT).show();
                }
            });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentModifyUserBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        NetworkService networkService = new NetworkService(requireContext());
        UserService userService = new UserService(networkService);

        viewModel = new ViewModelProvider(this).get(ModifyUserViewModel.class);
        viewModel.init(userService);

        setupObservers();
        setupListeners();
    }

    private void setupObservers() {
        viewModel.getUserProfile().observe(getViewLifecycleOwner(), this::updateUI);

        viewModel.getStatusMessage().observe(getViewLifecycleOwner(), msg -> {
            if (msg == null) return;
            Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show();
            if (msg.equals("Profile Update Successful")) {
                Navigation.findNavController(requireView()).navigateUp();
            }
        });

        viewModel.isUserDeleted().observe(getViewLifecycleOwner(), deleted -> {
            if (deleted) {
                Toast.makeText(requireContext(), "Account Deleted", Toast.LENGTH_LONG).show();
                requireActivity().finish();
            }
        });
    }

    private void updateUI(SimpleUserEntity user) {
        binding.nameInput.setText(user.getUsername());
        binding.descriptionInput.setText(user.getDescription());

        Glide.with(this)
                .load(user.getProfilePictureURL())
                .circleCrop()
                .placeholder(android.R.drawable.ic_menu_gallery)
                .into(binding.profileImage);
    }

    private void setupListeners() {
        binding.commitButton.setOnClickListener(v -> {
            String name = binding.nameInput.getText().toString();
            String desc = binding.descriptionInput.getText().toString();
            viewModel.updateProfile(name, desc, croppedImageUri);
        });

        binding.deleteUserButton.setOnClickListener(v -> viewModel.deleteAccount());

        // Open the system photo picker when the user taps "Change"
        binding.btnChangePic.setOnClickListener(v -> {
            new android.app.AlertDialog.Builder(requireContext())
                    .setTitle("Change Profile Picture")
                    .setItems(new String[]{"Choose from Gallery", "Take a Photo"}, (dialog, which) -> {
                        if (which == 0) {
                            imagePickerLauncher.launch(
                                    new PickVisualMediaRequest.Builder()
                                            .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                                            .build()
                            );
                        } else {
                            launchCamera();
                        }
                    })
                    .show();
        });
    }

    // Creates a temp output file and launches UCropActivity
    private void launchCrop(Uri sourceUri) {
        // Output file inside cache dir — shared via FileProvider
        File outputFile = new File(requireContext().getCacheDir(), "cropped_avatar_" + System.currentTimeMillis() + ".jpg");
        Uri outputUri = FileProvider.getUriForFile(
                requireContext(),
                requireContext().getPackageName() + ".fileprovider",
                outputFile
        );

        UCrop.Options options = new UCrop.Options();
        options.setCircleDimmedLayer(true);         // circular crop overlay
        options.setShowCropFrame(false);
        options.setShowCropGrid(false);
        options.setCompressionQuality(90);

        android.content.Intent cropIntent = UCrop.of(sourceUri, outputUri)
                .withAspectRatio(1, 1)              // square / circle crop
                .withMaxResultSize(512, 512)         // limit output resolution
                .withOptions(options)
                .getIntent(requireContext());

        cropLauncher.launch(cropIntent);
    }

    private void launchCamera() {
        if (!requireContext().getPackageManager()
                .hasSystemFeature(android.content.pm.PackageManager.FEATURE_CAMERA_ANY)) {
            Toast.makeText(requireContext(), "No camera available on this device", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if permission is already granted, otherwise request it
        if (androidx.core.content.ContextCompat.checkSelfPermission(
                requireContext(), android.Manifest.permission.CAMERA)
                == android.content.pm.PackageManager.PERMISSION_GRANTED) {
            startCamera();
        } else {
            cameraPermissionLauncher.launch(android.Manifest.permission.CAMERA);
        }
    }

    // Separate method that actually launches the camera, only called after permission is confirmed
    private void startCamera() {
        File photoFile = new File(requireContext().getCacheDir(),
                "camera_capture_" + System.currentTimeMillis() + ".jpg");
        cameraOutputUri = FileProvider.getUriForFile(
                requireContext(),
                requireContext().getPackageName() + ".fileprovider",
                photoFile
        );
        cameraLauncher.launch(cameraOutputUri);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}