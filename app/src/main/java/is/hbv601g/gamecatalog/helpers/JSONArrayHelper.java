package is.hbv601g.gamecatalog.helpers;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import is.hbv601g.gamecatalog.entities.game.SimpleGameEntity;
import is.hbv601g.gamecatalog.entities.genre.SimpleGenreEntity;
import is.hbv601g.gamecatalog.entities.review.SimpleReviewEntity;
import is.hbv601g.gamecatalog.entities.user.SimpleUserEntity;

public class JSONArrayHelper {

    public static List<SimpleGameEntity> makeGameList(JSONArray array) {
        try {
            List<SimpleGameEntity> games = new ArrayList<>();

            for (int i = 0; i < array.length(); i++) {
                JSONObject gameJson = array.getJSONObject(i);

                SimpleGameEntity genre = new SimpleGameEntity(
                        gameJson.getLong("id"),
                        gameJson.getString("title"),
                        gameJson.getString("description"),
                        gameJson.getString("releaseDate"),
                        (float) gameJson.getDouble("price"), // idk how to do this without casting or if this works correctly
                        gameJson.getString("coverImage"),
                        gameJson.getString("developer"),
                        gameJson.getString("publisher"),
                        (float) gameJson.getDouble("averageRating") // this can be null so needs to be Float not float idk how
                );

                games.add(genre);
            }

            return games;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static List<SimpleGenreEntity> makeGenreList(JSONArray array) {
        try {
            List<SimpleGenreEntity> genres = new ArrayList<>();

            for (int i = 0; i < array.length(); i++) {
                JSONObject genreJson = array.getJSONObject(i);

                SimpleGenreEntity genre = new SimpleGenreEntity(
                        genreJson.getLong("id"),
                        genreJson.getString("title"),
                        genreJson.getString("description"),
                        genreJson.getInt("gameAmount")
                );

                genres.add(genre);
            }

            return genres;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static List<SimpleUserEntity> makeUserList(JSONArray array) {
        try {
            List<SimpleUserEntity> users = new ArrayList<>();

            for (int i = 0; i < array.length(); i++) {
                JSONObject userJson = array.getJSONObject(i);

                SimpleUserEntity user = new SimpleUserEntity(
                        userJson.getLong("id"),
                        userJson.getString("username"),
                        userJson.getString("profilePictureURL"),
                        userJson.getString("description")
                );

                users.add(user);
            }

            return users;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static List<SimpleReviewEntity> makeReviewList(JSONArray array) {
        try {
            List<SimpleReviewEntity> reviews = new ArrayList<>();

            for (int i = 0; i < array.length(); i++) {
                JSONObject reviewJson = array.getJSONObject(i);

                SimpleReviewEntity review = new SimpleReviewEntity(
                        reviewJson.getLong("id"),
                        reviewJson.getInt("rating"),
                        reviewJson.getString("text"),
                        reviewJson.getString("title"),
                        reviewJson.getString("author"),
                        reviewJson.getString("gameTitle")
                );

                reviews.add(review);
            }

            return reviews;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
