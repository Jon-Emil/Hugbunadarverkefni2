package is.hbv601g.gamecatalog.services;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import is.hbv601g.gamecatalog.entities.genre.ListedGenreEntity;
import is.hbv601g.gamecatalog.helpers.JSONArrayHelper;
import is.hbv601g.gamecatalog.helpers.ServiceCallback;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class GenreService {

    private final NetworkService networkService;

    public GenreService(NetworkService networkService) {
        this.networkService = networkService;
    }

    public void getAllGenres(ServiceCallback<List<ListedGenreEntity>> callback) {
        String url = "/genres?&perPage=1000"; // genres should not have been paginated

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
                    List<ListedGenreEntity> fetchedGenres = JSONArrayHelper.getListedGenres(jsonArray);
                    callback.onSuccess(fetchedGenres);
                } catch (Exception e) {
                    callback.onError(e);
                }
            }
        });
    }

}
