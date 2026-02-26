package is.hbv601g.gamecatalog.services;

import android.os.Handler;
import android.os.Looper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import is.hbv601g.gamecatalog.entities.game.SimpleGameEntity;
import is.hbv601g.gamecatalog.entities.review.SimpleReviewEntity;
import is.hbv601g.gamecatalog.entities.user.DetailedUserEntity;
import is.hbv601g.gamecatalog.helpers.JSONArrayHelper;
import is.hbv601g.gamecatalog.helpers.ServiceCallback;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class UserService {

    private final NetworkService networkService;

    public UserService(NetworkService networkService) {
        this.networkService = networkService;
    }

    public void getMyProfile(ServiceCallback<DetailedUserEntity> callback) {
        String url = "/users/me";

        networkService.getRequest(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                new Handler(Looper.getMainLooper()).post(() ->
                        callback.onError(e)
                );
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    String body = response.body().string();

                    if (!response.isSuccessful()) {
                        final int code = response.code();
                        new Handler(Looper.getMainLooper()).post(() ->
                                callback.onError(new Exception("Request failed with code: " + code))
                        );
                        return;
                    }

                    JSONObject json = new JSONObject(body);
                    JSONObject data = json.getJSONArray("data").getJSONObject(0);

                    Long id = data.getLong("id");
                    String username = data.getString("username");
                    String email = data.optString("email", "");
                    String profilePictureURL = data.optString("profilePictureURL", "");
                    String description = data.optString("description", "");

                    JSONArray reviewsJson = data.optJSONArray("reviews");
                    List<SimpleReviewEntity> reviews = reviewsJson != null
                            ? JSONArrayHelper.makeReviewList(reviewsJson)
                            : new java.util.ArrayList<>();

                    JSONArray favoritesJson = data.optJSONArray("favoriteGames");
                    List<SimpleGameEntity> favoriteGames = favoritesJson != null
                            ? JSONArrayHelper.makeGameList(favoritesJson)
                            : new java.util.ArrayList<>();

                    JSONArray wantToPlayJson = data.optJSONArray("wantToPlayGames");
                    List<SimpleGameEntity> wantToPlayGames = wantToPlayJson != null
                            ? JSONArrayHelper.makeGameList(wantToPlayJson)
                            : new java.util.ArrayList<>();

                    JSONArray havePlayedJson = data.optJSONArray("havePlayedGames");
                    List<SimpleGameEntity> havePlayedGames = havePlayedJson != null
                            ? JSONArrayHelper.makeGameList(havePlayedJson)
                            : new java.util.ArrayList<>();

                    DetailedUserEntity user = new DetailedUserEntity(
                            id, username, email, profilePictureURL, description,
                            reviews, favoriteGames, wantToPlayGames, havePlayedGames
                    );

                    new Handler(Looper.getMainLooper()).post(() ->
                            callback.onSuccess(user)
                    );

                } catch (Exception e) {
                    new Handler(Looper.getMainLooper()).post(() ->
                            callback.onError(e)
                    );
                }
            }
        });
    }
}