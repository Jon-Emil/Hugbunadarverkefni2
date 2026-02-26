package is.hbv601g.gamecatalog.helpers;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import is.hbv601g.gamecatalog.entities.game.ListedGameEntity;
import is.hbv601g.gamecatalog.entities.game.SimpleGameEntity;
import is.hbv601g.gamecatalog.entities.genre.ListedGenreEntity;
import is.hbv601g.gamecatalog.entities.genre.SimpleGenreEntity;
import is.hbv601g.gamecatalog.entities.review.SimpleReviewEntity;
import is.hbv601g.gamecatalog.entities.user.SimpleUserEntity;

public class JSONArrayHelper {

    public static List<ListedGameEntity> getListedGames(JSONArray jsonArray) throws JSONException {
        List<ListedGameEntity> fetchedGames = new ArrayList<>();

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject json = jsonArray.getJSONObject(i);
            long id = json.getLong("id");
            String title = json.getString("title");
            String description = json.getString("description");
            String releaseDate = json.getString("releaseDate");
            float price = (float) json.getDouble("price");
            String coverImage = json.getString("coverImage");
            String developer = json.getString("developer");
            String publisher = json.getString("publisher");
            int reviewAmount = json.getInt("reviewAmount");

            Float averageRating = null;
            if (!json.isNull("averageRating")) {
                averageRating = (float) json.getDouble("averageRating");
            }

            int favoriteAmount = json.getInt("favoriteAmount");
            int wantToPlayAmount = json.getInt("wantToPlayAmount");
            int havePlayedAmount = json.getInt("havePlayedAmount");

            JSONArray genresJson = json.getJSONArray("genres");
            List<SimpleGenreEntity> genres = JSONArrayHelper.makeGenreList(genresJson);

            ListedGameEntity fetchedGame = new ListedGameEntity(
                    id, title, description, releaseDate, price, coverImage,
                    developer, publisher, reviewAmount, averageRating,
                    favoriteAmount, wantToPlayAmount, havePlayedAmount, genres
            );
            fetchedGames.add(fetchedGame);
        }
        return fetchedGames;
    }

    public static List<ListedGenreEntity> getListedGenres(JSONArray jsonArray) throws JSONException {
        List<ListedGenreEntity> fetchedGenres = new ArrayList<>();

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject json = jsonArray.getJSONObject(i);
            long id = json.getLong("id");
            String title = json.getString("title");
            String description = json.getString("description");
            int gameAmount = json.getInt("gameAmount");

            fetchedGenres.add(new ListedGenreEntity(id, title, description, gameAmount));
        }
        return fetchedGenres;
    }

    /**
     * Parses a JSONArray of ReferencedGameDTO objects.
     * Fields confirmed from backend ReferencedGameDTO:
     *   id, title, description, releaseDate, price, coverImage,
     *   developer, publisher, averageRating (nullable)
     */
    public static List<SimpleGameEntity> makeGameList(JSONArray array) {
        try {
            List<SimpleGameEntity> games = new ArrayList<>();

            for (int i = 0; i < array.length(); i++) {
                JSONObject g = array.getJSONObject(i);

                // averageRating is Float (nullable) in the backend entity
                Float averageRating = null;
                if (!g.isNull("averageRating")) {
                    averageRating = (float) g.getDouble("averageRating");
                }

                games.add(new SimpleGameEntity(
                        g.getLong("id"),
                        g.optString("title", ""),
                        g.optString("description", ""),
                        g.optString("releaseDate", ""),
                        (float) g.optDouble("price", 0.0),
                        g.optString("coverImage", ""),
                        g.optString("developer", ""),
                        g.optString("publisher", ""),
                        averageRating
                ));
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
                JSONObject g = array.getJSONObject(i);
                genres.add(new SimpleGenreEntity(
                        g.getLong("id"),
                        g.getString("title"),
                        g.getString("description"),
                        g.getInt("gameAmount")
                ));
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
                JSONObject u = array.getJSONObject(i);
                users.add(new SimpleUserEntity(
                        u.getLong("id"),
                        u.getString("username"),
                        u.getString("profilePictureURL"),
                        u.getString("description")
                ));
            }
            return users;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Parses a JSONArray of ReferencedReviewDTO objects.
     * Fields confirmed from backend ReferencedReviewDTO:
     *   id, rating, text, title, author (String), gameTitle (String)
     */
    public static List<SimpleReviewEntity> makeReviewList(JSONArray array) {
        try {
            List<SimpleReviewEntity> reviews = new ArrayList<>();

            for (int i = 0; i < array.length(); i++) {
                JSONObject r = array.getJSONObject(i);
                reviews.add(new SimpleReviewEntity(
                        r.getLong("id"),
                        r.getInt("rating"),
                        r.optString("text", ""),
                        r.optString("title", ""),
                        r.optString("author", ""),
                        r.optString("gameTitle", "")
                ));
            }
            return reviews;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}