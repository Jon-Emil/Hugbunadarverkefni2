package is.hbv601g.gamecatalog.pages.view_own_profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import is.hbv601g.gamecatalog.R;
import is.hbv601g.gamecatalog.databinding.FragmentPersonalProfileBinding;
import is.hbv601g.gamecatalog.entities.user.DetailedUserEntity;
import is.hbv601g.gamecatalog.pages.BaseProfileFragment;

public class PersonalProfileFragment extends BaseProfileFragment {

    private PersonalProfileViewModel personalProfileViewModel;
    private FragmentPersonalProfileBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentPersonalProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        personalProfileViewModel = new ViewModelProvider(this).get(PersonalProfileViewModel.class);

        // Initialise all shared profile views, avatar, game lists, expand buttons, reviews
        initSharedViews(view);


        personalProfileViewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            // Stop the pull-to-refresh animation
            binding.swipeRefreshLayout.setRefreshing(isLoading);

            // Use the existing setLoading method for the central ProgressBar
            setLoading(isLoading);
        });

        binding.swipeRefreshLayout.setOnRefreshListener(() -> {
            // You can use the existing loadProfile method
            personalProfileViewModel.loadProfile();
        });

        personalProfileViewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_LONG).show();
                personalProfileViewModel.clearErrorMessage();
            }
        });

        personalProfileViewModel.getUser().observe(getViewLifecycleOwner(), this::bindUser);

        personalProfileViewModel.getLoggedOut().observe(getViewLifecycleOwner(), loggedOut -> {
            if (Boolean.TRUE.equals(loggedOut)) {
                personalProfileViewModel.clearLoggedOut();
                Navigation.findNavController(view).navigate(R.id.navigation_login);
            }
        });

        personalProfileViewModel.getAccountDeleted().observe(getViewLifecycleOwner(), deleted -> {
            if (Boolean.TRUE.equals(deleted)) {
                Toast.makeText(requireContext(), "Account deleted", Toast.LENGTH_LONG).show();
                Navigation.findNavController(view).navigate(R.id.navigation_login);
            }
        });

        binding.modifyButton.setOnClickListener(v -> modifyButtonClicked());
        binding.logoutButton.setOnClickListener(v -> logOut());
        binding.deleteAccountButton.setOnClickListener(v -> confirmDeleteAccount());

        personalProfileViewModel.loadProfile();
    }

    @Override
    protected void bindUser(DetailedUserEntity user) {
        super.bindUser(user);
        // Email is only shown on the own-profile screen; all other fields are handled by super.
        binding.emailText.setText(user.getEmail() != null ? user.getEmail() : "");

        // Configure the shared reviewAdapter (created by super.bindUser()) with own-profile
        // specific behavior: highlight the user's own reviews and make them tappable.
        reviewAdapter.setLoggedInUsername(user.getUsername());
        reviewAdapter.setOnReviewClickListener(review -> {
            if (review.getGameId() != null) {
                navigateToGame(review.getGameId());
            } else {
                Toast.makeText(requireContext(), "This review is not linked to a game", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void modifyButtonClicked() {
        Navigation.findNavController(requireView()).navigate(R.id.navigation_modify_user);
    }

    public void logOut() {
        personalProfileViewModel.logOut();
    }

    private void confirmDeleteAccount() {
        new android.app.AlertDialog.Builder(requireContext())
                .setTitle("Delete Account")
                .setMessage("Are you sure you want to permanently delete your account? This cannot be undone.")
                .setPositiveButton("Delete", (dialog, which) ->
                        personalProfileViewModel.deleteAccount())
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}