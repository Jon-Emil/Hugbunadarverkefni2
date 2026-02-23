package is.hbv601g.gamecatalog.services;

import android.net.Uri;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import java.util.List;

import is.hbv601g.gamecatalog.entities.extras.AdvancedSearchParameters;
import is.hbv601g.gamecatalog.entities.game.DetailedGameEntity;
import is.hbv601g.gamecatalog.entities.game.ListedGameEntity;
import is.hbv601g.gamecatalog.helpers.JSONArrayHelper;
import is.hbv601g.gamecatalog.helpers.JSONObjectHelper;
import is.hbv601g.gamecatalog.helpers.ServiceCallback;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Response;

public class GameService {

    private final NetworkService networkService;

    public GameService(NetworkService networkService) {
        this.networkService = networkService;
    }

    public void getSpecificGame(long gameID, ServiceCallback<DetailedGameEntity> callback) {
        String idString = String.valueOf(gameID);
        String url = "/games/" + idString;

        networkService.getRequest(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onError(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    String body = response.body().string();
                    JSONObject json = new JSONObject(body);
                    json = json.getJSONArray("data").getJSONObject(0);
                    DetailedGameEntity fetchedGame = JSONObjectHelper.getDetailedGame(json);
                    callback.onSuccess(fetchedGame);
                } catch (Exception e) {
                    callback.onError(e);
                }
            }
        });
    }

    public void getAllGames(int page , String sortBy, boolean sortReverse, ServiceCallback<List<ListedGameEntity>> callback) {
        String pageString = String.valueOf(page);
        String reverseString = sortReverse ? "true" : "false";
        String url = "/games?pageNr=" + pageString + "&sortBy=" + sortBy + "&sortReverse=" + reverseString;

        networkService.getRequest(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onError(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    String body = response.body().string();
                    JSONObject json = new JSONObject(body);
                    JSONArray jsonArray = json.getJSONArray("data");
                    List<ListedGameEntity> fetchedGames = JSONArrayHelper.getListedGames(jsonArray);
                    callback.onSuccess(fetchedGames);
                } catch (Exception e) {
                    callback.onError(e);
                }
            }
        });
    }

    public void getSearchedGames(
            String gameTitleParam,
            AdvancedSearchParameters advancedParams,
            int page,
            String sortBy,
            boolean sortReverse,
            ServiceCallback<List<ListedGameEntity>> callback
    ) {
        // safe way to make urls with user parameters
        Uri.Builder builder = new Uri.Builder()
                .path("games/search")
                .appendQueryParameter("title", gameTitleParam)
                .appendQueryParameter("pageNr", String.valueOf(page))
                .appendQueryParameter("sortBy", sortBy)
                .appendQueryParameter("sortReverse", String.valueOf(sortReverse));

        if (advancedParams.getMinPrice() != null) {
            builder.appendQueryParameter("minPrice",
                    String.valueOf(advancedParams.getMinPrice()));
        }

        if (advancedParams.getMaxPrice() != null) {
            builder.appendQueryParameter("maxPrice",
                    String.valueOf(advancedParams.getMaxPrice()));
        }

        if (advancedParams.getReleasedAfter() != null &&
                !advancedParams.getReleasedAfter().isEmpty()) {
            builder.appendQueryParameter("releasedAfter",
                    advancedParams.getReleasedAfter());
        }

        if (advancedParams.getReleasedBefore() != null &&
                !advancedParams.getReleasedBefore().isEmpty()) {
            builder.appendQueryParameter("releasedBefore",
                    advancedParams.getReleasedBefore());
        }

        if (advancedParams.getDeveloper() != null &&
                !advancedParams.getDeveloper().isEmpty()) {
            builder.appendQueryParameter("developer",
                    advancedParams.getDeveloper());
        }

        if (advancedParams.getPublisher() != null &&
                !advancedParams.getPublisher().isEmpty()) {
            builder.appendQueryParameter("publisher",
                    advancedParams.getPublisher());
        }

        if (advancedParams.getGenres() != null &&
                !advancedParams.getGenres().isEmpty()) {
            for (String genre : advancedParams.getGenres()) {
                builder.appendQueryParameter("genres", genre);
            }
        }

        String finalUrl = "/" + builder.build().toString();

        networkService.getRequest(finalUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onError(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    String body = response.body().string();
                    JSONObject json = new JSONObject(body);
                    JSONArray jsonArray = json.getJSONArray("data");
                    List<ListedGameEntity> fetchedGames = JSONArrayHelper.getListedGames(jsonArray);
                    callback.onSuccess(fetchedGames);
                } catch (Exception e) {
                    callback.onError(e);
                }
            }
        });
    }
}
