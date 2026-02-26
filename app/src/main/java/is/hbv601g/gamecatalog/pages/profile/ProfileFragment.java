package is.hbv601g.gamecatalog.pages.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import is.hbv601g.gamecatalog.R;
import is.hbv601g.gamecatalog.adapters.ReviewAdapter;
import is.hbv601g.gamecatalog.entities.user.DetailedUserEntity;

public class ProfileFragment extends Fragment {

    private ProfileViewModel viewModel;

    private ProgressBar progressBar;
    private TextView usernameText;
    private TextView emailText;
    private TextView descriptionText;
    private TextView statsReviews;
    private TextView statsFavorites;
    private TextView statsWantToPlay;
    private TextView statsHavePlayed;
    private RecyclerView reviewRecycler;
    private TextView noReviewsText;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        progressBar     = view.findViewById(R.id.profileProgressBar);
        usernameText    = view.findViewById(R.id.profileUsername);
        emailText       = view.findViewById(R.id.profileEmail);
        descriptionText = view.findViewById(R.id.profileDescription);
        statsReviews    = view.findViewById(R.id.statReviews);
        statsFavorites  = view.findViewById(R.id.statFavorites);
        statsWantToPlay = view.findViewById(R.id.statWantToPlay);
        statsHavePlayed = view.findViewById(R.id.statHavePlayed);
        reviewRecycler  = view.findViewById(R.id.profileReviewRecycler);
        noReviewsText   = view.findViewById(R.id.noReviewsText);

        reviewRecycler.setLayoutManager(new LinearLayoutManager(requireContext()));

        viewModel = new ViewModelProvider(this).get(ProfileViewModel.class);

        viewModel.getIsLoading().observe(getViewLifecycleOwner(), loading -> {
            progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        });

        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_LONG).show();
            }
        });

        viewModel.getUser().observe(getViewLifecycleOwner(), this::bindUser);

        viewModel.loadProfile();
    }

    private void bindUser(DetailedUserEntity user) {
        usernameText.setText(user.getUsername());

        if (user.getEmail() != null && !user.getEmail().isEmpty()) {
            emailText.setText(user.getEmail());
            emailText.setVisibility(View.VISIBLE);
        } else {
            emailText.setVisibility(View.GONE);
        }

        if (user.getDescription() != null && !user.getDescription().isEmpty()) {
            descriptionText.setText(user.getDescription());
            descriptionText.setVisibility(View.VISIBLE);
        } else {
            descriptionText.setVisibility(View.GONE);
        }

        statsReviews.setText(getString(R.string.stat_reviews, user.getReviews().size()));
        statsFavorites.setText(getString(R.string.stat_favorites, user.getFavoriteGames().size()));
        statsWantToPlay.setText(getString(R.string.stat_want_to_play, user.getWantToPlayGames().size()));
        statsHavePlayed.setText(getString(R.string.stat_have_played, user.getHavePlayedGames().size()));

        if (user.getReviews().isEmpty()) {
            noReviewsText.setVisibility(View.VISIBLE);
            reviewRecycler.setVisibility(View.GONE);
        } else {
            noReviewsText.setVisibility(View.GONE);
            reviewRecycler.setVisibility(View.VISIBLE);
            ReviewAdapter adapter = new ReviewAdapter();
            adapter.setData(user.getReviews());
            reviewRecycler.setAdapter(adapter);
        }
    }
}