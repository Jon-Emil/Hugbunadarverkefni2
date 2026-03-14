package is.hbv601g.gamecatalog.pages.offline_all_games;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

import is.hbv601g.gamecatalog.database.CacheDatabase;
import is.hbv601g.gamecatalog.entities.game.CachedGame;
import is.hbv601g.gamecatalog.entities.game.ListedGameEntity;
import is.hbv601g.gamecatalog.dao.CachedGameDao;

public class OfflineAllGamesViewModel extends ViewModel {
    private final MutableLiveData<List<CachedGame>> games = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    private CachedGameDao cachedGameDao;

    public LiveData<List<CachedGame>> getGames() {
        return games;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public void init(CacheDatabase database) {
        this.cachedGameDao = database.cachedGameDao();
        if (games.getValue() == null) {
            fetchCachedGames();
        }
    }

    public void refreshPage() {
        fetchCachedGames();
    }

    private void fetchCachedGames() {
        isLoading.postValue(true);
        //IMPLEMENT CACHE FETCH LOGIC HERE FROM ROOM
        //Run caching on background thread
        Executors.newSingleThreadExecutor().execute(() -> {
            List<CachedGame> fetchedGames = cachedGameDao.getAll();
            games.postValue(fetchedGames);
        });
        isLoading.postValue(false);
    }

}
