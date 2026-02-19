package is.hbv601g.gamecatalog.pages.all_games;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import is.hbv601g.gamecatalog.entities.game.ListedGameEntity;
import is.hbv601g.gamecatalog.entities.genre.SimpleGenreEntity;
import is.hbv601g.gamecatalog.helpers.JSONArrayHelper;
import is.hbv601g.gamecatalog.services.GameService;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class AllGamesViewModel extends ViewModel {

    private final MutableLiveData<List<ListedGameEntity>> games = new MutableLiveData<>();
    private GameService gameService;
    private int currentPage = 1;

    public LiveData<List<ListedGameEntity>> getGames() {
        return games;
    }

    public void init(GameService gameService) {
        this.gameService = gameService;
        if (games.getValue() == null) {
            fetchGames(currentPage);
        }
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void loadPage(int pageNr) {
        currentPage = pageNr;
        fetchGames(pageNr);
    }

    public void nextPage() {
        loadPage(currentPage + 1);
    }

    public void previousPage() {
        if (currentPage > 1) {
            loadPage(currentPage - 1);
        }
    }

    private void fetchGames(int pageNr) {
        gameService.getAllGames(pageNr, "title", false, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    String body = response.body().string();
                    JSONObject json = new JSONObject(body);
                    JSONArray jsonArray = json.getJSONArray("data");
                    List<ListedGameEntity> fetchedGames = new ArrayList<>();

                    for (int i = 0; i < jsonArray.length(); i++) {
                        json = jsonArray.getJSONObject(i);
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
                    games.postValue(fetchedGames);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
