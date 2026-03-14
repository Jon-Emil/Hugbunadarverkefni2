package is.hbv601g.gamecatalog.pages.offline_all_games;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.room.Room;

import java.util.ArrayList;
import java.util.List;

import is.hbv601g.gamecatalog.R;
import is.hbv601g.gamecatalog.adapters.GameAdapter;
import is.hbv601g.gamecatalog.database.CacheDatabase;
import is.hbv601g.gamecatalog.databinding.FragmentOfflineAllGamesBinding;
import is.hbv601g.gamecatalog.entities.game.CachedGame;
import is.hbv601g.gamecatalog.entities.game.ListedGameEntity;
import is.hbv601g.gamecatalog.dao.CachedGameDao;

public class OfflineAllGamesFragment extends Fragment {
    private OfflineAllGamesViewModel viewModel;

    private FragmentOfflineAllGamesBinding binding;
    private GameAdapter gameAdapter;

    public OfflineAllGamesFragment() {
        // Required empty public constructor
    }

    //Code boilerplate from developer.android.com
    //Inflates the layout for this fragment
    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        binding = FragmentOfflineAllGamesBinding.inflate(inflater, container, false);
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
        viewModel = new ViewModelProvider(this).get(OfflineAllGamesViewModel.class);

        //INITIATE DATABASE

        CacheDatabase db = Room.databaseBuilder(
                requireContext(),
                CacheDatabase.class,
                "cache_database"
        ).build();

        viewModel.init(db);

        viewModel.getGames().observe(getViewLifecycleOwner(), this::updateGamesInfo);

        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading) {
                binding.loadingIndicator.setVisibility(View.VISIBLE);
                binding.gameRecycler.setAlpha(0.3f);
            } else {
                binding.loadingIndicator.setVisibility(View.GONE);
                binding.gameRecycler.setAlpha(1f);
            }
        });

        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), errorMessage -> {
            binding.gameListErrorMessage.setText(errorMessage);
            binding.gameRecycler.setVisibility(View.GONE);
            binding.loadingIndicator.setVisibility(View.GONE);
            binding.gameListErrorContainer.setVisibility(View.VISIBLE);
        });

        binding.gameListRetryButton.setOnClickListener(v -> {
            binding.gameListErrorContainer.setVisibility(View.GONE);
            binding.gameRecycler.setVisibility(View.VISIBLE);
            viewModel.refreshPage();
        });
    }

    public void updateGamesInfo(List<CachedGame> games) {
        List<ListedGameEntity> gamesInfo = new ArrayList<>();
        for (CachedGame game : games) {
            ListedGameEntity listedGame = new ListedGameEntity(
                game.gameId,
                game.title,
                game.description,
                game.releaseDate,
                game.price,
                "",
                game.developer,
                game.publisher,
                game.reviewAmount,
                game.averageRating,
                game.favoriteAmount,
                game.wantToPlayAmount,
                game.havePlayedAmount,
                new ArrayList<>()
            );
            gamesInfo.add(listedGame);

        }
        gameAdapter.setData(gamesInfo);
    }

    private void openSpecificGame(long gameId) {
        Bundle bundle = new Bundle();
        bundle.putLong("game_id", gameId);

        //Navigate to offline specific game fragment
        NavController navController = Navigation.findNavController(requireView());
        navController.navigate(R.id.navigation_offline_specific_game, bundle);
    }

    //Code from developer.android.com
    //Ensures binding is null when fragment is destroyed to avoid memory leaks
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}
