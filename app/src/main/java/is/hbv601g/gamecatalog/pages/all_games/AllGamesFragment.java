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
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import is.hbv601g.gamecatalog.R;
import is.hbv601g.gamecatalog.adapters.GameAdapter;
import is.hbv601g.gamecatalog.databinding.FragmentAllGamesBinding;
import is.hbv601g.gamecatalog.entities.game.ListedGameEntity;
import is.hbv601g.gamecatalog.pages.specific_game.SpecificGameFragment;
import is.hbv601g.gamecatalog.services.GameService;
import is.hbv601g.gamecatalog.services.NetworkService;

public class AllGamesFragment extends Fragment {


    private AllGamesViewModel viewModel;

    private FragmentAllGamesBinding binding;
    private GameAdapter gameAdapter;

    public AllGamesFragment() {
        //Required empty constructor
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        binding = FragmentAllGamesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        gameAdapter = new GameAdapter(game -> openSpecificGame(game.getId()));
        binding.gameRecycler.setLayoutManager(
                new LinearLayoutManager(
                        requireContext(),
                        LinearLayoutManager.VERTICAL,
                        false
                )
        );
        binding.gameRecycler.setAdapter(gameAdapter);

        //Initialize ViewModel
        viewModel = new ViewModelProvider(this).get(AllGamesViewModel.class);

        NetworkService networkService = new NetworkService();
        GameService gameService = new GameService(networkService);

        viewModel.init(gameService);

        binding.nextPage.setOnClickListener(v -> {
            viewModel.nextPage();
        });

        binding.previousPage.setOnClickListener(v -> {
            viewModel.previousPage();
        });

        viewModel.getGames().observe(getViewLifecycleOwner(), this::updateGamesInfo);
    }

    public void updateGamesInfo(List<ListedGameEntity> games) {
        gameAdapter.setData(games);
        String pageDisplay = "Page " + viewModel.getCurrentPage();
        binding.pageDisplay.setText(pageDisplay);
    }

    private void openSpecificGame(long gameId) {
        Bundle bundle = new Bundle();
        bundle.putLong("game_id", gameId);

        NavController navController = Navigation.findNavController(requireView());
        navController.navigate(R.id.navigation_specific_game, bundle);
        /**
         Fragment fragment = SpecificGameFragment.newInstance(gameId);
         String pageDisplay = "Page " + viewModel.getCurrentPage();
         binding.pageDisplay.setText(pageDisplay);

         requireActivity()
         .getSupportFragmentManager()
         .beginTransaction()
         .replace(R.id.nav_host_fragment_activity_main, fragment)
         .addToBackStack(null)
         .commit();
         }
         */
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}
