package is.hbv601g.gamecatalog.pages.modify_user;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.bumptech.glide.Glide;
import androidx.navigation.Navigation;
import is.hbv601g.gamecatalog.R;
import is.hbv601g.gamecatalog.databinding.FragmentModifyUserBinding;
import is.hbv601g.gamecatalog.entities.user.SimpleUserEntity;
import is.hbv601g.gamecatalog.services.NetworkService;
import is.hbv601g.gamecatalog.services.UserService;

public class ModifyUserFragment extends Fragment {

    private FragmentModifyUserBinding binding;
    private ModifyUserViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
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
                requireActivity().finish(); // Or navigate to login
            }
        });
    }

    private void updateUI(SimpleUserEntity user) {
        binding.nameInput.setText(user.getUsername());
        binding.descriptionInput.setText(user.getDescription());

        String url = user.getProfilePictureURL();

        boolean valid = url != null && !url.isEmpty() && !"null".equalsIgnoreCase(url);
        // matching  SpecificGameFragment
        Glide.with(this)
                .load(valid ? url: null)
                .placeholder(android.R.drawable.ic_menu_gallery)
                .error(android.R.drawable.ic_menu_gallery)
                .into(binding.profileImage);
    }

    private void setupListeners() {
        binding.commitButton.setOnClickListener(v -> {
            String name = binding.nameInput.getText().toString();
            String desc = binding.descriptionInput.getText().toString();
            viewModel.updateProfile(name, desc);
        });

        binding.deleteUserButton.setOnClickListener(v -> {
            viewModel.deleteAccount();
        });

        binding.btnChangePic.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Gallery Logic Here", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}