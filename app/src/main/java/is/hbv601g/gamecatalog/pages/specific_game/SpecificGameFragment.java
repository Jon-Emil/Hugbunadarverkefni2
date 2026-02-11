package is.hbv601g.gamecatalog.pages.specific_game;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import is.hbv601g.gamecatalog.R;
import is.hbv601g.gamecatalog.services.GameService;
import is.hbv601g.gamecatalog.services.NetworkService;

public class SpecificGameFragment extends Fragment {

    private static final String ARG_GAME_ID = "game_id";

    private SpecificGameViewModel viewModel;

    private TextView titleText;
    private TextView genreText;

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

        titleText = view.findViewById(R.id.gameTitle);
        genreText = view.findViewById(R.id.gameGenre);

        long gameId = getArguments().getLong(ARG_GAME_ID);

        NetworkService networkService = new NetworkService();
        GameService gameService = new GameService(networkService);

        viewModel = new ViewModelProvider(this).get(SpecificGameViewModel.class);

        viewModel.init(gameService, gameId);

        viewModel.getGame().observe(getViewLifecycleOwner(), game -> {
            titleText.setText(game.getTitle());
            genreText.setText(game.getDescription());
        });
    }
}

