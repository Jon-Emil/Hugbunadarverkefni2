package is.hbv601g.gamecatalog.pages.all_games;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import is.hbv601g.gamecatalog.entities.game.DetailedGameEntity;
import is.hbv601g.gamecatalog.entities.game.ListedGameEntity;
import is.hbv601g.gamecatalog.entities.genre.SimpleGenreEntity;
import is.hbv601g.gamecatalog.helpers.JSONArrayHelper;
import is.hbv601g.gamecatalog.helpers.PaginatedCallback;
import is.hbv601g.gamecatalog.helpers.ServiceCallback;
import is.hbv601g.gamecatalog.services.GameService;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class AllGamesViewModel extends ViewModel {

    private final MutableLiveData<List<ListedGameEntity>> games = new MutableLiveData<>();
    private final MutableLiveData<Integer> pageAmount = new MutableLiveData<>();
    private final MutableLiveData<Integer> currentPage = new MutableLiveData<>(1);
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private GameService gameService;

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

    public void init(GameService gameService) {
        this.gameService = gameService;
        if (games.getValue() == null) {
            Integer page = currentPage.getValue();
            fetchGames(page == null ? 1 : page);
        }
    }

    public void loadPage(int pageNr) {
        currentPage.postValue(pageNr);
        fetchGames(pageNr);
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

    public void refreshPage() {
        Integer page = currentPage.getValue();
        fetchGames(page == null ? 1 : page);
    }

    private void fetchGames(int pageNr) {
        isLoading.postValue(true);
        gameService.getAllGames(pageNr, "title", false, new PaginatedCallback<ListedGameEntity>() {
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
}
