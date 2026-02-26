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
            float price = (float) json.getDouble("price"); // no getFloat so casting is needed
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
                    id,
                    title,
                    description,
                    releaseDate,
                    price,
                    coverImage,
                    developer,
                    publisher,
                    reviewAmount,
                    averageRating,
                    favoriteAmount,
                    wantToPlayAmount,
                    havePlayedAmount,
                    genres
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

            ListedGenreEntity fetchedGenre = new ListedGenreEntity(
                    id,
                    title,
                    description,
                    gameAmount
            );
            fetchedGenres.add(fetchedGenre);
        }
        return fetchedGenres;
    }

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
