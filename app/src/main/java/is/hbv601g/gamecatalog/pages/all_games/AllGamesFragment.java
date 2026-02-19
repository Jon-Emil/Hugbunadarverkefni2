package is.hbv601g.gamecatalog.pages.all_games;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import is.hbv601g.gamecatalog.R;
import is.hbv601g.gamecatalog.adapters.GameAdapter;
import is.hbv601g.gamecatalog.entities.game.ListedGameEntity;
import is.hbv601g.gamecatalog.pages.specific_game.SpecificGameFragment;
import is.hbv601g.gamecatalog.services.GameService;
import is.hbv601g.gamecatalog.services.NetworkService;

public class AllGamesFragment extends Fragment {

    private static final String ARG_PAGE_NR = "page_nr";

    private AllGamesViewModel viewModel;

    private TextView pageDisplayText;
    private Button previousPageButton;
    private Button nextPageButton;

    private RecyclerView gameRecycler;
    private GameAdapter gameAdapter;

    public AllGamesFragment() {}

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        return inflater.inflate(R.layout.fragment_all_games, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        pageDisplayText = view.findViewById(R.id.pageDisplay);
        previousPageButton = view.findViewById(R.id.previousPage);
        nextPageButton = view.findViewById(R.id.nextPage);

        gameRecycler = view.findViewById(R.id.gameRecycler);
        gameAdapter = new GameAdapter(game -> openSpecificGame(game.getId()));
        gameRecycler.setLayoutManager(
                new LinearLayoutManager(
                        requireContext(),
                        LinearLayoutManager.VERTICAL,
                        false
                )
        );
        gameRecycler.setAdapter(gameAdapter);

        //Initialize ViewModel
        viewModel = new ViewModelProvider(this).get(AllGamesViewModel.class);

        NetworkService networkService = new NetworkService();
        GameService gameService = new GameService(networkService);

        viewModel.init(gameService);

        nextPageButton.setOnClickListener(v -> {
            viewModel.nextPage();
        });

        previousPageButton.setOnClickListener(v -> {
            viewModel.previousPage();
        });

        viewModel.getGames().observe(getViewLifecycleOwner(), this::updateGamesInfo);
    }

    public void updateGamesInfo(List<ListedGameEntity> games) {
        gameAdapter.setData(games);
        String pageDisplay = "Page " + viewModel.getCurrentPage();
        pageDisplayText.setText(pageDisplay);
    }

    private void openSpecificGame(long gameId) {
        Fragment fragment = SpecificGameFragment.newInstance(gameId);
        String pageDisplay = "Page " + viewModel.getCurrentPage();
        pageDisplayText.setText(pageDisplay);

        requireActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.nav_host_fragment_activity_main, fragment)
                .addToBackStack(null)
                .commit();
    }
}
