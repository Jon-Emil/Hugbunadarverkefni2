package is.hbv601g.gamecatalog.pages.offline_specific_game;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

import is.hbv601g.gamecatalog.entities.game.DetailedGameEntity;
import is.hbv601g.gamecatalog.storage.CacheManager;

public class OfflineSpecificGameViewModel extends ViewModel {
    private final MutableLiveData<DetailedGameEntity> game = new MutableLiveData<>();
    private CacheManager cacheManager;

    public LiveData<DetailedGameEntity> getGame() {
        return game;
    }

    public void init(CacheManager cacheManager, long gameId) {
        if (game.getValue() != null) {
            return;
        }

        this.cacheManager = cacheManager;
        fetchCachedGame(gameId);
    }

    private void fetchCachedGame(long gameId) {
        //IMPLEMENT CACHE FETCH LOGIC HERE FROM CACHE MANAGER
        String game = cacheManager.getCachedGame("1");

        //Code from ChatGPT to create dummy data to test offline functionality and fragments
        DetailedGameEntity dummyGame = new DetailedGameEntity(
                gameId,
                "Offline Game #" + gameId,
                "Cached game description",
                "2020-01-01",
                19.99f,
                "",
                "Offline Dev",
                "Offline Publisher",
                4.5f,
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>()
        );
    }
}
