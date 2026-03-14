package is.hbv601g.gamecatalog.dao;


import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import is.hbv601g.gamecatalog.entities.game.CachedGame;

@Dao
public interface CachedGameDao {
    @Query("SELECT * FROM cachedgame")
    List<CachedGame> getAll();

    @Query("SELECT * FROM cachedgame WHERE gameId = :gameId")
    CachedGame getCachedGame(long gameId);

    @Insert
    void insert(CachedGame cachedGame);

    @Delete
    void delete(CachedGame cachedGame);

}
