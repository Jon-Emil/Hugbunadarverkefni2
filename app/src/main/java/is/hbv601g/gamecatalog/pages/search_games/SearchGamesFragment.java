package is.hbv601g.gamecatalog.pages.search_games;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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
import is.hbv601g.gamecatalog.services.GenreService;
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
        viewModel = new ViewModelProvider(requireActivity()).get(SearchGamesViewModel.class);

        NetworkService networkService = new NetworkService(requireContext());
        GameService gameService = new GameService(networkService);
        GenreService genreService = new GenreService(networkService);

        viewModel.init(gameService, genreService);

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
            viewModel.refreshPage();
        });

        binding.advancedSearchButton.setOnClickListener(v -> {
            AdvancedSearchBottomSheet sheet =
                    new AdvancedSearchBottomSheet();

            sheet.setFilterListener((params) -> {
                viewModel.setAdvancedSearchParameters(params);
            });

            sheet.show(getParentFragmentManager(), "AdvancedSheet");
        });

        binding.nextPage.setOnClickListener(v -> {
            viewModel.nextPage();
        });

        binding.previousPage.setOnClickListener(v -> {
            viewModel.previousPage();
        });

        viewModel.getGames().observe(getViewLifecycleOwner(), this::updateGamesInfo);

        // this calls updatePageInfo twice if both maxPage and currentPage change
        // but that only happens if new games are added while browsing which is unlikely
        viewModel.getCurrentPage().observe(getViewLifecycleOwner(), this::updatePageInfo);
        viewModel.getPageAmount().observe(getViewLifecycleOwner(), this::updatePageInfo);

        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            binding.nextPage.setEnabled(!isLoading);
            binding.previousPage.setEnabled(!isLoading);
            binding.searchButton.setEnabled(!isLoading);
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
            binding.previousPage.setEnabled(false);
            binding.nextPage.setEnabled(false);
            binding.searchButton.setEnabled(false);
        });

        binding.gameListRetryButton.setOnClickListener(v -> {
            binding.gameListErrorContainer.setVisibility(View.GONE);
            binding.gameRecycler.setVisibility(View.VISIBLE);
            viewModel.refreshPage();
        });
    }

    public void updateGamesInfo(List<ListedGameEntity> games) {
        gameAdapter.setData(games);
        String pageDisplay = "Page " + viewModel.getCurrentPage();
        binding.pageDisplay.setText(pageDisplay);
    }

    public void updatePageInfo(Integer ignored) {
        Integer currentPage = viewModel.getCurrentPage().getValue();
        Integer pageAmount = viewModel.getPageAmount().getValue();

        String pageAmountDisplay = pageAmount == null ? "?" : pageAmount.toString();
        String currentPageDisplay = currentPage == null ? "?" : currentPage.toString();

        String pageDisplayText = currentPageDisplay + " / " + pageAmountDisplay;
        binding.pageDisplay.setText(pageDisplayText);
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
