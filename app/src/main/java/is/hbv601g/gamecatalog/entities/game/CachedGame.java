package is.hbv601g.gamecatalog.entities.game;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;
import java.util.List;

@Entity
public class CachedGame {
    @PrimaryKey
    public long gameId;
    public String title;
    public String description;
    public float price;
    public String developer;
    public String publisher;
    public String releaseDate;
    public int reviewAmount;
    public Float averageRating;
    public int favoriteAmount;
    public int wantToPlayAmount;
    public int havePlayedAmount;
    public String genres;
    public long dateCached;
}