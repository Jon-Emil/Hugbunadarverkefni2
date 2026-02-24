package is.hbv601g.gamecatalog.helpers;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import is.hbv601g.gamecatalog.entities.extras.LogInCredentials;
import is.hbv601g.gamecatalog.entities.game.DetailedGameEntity;
import is.hbv601g.gamecatalog.entities.genre.SimpleGenreEntity;
import is.hbv601g.gamecatalog.entities.review.SimpleReviewEntity;
import is.hbv601g.gamecatalog.entities.user.SimpleUserEntity;

public class JSONObjectHelper {

    public static DetailedGameEntity getDetailedGame(JSONObject json) throws JSONException {
        long id = json.getLong("id");
        String title = json.getString("title");
        String description = json.getString("description");
        String releaseDate = json.getString("releaseDate");
        float price = (float) json.getDouble("price"); // no getFloat so casting is needed
        String coverImage = json.getString("coverImage");
        String developer = json.getString("developer");
        String publisher = json.getString("publisher");

        Float averageRating = null;
        if (!json.isNull("averageRating")) {
            averageRating = (float) json.getDouble("averageRating");
        }

        JSONArray genresJson = json.getJSONArray("genres");
        List<SimpleGenreEntity> genres = JSONArrayHelper.makeGenreList(genresJson);

        JSONArray reviewsJson = json.getJSONArray("reviews");
        List<SimpleReviewEntity> reviews = JSONArrayHelper.makeReviewList(reviewsJson);

        JSONArray favoriteOfJson = json.getJSONArray("favoriteOf");
        List<SimpleUserEntity> favoriteOf = JSONArrayHelper.makeUserList(favoriteOfJson);

        JSONArray wantToPlayJson = json.getJSONArray("wantToPlay");
        List<SimpleUserEntity> wantToPlay = JSONArrayHelper.makeUserList(wantToPlayJson);

        JSONArray havePlayedJson = json.getJSONArray("havePlayed");
        List<SimpleUserEntity> havePlayed = JSONArrayHelper.makeUserList(havePlayedJson);

        DetailedGameEntity fetchedGame = new DetailedGameEntity(
                id,
                title,
                description,
                releaseDate,
                price,
                coverImage,
                developer,
                publisher,
                averageRating,
                genres,
                reviews,
                favoriteOf,
                wantToPlay,
                havePlayed
        );
        return fetchedGame;
    }

    //code from auto complete
    public static String convertCredentialsToJsonString(LogInCredentials credentials) {
        String json = "";
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("email", credentials.getEmail());
            jsonObject.put("password", credentials.getPassword());
            json = jsonObject.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;

    }
}
