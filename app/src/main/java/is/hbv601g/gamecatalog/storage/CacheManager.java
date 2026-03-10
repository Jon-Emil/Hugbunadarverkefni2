package is.hbv601g.gamecatalog.storage;

import android.content.Context;

import java.io.File;
import java.util.List;

public class CacheManager {
    private final Context context;

    public CacheManager(Context context) {
        this.context = context;
    }

    //Save game details to cache
    public void cacheGame(String gameId, String json){

    }

    //Get game details from cache
    public String getCachedGame(String gameId){
        return "testCahceText";
    }

    //Get all game files from cache
    public List<File> getAllCachedGames(){
        return null;
    }

}
