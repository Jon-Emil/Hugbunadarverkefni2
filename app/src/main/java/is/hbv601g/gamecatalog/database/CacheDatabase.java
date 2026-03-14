package is.hbv601g.gamecatalog.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import is.hbv601g.gamecatalog.dao.CachedGameDao;
import is.hbv601g.gamecatalog.entities.game.CachedGame;

@Database(entities = {CachedGame.class}, version = 1)
public abstract class CacheDatabase extends RoomDatabase {
    public abstract CachedGameDao cachedGameDao();
}
