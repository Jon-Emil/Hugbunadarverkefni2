package is.hbv601g.gamecatalog.pages.offline_specific_game;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

import is.hbv601g.gamecatalog.database.CacheDatabase;
import is.hbv601g.gamecatalog.entities.game.CachedGame;
import is.hbv601g.gamecatalog.entities.game.DetailedGameEntity;
import is.hbv601g.gamecatalog.dao.CachedGameDao;

public class OfflineSpecificGameViewModel extends ViewModel {
    private final MutableLiveData<CachedGame> game = new MutableLiveData<>();
    private CachedGameDao cachedGameDao;

    public LiveData<CachedGame> getGame() {
        return game;
    }

    public void init(CacheDatabase database, long gameId) {
        if (game.getValue() != null) {
            return;
        }

        this.cachedGameDao = database.cachedGameDao();
        fetchCachedGame(gameId);
    }

    private void fetchCachedGame(long gameId) {
        //IMPLEMENT CACHE FETCH LOGIC HERE FROM ROOM
        CachedGame cachedGame = cachedGameDao.getCachedGame(gameId);

        game.postValue(cachedGame);
    }
}
