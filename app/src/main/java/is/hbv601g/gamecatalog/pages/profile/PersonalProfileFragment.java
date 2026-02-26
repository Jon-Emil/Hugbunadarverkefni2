package is.hbv601g.gamecatalog.pages.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import is.hbv601g.gamecatalog.R;
import is.hbv601g.gamecatalog.adapters.ReviewAdapter;
import is.hbv601g.gamecatalog.adapters.SimpleGameAdapter;
import is.hbv601g.gamecatalog.databinding.FragmentPersonalProfileBinding;
import is.hbv601g.gamecatalog.entities.user.DetailedUserEntity;

public class PersonalProfileFragment extends Fragment {

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

        // Set up RecyclerViews
        binding.favouriteGames.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.wantsToPlay.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.hasPlayed.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.reviews.setLayoutManager(new LinearLayoutManager(requireContext()));

        // Observe loading state
        personalProfileViewModel.getIsLoading().observe(getViewLifecycleOwner(), loading -> {
            binding.profileProgressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        });

        // Observe errors
        personalProfileViewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_LONG).show();
            }
        });

        // Observe user data
        personalProfileViewModel.getUser().observe(getViewLifecycleOwner(), this::bindUser);

        // Observe logout
        personalProfileViewModel.getLoggedOut().observe(getViewLifecycleOwner(), loggedOut -> {
            if (Boolean.TRUE.equals(loggedOut)) {
                personalProfileViewModel.clearLoggedOut();
                Navigation.findNavController(view).navigate(R.id.navigation_login);
            }
        });

        // Button listeners
        binding.modifyButton.setOnClickListener(v -> modifyButtonClicked());
        binding.logoutButton.setOnClickListener(v -> logOut());
        binding.backButton.setOnClickListener(v -> onBackButtonClicked());

        personalProfileViewModel.loadProfile();
    }

    private void bindUser(DetailedUserEntity user) {
        binding.usernameText.setText(user.getUsername());
        binding.emailText.setText(user.getEmail() != null ? user.getEmail() : "");
        binding.describtion.setText(user.getDescription() != null ? user.getDescription() : "");

        // followerCount and followingCount — set to 0 until API supports it
        binding.followerCount.setText("0 Followers");
        binding.followingCount.setText("0 Following");

        // Favourite games
        SimpleGameAdapter favouriteAdapter = new SimpleGameAdapter();
        favouriteAdapter.setData(user.getFavoriteGames());
        binding.favouriteGames.setAdapter(favouriteAdapter);

        // Wants to play
        SimpleGameAdapter wantsToPlayAdapter = new SimpleGameAdapter();
        wantsToPlayAdapter.setData(user.getWantToPlayGames());
        binding.wantsToPlay.setAdapter(wantsToPlayAdapter);

        // Has played
        SimpleGameAdapter hasPlayedAdapter = new SimpleGameAdapter();
        hasPlayedAdapter.setData(user.getHavePlayedGames());
        binding.hasPlayed.setAdapter(hasPlayedAdapter);

        // Reviews
        ReviewAdapter reviewAdapter = new ReviewAdapter();
        reviewAdapter.setData(user.getReviews());
        binding.reviews.setAdapter(reviewAdapter);
    }

    public void modifyButtonClicked() {
        // Navigate to edit profile screen when that page is ready
        Toast.makeText(requireContext(), "Edit profile coming soon", Toast.LENGTH_SHORT).show();
    }

    public void logOut() {
        personalProfileViewModel.logOut();
    }

    public void onBackButtonClicked() {
        Navigation.findNavController(requireView()).navigateUp();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}