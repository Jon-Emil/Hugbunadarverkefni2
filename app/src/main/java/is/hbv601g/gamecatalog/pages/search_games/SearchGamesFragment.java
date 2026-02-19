package is.hbv601g.gamecatalog.pages.search_games;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.List;

import is.hbv601g.gamecatalog.R;
import is.hbv601g.gamecatalog.adapters.GameAdapter;
import is.hbv601g.gamecatalog.databinding.FragmentSearchGamesBinding;
import is.hbv601g.gamecatalog.entities.game.ListedGameEntity;
import is.hbv601g.gamecatalog.services.GameService;
import is.hbv601g.gamecatalog.services.NetworkService;

public class SearchGamesFragment extends Fragment {

    private SearchGamesViewModel viewModel;

    private FragmentSearchGamesBinding binding;
    private GameAdapter gameAdapter;

    public SearchGamesFragment() {
        //Required empty constructor
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
        binding = FragmentSearchGamesBinding.inflate(inflater, container, false);
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
        viewModel = new ViewModelProvider(this).get(SearchGamesViewModel.class);

        NetworkService networkService = new NetworkService();
        GameService gameService = new GameService(networkService);

        viewModel.init(gameService);

        String[] modes = {"title", "releaseDate", "price", "developer", "publisher", "reviewAmount", "favoritesAmount", "wantToPlayAmount", "havePlayedAmount", "averageRating"};

        ArrayAdapter<String> selectorAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                modes
        );

        binding.sortBySelector.setAdapter(selectorAdapter);

        binding.titleInput.addTextChangedListener(new TextWatcher() {
              @Override
              public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                  // required override
              }

              @Override
              public void onTextChanged(CharSequence s, int start, int before, int count) {
                  viewModel.setGameTitleParam(s.toString());
              }

              @Override
              public void afterTextChanged(Editable s) {
                  // required override
              }
           }
        );

        binding.sortBySelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView parent, View view, int position, long id) {

                String selected = modes[position];
                viewModel.setSortBy(selected);
            }

            @Override
            public void onNothingSelected(AdapterView parent) {
                // required override
            }
        });

        binding.sortReverseToggleButton
                .setOnCheckedChangeListener(((buttonView, isChecked) -> {
            viewModel.setSortReverse(isChecked);
        }));

        binding.searchButton.setOnClickListener(v -> {
            viewModel.loadPage(viewModel.getCurrentPage());
        });

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
    }

    //Code from developer.android.com
    //Ensures binding is null when fragment is destroyed to avoid memory leaks
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}
