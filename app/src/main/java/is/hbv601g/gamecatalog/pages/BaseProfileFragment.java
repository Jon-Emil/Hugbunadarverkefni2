package is.hbv601g.gamecatalog.pages;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;

import java.util.List;

import is.hbv601g.gamecatalog.R;
import is.hbv601g.gamecatalog.adapters.ReviewAdapter;
import is.hbv601g.gamecatalog.adapters.SimpleGameAdapter;
import is.hbv601g.gamecatalog.entities.game.SimpleGameEntity;
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

    // Shared ReviewAdapter instance; protected so subclasses can configure it (e.g. set click listeners).
    protected ReviewAdapter reviewAdapter;

    // View references cached after initSharedViews(): all IDs exist in both layouts.
    private View profileProgressBar;
    private ImageView profilePicture;
    private TextView usernameText;
    private TextView describtion;
    private TextView followerCount;
    private TextView followingCount;
    private RecyclerView favouriteGamesExpandedRV;
    private RecyclerView wantsToPlayExpandedRV;
    private RecyclerView hasPlayedExpandedRV;
    private RecyclerView reviewsRV;
    private TextView expandReviewsBtn;
    private View favouriteGamesScroll;
    private View wantsToPlayScroll;
    private View hasPlayedScroll;
    private LinearLayout favouriteGamesRow;
    private LinearLayout wantsToPlayRow;
    private LinearLayout hasPlayedRow;

    /*
     * Called by subclasses from onViewCreated() once their binding is inflated.
     * Finds all shared views by ID, configures layout managers for the three
     * RecyclerViews, and wires the expand/collapse button click listeners.
     */
    protected void initSharedViews(@NonNull View root) {
        profileProgressBar = root.findViewById(R.id.profileProgressBar);
        profilePicture = root.findViewById(R.id.profilePicture);
        usernameText = root.findViewById(R.id.usernameText);
        describtion = root.findViewById(R.id.describtion);
        followerCount = root.findViewById(R.id.followerCount);
        followingCount = root.findViewById(R.id.followingCount);

        favouriteGamesExpandedRV = root.findViewById(R.id.favouriteGamesExpanded);
        wantsToPlayExpandedRV = root.findViewById(R.id.wantsToPlayExpanded);
        hasPlayedExpandedRV = root.findViewById(R.id.hasPlayedExpanded);
        reviewsRV = root.findViewById(R.id.reviews);

        TextView expandFavourites = root.findViewById(R.id.expandFavourites);
        TextView expandWantsToPlay = root.findViewById(R.id.expandWantsToPlay);
        TextView expandHasPlayed = root.findViewById(R.id.expandHasPlayed);

        favouriteGamesScroll = root.findViewById(R.id.favouriteGamesScroll);
        wantsToPlayScroll = root.findViewById(R.id.wantsToPlayScroll);
        hasPlayedScroll = root.findViewById(R.id.hasPlayedScroll);

        favouriteGamesRow = root.findViewById(R.id.favouriteGamesRow);
        wantsToPlayRow = root.findViewById(R.id.wantsToPlayRow);
        hasPlayedRow = root.findViewById(R.id.hasPlayedRow);

        // FlexboxLayout wraps chips to multiple rows when the list is long.
        favouriteGamesExpandedRV.setLayoutManager(makeFlexLayout());
        wantsToPlayExpandedRV.setLayoutManager(makeFlexLayout());
        hasPlayedExpandedRV.setLayoutManager(makeFlexLayout());
        reviewsRV.setLayoutManager(new LinearLayoutManager(requireContext()));

        expandFavourites.setOnClickListener(v -> {
            favouritesExpanded = !favouritesExpanded;
            applyExpandState(favouriteGamesScroll, favouriteGamesExpandedRV,
                    expandFavourites, favouritesExpanded);
        });
        expandWantsToPlay.setOnClickListener(v -> {
            wantsToPlayExpanded = !wantsToPlayExpanded;
            applyExpandState(wantsToPlayScroll, wantsToPlayExpandedRV,
                    expandWantsToPlay, wantsToPlayExpanded);
        });
        expandHasPlayed.setOnClickListener(v -> {
            hasPlayedExpanded = !hasPlayedExpanded;
            applyExpandState(hasPlayedScroll, hasPlayedExpandedRV,
                    expandHasPlayed, hasPlayedExpanded);
        });

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
     * Binds all shared profile fields to the UI. Subclasses that have extra fields like email override this and call super first.
     */
    protected void bindUser(DetailedUserEntity user) {
        usernameText.setText(user.getUsername());
        describtion.setText(user.getDescription() != null ? user.getDescription() : "");
        followerCount.setText(user.getFollowerCount() + " Followers");
        followingCount.setText(user.getFollowingCount() + " Following");

        // Wire here (not in onViewCreated) because userId is only known after the network call.
        long userId = user.getId();
        followerCount.setOnClickListener(v -> navigateToUserList(userId, "followers"));
        followingCount.setOnClickListener(v -> navigateToUserList(userId, "following"));

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

        populateChipRow(favouriteGamesRow, user.getFavoriteGames());
        populateChipRow(wantsToPlayRow, user.getWantToPlayGames());
        populateChipRow(hasPlayedRow, user.getHavePlayedGames());

        favouriteGamesExpandedRV.setAdapter(makeGameAdapter(user.getFavoriteGames()));
        wantsToPlayExpandedRV.setAdapter(makeGameAdapter(user.getWantToPlayGames()));
        hasPlayedExpandedRV.setAdapter(makeGameAdapter(user.getHavePlayedGames()));

        // Build the review adapter in collapsed state (shows first 3 reviews only).
        // The "See all" button is only shown when there are more than 3 reviews.
        reviewAdapter = new ReviewAdapter();
        reviewAdapter.setCollapsed(true);
        reviewAdapter.setData(user.getReviews());
        reviewsRV.setAdapter(reviewAdapter);
        boolean hasMoreReviews = user.getReviews().size() > 3;
        expandReviewsBtn.setVisibility(hasMoreReviews ? View.VISIBLE : View.GONE);
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

    // clear view properties, subclasses null their own binding afterwards
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        profileProgressBar = null;
        profilePicture = null;
        usernameText = null;
        describtion = null;
        followerCount = null;
        followingCount = null;
        favouriteGamesExpandedRV = null;
        wantsToPlayExpandedRV = null;
        hasPlayedExpandedRV = null;
        reviewsRV = null;
        expandReviewsBtn = null;
        favouriteGamesScroll = null;
        wantsToPlayScroll = null;
        hasPlayedScroll = null;
        favouriteGamesRow = null;
        wantsToPlayRow = null;
        hasPlayedRow = null;
    }
}
