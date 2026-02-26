package is.hbv601g.gamecatalog.pages.search_games;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import is.hbv601g.gamecatalog.entities.extras.AdvancedSearchParameters;
import is.hbv601g.gamecatalog.entities.game.ListedGameEntity;
import is.hbv601g.gamecatalog.entities.genre.ListedGenreEntity;
import is.hbv601g.gamecatalog.entities.genre.SimpleGenreEntity;
import is.hbv601g.gamecatalog.helpers.JSONArrayHelper;
import is.hbv601g.gamecatalog.helpers.PaginatedCallback;
import is.hbv601g.gamecatalog.helpers.ServiceCallback;
import is.hbv601g.gamecatalog.services.GameService;
import is.hbv601g.gamecatalog.services.GenreService;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class SearchGamesViewModel extends ViewModel {

    private final MutableLiveData<List<ListedGameEntity>> games = new MutableLiveData<>();
    private final MutableLiveData<List<ListedGenreEntity>> genres = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<Integer> pageAmount = new MutableLiveData<>();
    private final MutableLiveData<Integer> currentPage = new MutableLiveData<>(1);
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    private GameService gameService;
    private GenreService genreService;

    private boolean sortReverse = false;
    private String sortBy = "title";
    private String gameTitleParam = "";
    private AdvancedSearchParameters advancedSearchParameters = new AdvancedSearchParameters();

    public void init(GameService gameService, GenreService genreService) {
        this.gameService = gameService;
        this.genreService = genreService;
        if (games.getValue() == null) {
            Integer page = currentPage.getValue();
            fetchGames(page == null ? 1 : page);
        }
        if (genres.getValue() == null || genres.getValue().isEmpty()) {
            fetchGenres();
        }
    }

    public LiveData<List<ListedGameEntity>> getGames() {
        return games;
    }

    public LiveData<Integer> getPageAmount() {
        return pageAmount;
    }

    public LiveData<Integer> getCurrentPage() {
        return currentPage;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<List<ListedGenreEntity>> getGenres() {
        return genres;
    }

    public void loadPage(int pageNr) {
        currentPage.postValue(pageNr);
        fetchGames(pageNr);
    }

    public void refreshPage() {
        Integer page = currentPage.getValue();
        fetchGames(page == null ? 1 : page);
    }

    public void nextPage() {
        Integer page = currentPage.getValue();
        Integer lastPage = pageAmount.getValue();
        if (page != null && (lastPage == null || page < lastPage)) {
            loadPage(page + 1);
        }
    }

    public void previousPage() {
        Integer page = currentPage.getValue();
        if (page != null && page > 1) {
            loadPage(page - 1);
        }
    }

    public void setGameTitleParam(String newGameTitleParam) {
        gameTitleParam = newGameTitleParam;
    }

    public void setSortReverse(boolean sortReverse) {
        this.sortReverse = sortReverse;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public void setAdvancedSearchParameters(AdvancedSearchParameters params) {
        advancedSearchParameters = params;
    }

    public AdvancedSearchParameters getAdvancedSearchParameters() {
        return advancedSearchParameters;
    }

    private void fetchGames(int pageNr) {
        isLoading.postValue(true);
        gameService.getSearchedGames(gameTitleParam, advancedSearchParameters, pageNr, sortBy, sortReverse, new PaginatedCallback<ListedGameEntity>() {
            @Override
            public void onError(Exception e) {
                errorMessage.postValue("Couldn't Fetch Games");
            }

            @Override
            public void onSuccess(List<ListedGameEntity> fetchedGames, int newPageAmount) {
                games.postValue(fetchedGames);
                pageAmount.postValue(newPageAmount);
                isLoading.postValue(false);
            }
        });
    }

    private void fetchGenres() {
        genreService.getAllGenres(new ServiceCallback<List<ListedGenreEntity>>() {
            @Override
            public void onSuccess(List<ListedGenreEntity> result) {
                genres.postValue(result);
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
            }
        });
    }
}
