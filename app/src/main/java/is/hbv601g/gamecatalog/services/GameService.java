package is.hbv601g.gamecatalog.services;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import java.util.List;

import is.hbv601g.gamecatalog.entities.game.DetailedGameEntity;
import is.hbv601g.gamecatalog.entities.game.ListedGameEntity;
import is.hbv601g.gamecatalog.helpers.JSONArrayHelper;
import is.hbv601g.gamecatalog.helpers.JSONObjectHelper;
import is.hbv601g.gamecatalog.helpers.ServiceCallback;
import okhttp3.Call;
import okhttp3.Callback;
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
                    List<ListedGameEntity> fetchedGames = JSONArrayHelper.getListedGame(jsonArray);
                    callback.onSuccess(fetchedGames);
                } catch (Exception e) {
                    callback.onError(e);
                }
            }
        });
    }

    public void getSearchedGames(String gameTitleParam, int page, String sortBy, boolean sortReverse, ServiceCallback<List<ListedGameEntity>> callback) {
        String pageString = String.valueOf(page);
        String reverseString = sortReverse ? "true" : "false";
        String url = "/games/search?title=" + gameTitleParam + "&pageNr=" + pageString + "&sortBy=" + sortBy + "&sortReverse=" + reverseString;

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
                    List<ListedGameEntity> fetchedGames = JSONArrayHelper.getListedGame(jsonArray);
                    callback.onSuccess(fetchedGames);
                } catch (Exception e) {
                    callback.onError(e);
                }
            }
        });
    }
}
