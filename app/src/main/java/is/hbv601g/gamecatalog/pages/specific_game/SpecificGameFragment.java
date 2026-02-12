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

import com.bumptech.glide.Glide;

import is.hbv601g.gamecatalog.R;
import is.hbv601g.gamecatalog.entities.game.DetailedGameEntity;
import is.hbv601g.gamecatalog.services.GameService;
import is.hbv601g.gamecatalog.services.NetworkService;

public class SpecificGameFragment extends Fragment {

    private static final String ARG_GAME_ID = "game_id";

    private SpecificGameViewModel viewModel;

    private ImageView gameImage;
    private TextView titleText;
    private TextView descriptionText;
    private TextView releaseDateText;
    private TextView priceText;
    private TextView developerText;
    private TextView publisherText;
    private TextView ratingText;

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
        return inflater.inflate(R.layout.fragment_specific_game, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        gameImage = view.findViewById(R.id.gameImage);
        titleText = view.findViewById(R.id.gameTitle);
        descriptionText = view.findViewById(R.id.gameDescription);
        releaseDateText = view.findViewById(R.id.gameReleaseDate);
        priceText = view.findViewById(R.id.gamePrice);
        developerText = view.findViewById(R.id.gameDeveloper);
        publisherText = view.findViewById(R.id.gamePublisher);
        ratingText = view.findViewById(R.id.gameRating);

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
        titleText.setText(title);

        String description = "Description:\n" + game.getDescription();
        descriptionText.setText(description);

        Glide.with(requireContext()).load(game.getCoverImage()).into(gameImage);

        String releaseDate = "ReleaseDate: " + game.getReleaseDate();
        releaseDateText.setText(releaseDate);

        String price = "Price: " + game.getPrice();
        priceText.setText(price);

        String developer = "Developer: " + game.getDeveloper();
        developerText.setText(developer);

        String publisher = "Publisher: " + game.getPublisher();
        publisherText.setText(publisher);

        String rating = "Rating: " + game.getAverageRating();
        ratingText.setText(rating);
    }
}

