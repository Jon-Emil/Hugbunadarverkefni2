package is.hbv601g.gamecatalog.pages.offline_all_games;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

import is.hbv601g.gamecatalog.entities.game.ListedGameEntity;
import is.hbv601g.gamecatalog.helpers.PaginatedCallback;
import is.hbv601g.gamecatalog.services.GameService;
import is.hbv601g.gamecatalog.storage.CacheManager;

public class OfflineAllGamesViewModel extends ViewModel {
    private final MutableLiveData<List<ListedGameEntity>> games = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    //CACHE SERVICE HERE INSTEAD OF GAME SERVICE
    private CacheManager cacheManager;

    public LiveData<List<ListedGameEntity>> getGames() {
        return games;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public void init(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
        if (games.getValue() == null) {
            fetchCachedGames();
        }
    }

    public void refreshPage() {
        fetchCachedGames();
    }

    private void fetchCachedGames() {
        //IMPLEMENT CACHE FETCH LOGIC HERE FROM CACHE MANAGER
        String game = cacheManager.getCachedGame("1");
        //Need to fetch all cached games and parse the Json, or however we will store them in cache,
        //into Game objects and add that list to games.postValue(...)


        //Code from ChatGPT to create dummy data to test offline functionality and fragments

        isLoading.postValue(true);

        List<ListedGameEntity> dummyGames = new ArrayList<>();

        for (int i = 1; i <= 6; i++) {
            dummyGames.add(new ListedGameEntity(
                    i,
                    "Offline Game " + i,
                    "Cached game description",
                    "2020-01-01",
                    19.99f,
                    "",
                    "Offline Dev",
                    "Offline Publisher",
                    100,
                    4.5f,
                    50,
                    30,
                    20,
                    new ArrayList<>()
            ));
        }

        games.postValue(dummyGames);
        isLoading.postValue(false);
    }

}
