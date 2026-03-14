package is.hbv601g.gamecatalog.pages.offline_specific_game;

import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.room.Room;


import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import is.hbv601g.gamecatalog.adapters.GenreAdapter;
import is.hbv601g.gamecatalog.adapters.ReviewAdapter;
import is.hbv601g.gamecatalog.database.CacheDatabase;
import is.hbv601g.gamecatalog.databinding.FragmentOfflineSpecificGameBinding;
import is.hbv601g.gamecatalog.entities.game.CachedGame;
import is.hbv601g.gamecatalog.entities.game.DetailedGameEntity;
import is.hbv601g.gamecatalog.dao.CachedGameDao;

public class OfflineSpecificGameFragment extends Fragment {
    private static final String ARG_GAME_ID = "game_id";

    //Question if we want to display genres and reviews while offline, especially reviews??
    private GenreAdapter genreAdapter;

    private ReviewAdapter reviewAdapter;

    private OfflineSpecificGameViewModel viewModel;

    private FragmentOfflineSpecificGameBinding binding;

    //Code boilerplate from developer.android.com
    //Inflates the layout for this fragment
    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        binding = FragmentOfflineSpecificGameBinding.inflate(inflater, container, false);
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

        //Get the cache database
        CacheDatabase db = Room.databaseBuilder(
                requireContext(),
                CacheDatabase.class,
                "cache_database"
        ).build();


        viewModel = new ViewModelProvider(this).get(OfflineSpecificGameViewModel.class);

        viewModel.init(db, gameId);
        // IDE warning auto fix no idea how this::<methodName> works
        viewModel.getGame().observe(getViewLifecycleOwner(), this::updateGameInfo);
    }

    public void updateGameInfo(CachedGame gameInfo) {
        DetailedGameEntity game = new DetailedGameEntity(
                gameInfo.gameId,
                gameInfo.title,
                gameInfo.description,
                gameInfo.releaseDate,
                gameInfo.price,
                "",
                gameInfo.developer,
                gameInfo.publisher,
                gameInfo.averageRating,
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>()
        );
        String title = game.getTitle();
        binding.gameTitle.setText(title);
        binding.gameDescription.setText(game.getDescription());

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

    //Code from developer.android.com
    //Ensures binding is null when fragment is destroyed to avoid memory leaks
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


}
