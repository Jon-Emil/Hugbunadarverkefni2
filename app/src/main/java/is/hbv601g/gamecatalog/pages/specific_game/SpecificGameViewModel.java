package is.hbv601g.gamecatalog.pages.specific_game;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import is.hbv601g.gamecatalog.entities.game.DetailedGameEntity;
import is.hbv601g.gamecatalog.entities.genre.SimpleGenreEntity;
import is.hbv601g.gamecatalog.entities.review.SimpleReviewEntity;
import is.hbv601g.gamecatalog.entities.user.SimpleUserEntity;
import is.hbv601g.gamecatalog.helpers.JSONArrayHelper;
import is.hbv601g.gamecatalog.services.GameService;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class SpecificGameViewModel extends ViewModel {

    private final MutableLiveData<DetailedGameEntity> game = new MutableLiveData<>();
    private GameService gameService;

    public LiveData<DetailedGameEntity> getGame() {
        return game;
    }

    public void init(GameService gameService, long gameId) {
        if (game.getValue() != null) {
            return;
        }

        this.gameService = gameService;
        fetchGame(gameId);
    }

    private void fetchGame(long gameId) {
        gameService.getSpecificGame(gameId, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    String body = response.body().string();
                    JSONObject json = new JSONObject(body);
                    json = json.getJSONArray("data").getJSONObject(0);

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
                    game.postValue(fetchedGame);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
