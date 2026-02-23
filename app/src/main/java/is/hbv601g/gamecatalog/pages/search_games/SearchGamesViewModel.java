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

import is.hbv601g.gamecatalog.entities.game.ListedGameEntity;
import is.hbv601g.gamecatalog.entities.genre.ListedGenreEntity;
import is.hbv601g.gamecatalog.entities.genre.SimpleGenreEntity;
import is.hbv601g.gamecatalog.helpers.JSONArrayHelper;
import is.hbv601g.gamecatalog.helpers.ServiceCallback;
import is.hbv601g.gamecatalog.services.GameService;
import is.hbv601g.gamecatalog.services.GenreService;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class SearchGamesViewModel extends ViewModel {

    private final MutableLiveData<List<ListedGameEntity>> games = new MutableLiveData<>();
    private GameService gameService;
    private GenreService genreService;
    private int currentPage = 1;
    private String gameTitleParam = "";
    private boolean sortReverse = false;
    private String sortBy = "title";
    private final MutableLiveData<List<ListedGenreEntity>> genres =
            new MutableLiveData<>(new ArrayList<>());

    public LiveData<List<ListedGameEntity>> getGames() {
        return games;
    }

    public void init(GameService gameService, GenreService genreService) {
        this.gameService = gameService;
        this.genreService = genreService;
        if (games.getValue() == null) {
            fetchGames(currentPage);
        }
        if (genres.getValue().isEmpty()) {
            fetchGenres();
        }
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public LiveData<List<ListedGenreEntity>> getGenres() {
        return genres;
    }

    public void loadPage(int pageNr) {
        currentPage = pageNr;
        fetchGames(pageNr);
    }

    public void nextPage() {
        loadPage(currentPage + 1);
    }

    public void previousPage() {
        if (currentPage > 1) {
            loadPage(currentPage - 1);
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

    private void fetchGames(int pageNr) {
        gameService.getSearchedGames(gameTitleParam, pageNr, sortBy, sortReverse, new ServiceCallback<List<ListedGameEntity>>() {
            @Override
            public void onError(Exception e) {
                e.printStackTrace();
            }

            @Override
            public void onSuccess(List<ListedGameEntity> fetchedGames) {
                games.postValue(fetchedGames);
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
