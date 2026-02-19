package is.hbv601g.gamecatalog.pages.specific_game;

import android.os.Bundle;
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

import is.hbv601g.gamecatalog.R;
import is.hbv601g.gamecatalog.adapters.GenreAdapter;
import is.hbv601g.gamecatalog.adapters.ReviewAdapter;
import is.hbv601g.gamecatalog.databinding.FragmentAllGamesBinding;
import is.hbv601g.gamecatalog.databinding.FragmentSpecificGameBinding;
import is.hbv601g.gamecatalog.entities.game.DetailedGameEntity;
import is.hbv601g.gamecatalog.services.GameService;
import is.hbv601g.gamecatalog.services.NetworkService;

public class SpecificGameFragment extends Fragment {

    private static final String ARG_GAME_ID = "game_id";

    private SpecificGameViewModel viewModel;

    private FragmentSpecificGameBinding binding;

    private RecyclerView genreRecycler;
    private GenreAdapter genreAdapter;

    private RecyclerView reviewRecycler;
    private ReviewAdapter reviewAdapter;

    public static SpecificGameFragment newInstance(long gameId) {
        SpecificGameFragment fragment = new SpecificGameFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_GAME_ID, gameId);
        fragment.setArguments(args);
        return fragment;
    }

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

        genreRecycler = view.findViewById(R.id.genreRecycler);
        genreAdapter = new GenreAdapter();
        genreRecycler.setLayoutManager(
                new LinearLayoutManager(
                        requireContext(),
                        LinearLayoutManager.HORIZONTAL,
                        false
                )
        );
        genreRecycler.setAdapter(genreAdapter);

        reviewRecycler = view.findViewById(R.id.reviewRecycler);
        reviewAdapter = new ReviewAdapter();
        reviewRecycler.setLayoutManager(
                new LinearLayoutManager(
                        requireContext(),
                        LinearLayoutManager.VERTICAL,
                        false
                )
        );
        reviewRecycler.setAdapter(reviewAdapter);

        long gameId = getArguments().getLong(ARG_GAME_ID);

        NetworkService networkService = new NetworkService();
        GameService gameService = new GameService(networkService);

        viewModel = new ViewModelProvider(this).get(SpecificGameViewModel.class);

        viewModel.init(gameService, gameId);
        // IDE warning auto fix no idea how this::<methodName> works
        viewModel.getGame().observe(getViewLifecycleOwner(), this::updateGameInfo);
    }

    public void updateGameInfo(DetailedGameEntity game) {
        String title = "Title: " + game.getTitle();
        binding.gameTitle.setText(title);

        String description = "Description:\n" + game.getDescription();
        binding.gameDescription.setText(description);

        Glide.with(requireContext()).load(game.getCoverImage()).into(binding.gameImage);

        String releaseDate = "ReleaseDate: " + game.getReleaseDate();
        binding.gameReleaseDate.setText(releaseDate);

        String price = "Price: " + game.getPrice();
        binding.gamePrice.setText(price);

        String developer = "Developer: " + game.getDeveloper();
        binding.gameDeveloper.setText(developer);

        String publisher = "Publisher: " + game.getPublisher();
        binding.gamePublisher.setText(publisher);

        String rating = "Rating: " + game.getAverageRating();
        binding.gameRating.setText(rating);

        String favoriteAmount = "FavoriteAmount: " + game.getFavoriteOf().size();
        binding.gameFavoriteAmount.setText(favoriteAmount);

        String wantToPlayAmount = "WantToPlayAmount: " + game.getWantToPlay().size();
        binding.gameWantToPlayAmount.setText(wantToPlayAmount);

        String havePlayedAmount = "HavePlayedAmount: " + game.getHavePlayed().size();
        binding.gameHavePlayedAmount.setText(havePlayedAmount);

        genreAdapter.setData(game.getGenres());
        reviewAdapter.setData(game.getReviews());
    }
}

