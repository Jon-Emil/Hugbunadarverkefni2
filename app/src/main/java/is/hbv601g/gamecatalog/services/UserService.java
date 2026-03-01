package is.hbv601g.gamecatalog.services;

import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import is.hbv601g.gamecatalog.entities.game.SimpleGameEntity;
import is.hbv601g.gamecatalog.entities.review.SimpleReviewEntity;
import is.hbv601g.gamecatalog.entities.user.DetailedUserEntity;
import is.hbv601g.gamecatalog.entities.user.SimpleUserEntity;
import is.hbv601g.gamecatalog.helpers.JSONArrayHelper;
import is.hbv601g.gamecatalog.helpers.JSONObjectHelper;
import is.hbv601g.gamecatalog.helpers.ServiceCallback;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class UserService {

    private final NetworkService networkService;

    public UserService(NetworkService networkService) {
        this.networkService = networkService;
    }

    // Get own full profile (used by PersonalProfileFragment)
    public void getMyProfile(ServiceCallback<DetailedUserEntity> callback) {
        String url = "/users/profile";

        networkService.getRequest(url, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                new Handler(Looper.getMainLooper()).post(() ->
                        callback.onError(e)
                );
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                try {
                    String body = response.body().string();

                    if (!response.isSuccessful()) {
                        final int code = response.code();
                        new Handler(Looper.getMainLooper()).post(() ->
                                callback.onError(new Exception("Request failed with code: " + code))
                        );
                        return;
                    }

                    // Backend returns { status, message, data: [ MyselfUserDTO ] }
                    JSONObject json = new JSONObject(body);
                    JSONObject data = json.getJSONArray("data").getJSONObject(0);

                    Long id = data.getLong("id");
                    String username = data.optString("username", "");
                    String email = data.optString("email", "");

                    // better null string handling of profile pic url, inspired by Claude.
                    String profilePictureURL = data.isNull("profilePictureURL")?"": data.optString("profilePictureURL","");

                    String description = data.optString("description", "");

                    // Follower / following counts from follows / followedBy arrays
                    int followingCount = data.optJSONArray("follows") != null
                            ? data.getJSONArray("follows").length() : 0;
                    int followerCount = data.optJSONArray("followedBy") != null
                            ? data.getJSONArray("followedBy").length() : 0;

                    // Field names confirmed from MyselfUserDTO: favorites, wantsToPlay, hasPlayed, reviews
                    JSONArray favoritesJson = data.optJSONArray("favorites");
                    List<SimpleGameEntity> favoriteGames = favoritesJson != null
                            ? JSONArrayHelper.makeGameList(favoritesJson)
                            : new java.util.ArrayList<>();

                    JSONArray wantToPlayJson = data.optJSONArray("wantsToPlay");
                    List<SimpleGameEntity> wantToPlayGames = wantToPlayJson != null
                            ? JSONArrayHelper.makeGameList(wantToPlayJson)
                            : new java.util.ArrayList<>();

                    JSONArray hasPlayedJson = data.optJSONArray("hasPlayed");
                    List<SimpleGameEntity> havePlayedGames = hasPlayedJson != null
                            ? JSONArrayHelper.makeGameList(hasPlayedJson)
                            : new java.util.ArrayList<>();

                    JSONArray reviewsJson = data.optJSONArray("reviews");
                    List<SimpleReviewEntity> reviews = reviewsJson != null
                            ? JSONArrayHelper.makeReviewList(reviewsJson)
                            : new java.util.ArrayList<>();

                    DetailedUserEntity user = new DetailedUserEntity(
                            id, username, email, profilePictureURL, description,
                            followerCount, followingCount,
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

    // Get own simple profile
    public void getUserOwnProfile(ServiceCallback<SimpleUserEntity> callback) {
        String url = "/users/profile";

        networkService.getRequest(url, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                // better error handling inspired by Claude.
                new Handler(Looper.getMainLooper()).post(() -> callback.onError(e));
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                try {
                    String body = response.body().string();
                    JSONObject json = new JSONObject(body).getJSONArray("data").getJSONObject(0);
                    SimpleUserEntity user = JSONObjectHelper.getSimpleUser(json);
                    // better handling inspired by Claude.
                    new Handler(Looper.getMainLooper()).post(() -> callback.onSuccess(user));
                } catch (Exception e) {
                    // better error handling inspired by Claude.
                    new Handler(Looper.getMainLooper()).post(() -> callback.onError(e));
                }
            }
        });
    }

    // Modify own profile
    public void modifyProfile(SimpleUserEntity newProfile, ServiceCallback<Boolean> callback) {
        String url = "/users";

        try {
            org.json.JSONObject userInfoJson = new org.json.JSONObject();
            userInfoJson.put("username", newProfile.getUsername());
            userInfoJson.put("description", newProfile.getDescription());

            okhttp3.MultipartBody body = new okhttp3.MultipartBody.Builder()
                    .setType(okhttp3.MultipartBody.FORM)
                    .addFormDataPart("userInfo", null,
                            okhttp3.RequestBody.create(
                                    userInfoJson.toString(),
                                    okhttp3.MediaType.get("application/json; charset=utf-8")))
                    .build();

            networkService.patchMultipartRequest(url, body, new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    // better error handling inspired by Claude.
                    new Handler(Looper.getMainLooper()).post(() -> callback.onError(e));
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    // better error handling inspired by Claude.
                    boolean success = response.isSuccessful();
                    new Handler(Looper.getMainLooper()).post(() -> callback.onSuccess(success));
                }
            });
        } catch (Exception e) {
            // better error handling inspired by Claude.
            new Handler(Looper.getMainLooper()).post(() -> callback.onError(e));
        }
    }

    // Delete own account
    public void deleteAccount(ServiceCallback<Boolean> callback) {
        String url = "/users";

        networkService.deleteRequest(url, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                // better error handling inspired by Claude.
                new Handler(Looper.getMainLooper()).post(() -> callback.onError(e));
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                // better error handling inspired by Claude.
                boolean success = response.isSuccessful();
                new Handler(Looper.getMainLooper()).post(() -> callback.onSuccess(success));
            }
        });
    }
}