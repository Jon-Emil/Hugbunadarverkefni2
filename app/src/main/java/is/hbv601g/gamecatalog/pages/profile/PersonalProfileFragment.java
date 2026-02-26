package is.hbv601g.gamecatalog.pages.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;

import java.util.List;

import is.hbv601g.gamecatalog.R;
import is.hbv601g.gamecatalog.adapters.ReviewAdapter;
import is.hbv601g.gamecatalog.adapters.SimpleGameAdapter;
import is.hbv601g.gamecatalog.databinding.FragmentPersonalProfileBinding;
import is.hbv601g.gamecatalog.entities.game.SimpleGameEntity;
import is.hbv601g.gamecatalog.entities.user.DetailedUserEntity;

public class PersonalProfileFragment extends Fragment {

    private PersonalProfileViewModel personalProfileViewModel;
    private FragmentPersonalProfileBinding binding;

    private boolean favouritesExpanded = false;
    private boolean wantsToPlayExpanded = false;
    private boolean hasPlayedExpanded = false;

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

        // make new lines for expanded game capsules
        binding.favouriteGamesExpanded.setLayoutManager(makeFlexLayout());
        binding.wantsToPlayExpanded.setLayoutManager(makeFlexLayout());
        binding.hasPlayedExpanded.setLayoutManager(makeFlexLayout());

        binding.reviews.setLayoutManager(new LinearLayoutManager(requireContext()));

        // expand and collapse
        binding.expandFavourites.setOnClickListener(v -> {
            favouritesExpanded = !favouritesExpanded;
            applyExpandState(binding.favouriteGamesScroll,
                    binding.favouriteGamesExpanded, binding.expandFavourites, favouritesExpanded);
        });
        binding.expandWantsToPlay.setOnClickListener(v -> {
            wantsToPlayExpanded = !wantsToPlayExpanded;
            applyExpandState(binding.wantsToPlayScroll,
                    binding.wantsToPlayExpanded, binding.expandWantsToPlay, wantsToPlayExpanded);
        });
        binding.expandHasPlayed.setOnClickListener(v -> {
            hasPlayedExpanded = !hasPlayedExpanded;
            applyExpandState(binding.hasPlayedScroll,
                    binding.hasPlayedExpanded, binding.expandHasPlayed, hasPlayedExpanded);
        });

        personalProfileViewModel.getIsLoading().observe(getViewLifecycleOwner(), loading ->
                binding.profileProgressBar.setVisibility(loading ? View.VISIBLE : View.GONE));

        personalProfileViewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null) Toast.makeText(requireContext(), error, Toast.LENGTH_LONG).show();
        });

        personalProfileViewModel.getUser().observe(getViewLifecycleOwner(), this::bindUser);

        personalProfileViewModel.getLoggedOut().observe(getViewLifecycleOwner(), loggedOut -> {
            if (Boolean.TRUE.equals(loggedOut)) {
                personalProfileViewModel.clearLoggedOut();
                Navigation.findNavController(view).navigate(R.id.navigation_login);
            }
        });

        binding.modifyButton.setOnClickListener(v -> modifyButtonClicked());
        binding.logoutButton.setOnClickListener(v -> logOut());

        personalProfileViewModel.loadProfile();
    }

    private FlexboxLayoutManager makeFlexLayout() {
        FlexboxLayoutManager flex = new FlexboxLayoutManager(requireContext());
        flex.setFlexDirection(FlexDirection.ROW);
        flex.setFlexWrap(FlexWrap.WRAP);
        flex.setJustifyContent(JustifyContent.FLEX_START);
        return flex;
    }

    private void applyExpandState(View scrollView, View expandedView,
                                  TextView expandBtn, boolean expanded) {
        if (expanded) {
            animateOut(expandedView, () -> {
                expandedView.setVisibility(View.GONE);
                scrollView.setVisibility(View.VISIBLE);
                animateIn(scrollView);
            });
        } else {
            animateOut(scrollView, () -> {
                scrollView.setVisibility(View.GONE);
                expandedView.setVisibility(View.VISIBLE);
                animateIn(expandedView);
            });
        }

        android.view.animation.RotateAnimation rotate = new android.view.animation.RotateAnimation(
                expanded ? 180f : 0f, expanded ? 0f : 180f,
                android.view.animation.Animation.RELATIVE_TO_SELF, 0.5f,
                android.view.animation.Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(300);
        rotate.setFillAfter(true);
        rotate.setInterpolator(new android.view.animation.DecelerateInterpolator(1.5f));
        expandBtn.startAnimation(rotate);
        expandBtn.setText(expanded ? "▼" : "—");
    }

    private void animateIn(View view) {
        view.setAlpha(0f);
        view.setTranslationY(-12f);
        view.animate().alpha(1f).translationY(0f).setDuration(280)
                .setInterpolator(new android.view.animation.DecelerateInterpolator(1.5f)).start();
    }

    private void animateOut(View view, Runnable onEnd) {
        view.animate().alpha(0f).translationY(-8f).setDuration(200)
                .setInterpolator(new android.view.animation.AccelerateInterpolator(1.5f))
                .withEndAction(() -> {
                    view.setAlpha(1f);
                    view.setTranslationY(0f);
                    onEnd.run();
                }).start();
    }

    private void populateChipRow(LinearLayout row, List<SimpleGameEntity> games) {
        row.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(requireContext());
        for (SimpleGameEntity game : games) {
            TextView chip = (TextView) inflater.inflate(R.layout.item_chip_game, row, false);
            chip.setText(game.getTitle());
            chip.setOnClickListener(v -> navigateToGame(game.getId()));
            row.addView(chip);
        }
    }

    private SimpleGameAdapter makeGameAdapter(List<SimpleGameEntity> games) {
        SimpleGameAdapter adapter = new SimpleGameAdapter();
        adapter.setOnGameClickListener(this::navigateToGame);
        adapter.setData(games);
        return adapter;
    }

    private void bindUser(DetailedUserEntity user) {
        binding.usernameText.setText(user.getUsername());
        binding.emailText.setText(user.getEmail() != null ? user.getEmail() : "");
        binding.describtion.setText(user.getDescription() != null ? user.getDescription() : "");
        binding.followerCount.setText(user.getFollowerCount() + " Followers");
        binding.followingCount.setText(user.getFollowingCount() + " Following");

        String pictureUrl = user.getProfilePictureURL();
        if (pictureUrl != null && !pictureUrl.isEmpty()) {
            Glide.with(this).load(pictureUrl).circleCrop()
                    .placeholder(android.R.drawable.ic_menu_myplaces)
                    .error(android.R.drawable.ic_menu_myplaces)
                    .into(binding.profilePicture);
        } else {
            binding.profilePicture.setImageResource(android.R.drawable.ic_menu_myplaces);
        }

        populateChipRow(binding.favouriteGamesRow, user.getFavoriteGames());
        populateChipRow(binding.wantsToPlayRow, user.getWantToPlayGames());
        populateChipRow(binding.hasPlayedRow, user.getHavePlayedGames());

        binding.favouriteGamesExpanded.setAdapter(makeGameAdapter(user.getFavoriteGames()));
        binding.wantsToPlayExpanded.setAdapter(makeGameAdapter(user.getWantToPlayGames()));
        binding.hasPlayedExpanded.setAdapter(makeGameAdapter(user.getHavePlayedGames()));

        ReviewAdapter reviewAdapter = new ReviewAdapter();
        reviewAdapter.setData(user.getReviews());
        binding.reviews.setAdapter(reviewAdapter);
    }

    private void navigateToGame(long gameId) {
        Bundle args = new Bundle();
        args.putLong("game_id", gameId);
        Navigation.findNavController(requireView()).navigate(R.id.navigation_specific_game, args);
    }

    public void modifyButtonClicked() {
        Navigation.findNavController(requireView()).navigate(R.id.navigation_modify_user);
    }

    public void logOut() {
        personalProfileViewModel.logOut();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}