package is.hbv601g.gamecatalog.pages.specific_game;

import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import android.util.Log;
import java.text.ParseException;
import java.util.Date;
import java.util.Locale;

import is.hbv601g.gamecatalog.R;
import is.hbv601g.gamecatalog.adapters.GenreAdapter;
import is.hbv601g.gamecatalog.adapters.ReviewAdapter;
import is.hbv601g.gamecatalog.databinding.FragmentAllGamesBinding;
import is.hbv601g.gamecatalog.databinding.FragmentSpecificGameBinding;
import is.hbv601g.gamecatalog.entities.game.DetailedGameEntity;
import is.hbv601g.gamecatalog.helpers.GameCollections;
import is.hbv601g.gamecatalog.services.GameService;
import is.hbv601g.gamecatalog.services.NetworkService;
import is.hbv601g.gamecatalog.services.UserService;

public class SpecificGameFragment extends Fragment {

    private static final String ARG_GAME_ID = "game_id";

    private SpecificGameViewModel viewModel;

    private FragmentSpecificGameBinding binding;

    private GenreAdapter genreAdapter;

    private ReviewAdapter reviewAdapter;

    /**
    public static SpecificGameFragment newInstance(long gameId) {
        SpecificGameFragment fragment = new SpecificGameFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_GAME_ID, gameId);
        fragment.setArguments(args);
        return fragment;
    }
     */

    //Code boilerplate from developer.android.com
    //Inflates the layout for this fragment
    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        binding = FragmentSpecificGameBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        genreAdapter = new GenreAdapter();
        binding.genreRecycler.setLayoutManager(
                new LinearLayoutManager(
                        requireContext(),
                        LinearLayoutManager.HORIZONTAL,
                        false
                )
        );
        binding.genreRecycler.setAdapter(genreAdapter);


        reviewAdapter = new ReviewAdapter();
        binding.reviewRecycler.setLayoutManager(
                new LinearLayoutManager(
                        requireContext(),
                        LinearLayoutManager.VERTICAL,
                        false
                )
        );
        binding.reviewRecycler.setAdapter(reviewAdapter);

        //catch args missing error to prevent crash
        Bundle args = getArguments();
        if(args == null || !args.containsKey(ARG_GAME_ID)) {
            throw new IllegalArgumentException("Missing gameId argument");
        }
        long gameId = args.getLong(ARG_GAME_ID);

        NetworkService networkService = new NetworkService(requireContext());
        GameService gameService = new GameService(networkService);
        UserService userService = new UserService(networkService);

        viewModel = new ViewModelProvider(this).get(SpecificGameViewModel.class);

        viewModel.init(gameService, userService, gameId);
        viewModel.getGame().observe(getViewLifecycleOwner(), this::updateGameInfo);

        viewModel.getIsProcessingFavorites().observe(getViewLifecycleOwner(), isLoading -> {
            binding.favoriteButton.setEnabled(!isLoading);
            if (isLoading) {
                binding.favoriteButton.setText("Loading...");
            }
        });
        viewModel.getIsInFavorites().observe(getViewLifecycleOwner(), isFavorite -> {
            binding.favoriteButton.setText(getNewCollectionText(GameCollections.FAVORITE, isFavorite));
            // couple of unnecessary calls but it's a lightweight function so not a big deal.
            updateCollectionsAmount();
        });
        binding.favoriteButton.setOnClickListener(v -> {
            Boolean isFavorite = viewModel.getIsInFavorites().getValue();
            if (isFavorite != null ? isFavorite : false) {
                viewModel.removeFromCollection(GameCollections.FAVORITE);
            } else {
                viewModel.addToCollection(GameCollections.FAVORITE);
            }
        });

        viewModel.getIsProcessingWantToPlay().observe(getViewLifecycleOwner(), isLoading -> {
            binding.wantToPlayButton.setEnabled(!isLoading);
            if (isLoading) {
                binding.wantToPlayButton.setText("Loading...");
            }
        });
        viewModel.getIsInWantToPlay().observe(getViewLifecycleOwner(), isInWantToPlay -> {
            binding.wantToPlayButton.setText(getNewCollectionText(GameCollections.WANT_TO_PLAY, isInWantToPlay));
            // couple of unnecessary calls but it's a lightweight function so not a big deal.
            updateCollectionsAmount();
        });
        binding.wantToPlayButton.setOnClickListener(v -> {
            Boolean isInWantToPlay = viewModel.getIsInWantToPlay().getValue();
            if (isInWantToPlay != null ? isInWantToPlay : false) {
                viewModel.removeFromCollection(GameCollections.WANT_TO_PLAY);
            } else {
                viewModel.addToCollection(GameCollections.WANT_TO_PLAY);
            }
        });

        viewModel.getIsProcessingHasPlayed().observe(getViewLifecycleOwner(), isLoading -> {
            binding.havePlayedButton.setEnabled(!isLoading);
            if (isLoading) {
                binding.havePlayedButton.setText("Loading...");
            }
        });
        viewModel.getIsInHasPlayed().observe(getViewLifecycleOwner(), isInHasPlayed -> {
            binding.havePlayedButton.setText(getNewCollectionText(GameCollections.HAS_PLAYED, isInHasPlayed));
            // couple of unnecessary calls but it's a lightweight function so not a big deal.
            updateCollectionsAmount();
        });
        binding.havePlayedButton.setOnClickListener(v -> {
            Boolean isInHasPlayed = viewModel.getIsInHasPlayed().getValue();
            if (isInHasPlayed != null ? isInHasPlayed : false) {
                viewModel.removeFromCollection(GameCollections.HAS_PLAYED);
            } else {
                viewModel.addToCollection(GameCollections.HAS_PLAYED);
            }
        });
    }

    private void updateGameInfo(DetailedGameEntity game) {
        String title = game.getTitle();
        binding.gameTitle.setText(title);
        binding.gameDescription.setText(game.getDescription());

        Glide.with(requireContext()).load(game.getCoverImage()).into(binding.gameImage);

        String formatted = game.getReleaseDate();
        try {
            SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
            Date date = parser.parse(game.getReleaseDate());
            formatted = DateFormat.getMediumDateFormat(getContext()).format(date);
        } catch (ParseException e) {
            // better parsing error handling inspired by Claude.
            Log.w("SpecificGameFragment", "Failed to parse release date: " + game.getReleaseDate(), e);
        }
        binding.gameReleaseDate.setText(formatted);

        String priceText = "$" + game.getPrice();
        binding.gamePrice.setText(priceText);

        binding.gameDeveloper.setText(game.getDeveloper());
        binding.gamePublisher.setText(game.getPublisher());

        Float averageRating = game.getAverageRating();
        String averageRatingText = averageRating == null ? "No Reviews" : String.valueOf(averageRating);
        binding.gameRating.setText(averageRatingText);

        binding.gameFavoriteAmount.setText(String.valueOf(game.getFavoriteOf().size()));
        binding.gameWantToPlayAmount.setText(String.valueOf(game.getWantToPlay().size()));
        binding.gameHavePlayedAmount.setText(String.valueOf(game.getHavePlayed().size()));

        genreAdapter.setData(game.getGenres());
        reviewAdapter.setData(game.getReviews());
    }

    private String getNewCollectionText(GameCollections selectedCollection, boolean isInCollection) {
        String newCollectionText = "";
        switch (selectedCollection) {
            case FAVORITE:
                newCollectionText = isInCollection ? "Unfavorite" : "Favorite";
                break;
            case WANT_TO_PLAY:
                newCollectionText = isInCollection ? "Remove From WantToPlay" : "Add to WantToPlay";
                break;
            case HAS_PLAYED:
                newCollectionText = isInCollection ? "Remove From HavePlayed" : "Add to HavePlayed";
                break;
        }
        return newCollectionText;
    }

    private void updateCollectionsAmount() {
        DetailedGameEntity game = viewModel.getGame().getValue();
        if (game == null) { return; }

        binding.gameFavoriteAmount.setText(String.valueOf(game.getFavoriteOf().size()));
        binding.gameWantToPlayAmount.setText(String.valueOf(game.getWantToPlay().size()));
        binding.gameHavePlayedAmount.setText(String.valueOf(game.getHavePlayed().size()));
    }

    //Code from developer.android.com
    //Ensures binding is null when fragment is destroyed to avoid memory leaks
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

