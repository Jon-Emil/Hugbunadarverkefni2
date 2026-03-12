package is.hbv601g.gamecatalog.pages.other_user_profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import is.hbv601g.gamecatalog.databinding.FragmentOtherUserProfileBinding;
import is.hbv601g.gamecatalog.pages.BaseProfileFragment;

/*
 * Public profile screen for another user (read-only).
 * Nav arg: user_id (long). Rendering logic is all in BaseProfileFragment.
 */
public class OtherUserProfileFragment extends BaseProfileFragment {

    private OtherUserProfileViewModel viewModel;
    private FragmentOtherUserProfileBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentOtherUserProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(OtherUserProfileViewModel.class);

        long userId = getArguments() != null ? getArguments().getLong("user_id") : -1;

        initSharedViews(view);

        viewModel.getIsLoading().observe(getViewLifecycleOwner(), this::setLoading);

        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_LONG).show();
                viewModel.clearErrorMessage();
            }
        });

        viewModel.getUser().observe(getViewLifecycleOwner(), this::bindUser);

        if (userId != -1) viewModel.loadProfile(userId);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
