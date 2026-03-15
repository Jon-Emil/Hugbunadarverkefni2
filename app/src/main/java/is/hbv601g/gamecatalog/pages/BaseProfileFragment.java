package is.hbv601g.gamecatalog.pages;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import androidx.recyclerview.widget.GridLayoutManager;

import is.hbv601g.gamecatalog.R;
import is.hbv601g.gamecatalog.adapters.ReviewAdapter;
import is.hbv601g.gamecatalog.adapters.SimpleGameAdapter;
import is.hbv601g.gamecatalog.entities.user.DetailedUserEntity;

/*
 * Abstract base class shared by PersonalProfileFragment and OtherUserProfileFragment.
 *
 * Both screens show the same profile layout: avatar, username, description,
 * follower/following counts, three collapsible game lists (favourites, wants-to-play,
 * has-played), and a review list. The only differences are:
 *   - PersonalProfileFragment shows email and has Edit/Logout/Delete buttons.
 *   - OtherUserProfileFragment has none of those.
 *
 * All shared logic lives here. Subclasses call initSharedViews() from onViewCreated()
 * and can override bindUser() to add their own fields after calling super.bindUser().
 */
public abstract class BaseProfileFragment extends Fragment {

    // Expand/collapse state for each game list section.
    private boolean favouritesExpanded = false;
    private boolean wantsToPlayExpanded = false;
    private boolean hasPlayedExpanded = false;

    // Collapse state for the reviews section; starts collapsed (shows first 3 only).
    private boolean reviewsExpanded = false;

    // Shared ReviewAdapter; protected so subclasses can configure it (set click listeners).
    protected ReviewAdapter reviewAdapter;

    // Game list adapters; created once in initSharedViews, data set in bindUser.
    private SimpleGameAdapter favouritesAdapter;
    private SimpleGameAdapter wantsToPlayAdapter;
    private SimpleGameAdapter hasPlayedAdapter;

    // View references cached after initSharedViews(): all IDs exist in both layouts.
    private View profileProgressBar;
    private ImageView profilePicture;
    private TextView usernameText;
    private TextView describtion;
    private TextView followerCount;
    private TextView followingCount;
    private View followersColumn;
    private View followingColumn;
    private RecyclerView reviewsRV;
    private TextView expandReviewsBtn;

    // Expand toggle buttons cached so bindUser can reset their visual state on reload.
    private TextView expandFavouritesBtn;
    private TextView expandWantsToPlayBtn;
    private TextView expandHasPlayedBtn;

    // Empty-state labels shown when a game list has no entries.
    private TextView emptyFavourites;
    private TextView emptyWantsToPlay;
    private TextView emptyHasPlayed;

    /*
     * Called by subclasses from onViewCreated() once their binding is inflated.
     * Finds all shared views by ID, creates and attaches game adapters, and wires
     * all expand/collapse click listeners.
     */
    protected void initSharedViews(@NonNull View root) {
        profileProgressBar = root.findViewById(R.id.profileProgressBar);
        profilePicture = root.findViewById(R.id.profilePicture);
        usernameText = root.findViewById(R.id.usernameText);
        describtion = root.findViewById(R.id.describtion);
        followerCount = root.findViewById(R.id.followerCount);
        followingCount = root.findViewById(R.id.followingCount);
        followersColumn = root.findViewById(R.id.followersColumn);
        followingColumn = root.findViewById(R.id.followingColumn);

        expandFavouritesBtn = root.findViewById(R.id.expandFavourites);
        expandWantsToPlayBtn = root.findViewById(R.id.expandWantsToPlay);
        expandHasPlayedBtn = root.findViewById(R.id.expandHasPlayed);

        emptyFavourites = root.findViewById(R.id.emptyFavourites);
        emptyWantsToPlay = root.findViewById(R.id.emptyWantsToPlay);
        emptyHasPlayed = root.findViewById(R.id.emptyHasPlayed);

        // Create and attach game list adapters with FlexboxLayout for chip wrapping.
        RecyclerView favouriteGamesRV = root.findViewById(R.id.favouriteGamesExpanded);
        RecyclerView wantsToPlayRV = root.findViewById(R.id.wantsToPlayExpanded);
        RecyclerView hasPlayedRV = root.findViewById(R.id.hasPlayedExpanded);

        favouriteGamesRV.setLayoutManager(new GridLayoutManager(requireContext(), 3));
        wantsToPlayRV.setLayoutManager(new GridLayoutManager(requireContext(), 3));
        hasPlayedRV.setLayoutManager(new GridLayoutManager(requireContext(), 3));

        favouritesAdapter = makeGameAdapter();
        wantsToPlayAdapter = makeGameAdapter();
        hasPlayedAdapter = makeGameAdapter();

        // When the "+x more" cover is tapped, sync the expand button to the expanded state.
        favouritesAdapter.setOnExpandClickListener(() -> {
            favouritesExpanded = true;
            rotateExpandButton(expandFavouritesBtn, true);
        });
        wantsToPlayAdapter.setOnExpandClickListener(() -> {
            wantsToPlayExpanded = true;
            rotateExpandButton(expandWantsToPlayBtn, true);
        });
        hasPlayedAdapter.setOnExpandClickListener(() -> {
            hasPlayedExpanded = true;
            rotateExpandButton(expandHasPlayedBtn, true);
        });

        favouriteGamesRV.setAdapter(favouritesAdapter);
        wantsToPlayRV.setAdapter(wantsToPlayAdapter);
        hasPlayedRV.setAdapter(hasPlayedAdapter);

        expandFavouritesBtn.setOnClickListener(v -> {
            favouritesExpanded = !favouritesExpanded;
            favouritesAdapter.setCollapsed(!favouritesExpanded);
            rotateExpandButton(expandFavouritesBtn, favouritesExpanded);
        });
        expandWantsToPlayBtn.setOnClickListener(v -> {
            wantsToPlayExpanded = !wantsToPlayExpanded;
            wantsToPlayAdapter.setCollapsed(!wantsToPlayExpanded);
            rotateExpandButton(expandWantsToPlayBtn, wantsToPlayExpanded);
        });
        expandHasPlayedBtn.setOnClickListener(v -> {
            hasPlayedExpanded = !hasPlayedExpanded;
            hasPlayedAdapter.setCollapsed(!hasPlayedExpanded);
            rotateExpandButton(expandHasPlayedBtn, hasPlayedExpanded);
        });

        reviewsRV = root.findViewById(R.id.reviews);
        reviewsRV.setLayoutManager(new LinearLayoutManager(requireContext()));

        // Wire the "See all / Show less" toggle for the reviews section.
        expandReviewsBtn = root.findViewById(R.id.expandReviews);
        expandReviewsBtn.setOnClickListener(v -> {
            reviewsExpanded = !reviewsExpanded;
            reviewAdapter.setCollapsed(!reviewsExpanded);
            expandReviewsBtn.setText(reviewsExpanded ? "Show less" : "See all");
        });
    }

    /* Shows or hides the progress bar. Subclasses forward their ViewModel's isLoading LiveData here. */
    protected void setLoading(boolean loading) {
        if (profileProgressBar != null)
            profileProgressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
    }

    /*
     * Binds all shared profile fields to the UI. Subclasses that have extra fields
     * like email override this and call super first.
     */
    protected void bindUser(DetailedUserEntity user) {
        usernameText.setText(user.getUsername());
        describtion.setText(user.getDescription() != null ? user.getDescription() : "");

        // Only the number is shown; the labels ("Followers" / "Following") are static in the layout.
        followerCount.setText(String.valueOf(user.getFollowerCount()));
        followingCount.setText(String.valueOf(user.getFollowingCount()));

        // Wire here (not in onViewCreated) because userId is only known after the network call.
        long userId = user.getId();
        followersColumn.setOnClickListener(v -> navigateToUserList(userId, "followers"));
        followingColumn.setOnClickListener(v -> navigateToUserList(userId, "following"));

        String pictureUrl = user.getProfilePictureURL();
        boolean valid = pictureUrl != null && !pictureUrl.isEmpty() && !"null".equalsIgnoreCase(pictureUrl);

        if (valid) {
            Glide.with(this).load(pictureUrl).circleCrop()
                    .placeholder(android.R.drawable.ic_menu_myplaces)
                    .error(android.R.drawable.ic_menu_myplaces)
                    .into(profilePicture);
        } else {
            profilePicture.setImageResource(android.R.drawable.ic_menu_myplaces);
        }

        // Reset collapse state and load fresh data into each game adapter.
        favouritesExpanded = false;
        wantsToPlayExpanded = false;
        hasPlayedExpanded = false;
        resetExpandButton(expandFavouritesBtn);
        resetExpandButton(expandWantsToPlayBtn);
        resetExpandButton(expandHasPlayedBtn);

        favouritesAdapter.setCollapsed(true);
        favouritesAdapter.setData(user.getFavoriteGames());
        bindEmptyState(emptyFavourites, expandFavouritesBtn, user.getFavoriteGames().isEmpty());

        wantsToPlayAdapter.setCollapsed(true);
        wantsToPlayAdapter.setData(user.getWantToPlayGames());
        bindEmptyState(emptyWantsToPlay, expandWantsToPlayBtn, user.getWantToPlayGames().isEmpty());

        hasPlayedAdapter.setCollapsed(true);
        hasPlayedAdapter.setData(user.getHavePlayedGames());
        bindEmptyState(emptyHasPlayed, expandHasPlayedBtn, user.getHavePlayedGames().isEmpty());

        // Build the review adapter in collapsed state (shows first 3 reviews only).
        // The "See all" button is only shown when there are more than 3 reviews.
        reviewAdapter = new ReviewAdapter();
        reviewAdapter.setCollapsed(true);
        reviewAdapter.setData(user.getReviews());
        reviewsRV.setAdapter(reviewAdapter);
        boolean hasMoreReviews = user.getReviews().size() > 3;
        expandReviewsBtn.setVisibility(hasMoreReviews ? View.VISIBLE : View.GONE);
    }

    // Shows the empty label and hides the expand arrow when a list has no items, and vice versa.
    private void bindEmptyState(TextView emptyLabel, TextView expandBtn, boolean isEmpty) {
        emptyLabel.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        expandBtn.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
    }

    private SimpleGameAdapter makeGameAdapter() {
        SimpleGameAdapter adapter = new SimpleGameAdapter();
        adapter.setOnGameClickListener(this::navigateToGame);
        return adapter;
    }

    // Rotates the ▼ button to indicate expanded (rotated) or collapsed (upright) state.
    private void rotateExpandButton(TextView btn, boolean expanded) {
        android.view.animation.RotateAnimation rotate = new android.view.animation.RotateAnimation(
                expanded ? 0f : 180f, expanded ? 180f : 0f,
                android.view.animation.Animation.RELATIVE_TO_SELF, 0.5f,
                android.view.animation.Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(300);
        rotate.setFillAfter(true);
        rotate.setInterpolator(new android.view.animation.DecelerateInterpolator(1.5f));
        btn.startAnimation(rotate);
    }

    // Resets a button to the upright (collapsed) visual state without animation.
    private void resetExpandButton(TextView btn) {
        btn.clearAnimation();
        btn.setRotation(0f);
    }

    protected void navigateToGame(long gameId) {
        Bundle args = new Bundle();
        args.putLong("game_id", gameId);
        Navigation.findNavController(requireView()).navigate(R.id.navigation_specific_game, args);
    }

    protected void navigateToUserList(long userId, String listType) {
        Bundle args = new Bundle();
        args.putLong("user_id", userId);
        args.putString("list_type", listType);
        Navigation.findNavController(requireView()).navigate(R.id.navigation_user_list, args);
    }

    // Clear view references; subclasses null their own binding afterwards.
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        profileProgressBar = null;
        profilePicture = null;
        usernameText = null;
        describtion = null;
        followerCount = null;
        followingCount = null;
        followersColumn = null;
        followingColumn = null;
        reviewsRV = null;
        expandReviewsBtn = null;
        expandFavouritesBtn = null;
        expandWantsToPlayBtn = null;
        expandHasPlayedBtn = null;
        favouritesAdapter = null;
        wantsToPlayAdapter = null;
        hasPlayedAdapter = null;
        emptyFavourites = null;
        emptyWantsToPlay = null;
        emptyHasPlayed = null;
    }
}
