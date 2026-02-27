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
                    String profilePictureURL = data.optString("profilePictureURL", "");
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
                callback.onError(e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                try {
                    String body = response.body().string();
                    JSONObject json = new JSONObject(body).getJSONArray("data").getJSONObject(0);
                    SimpleUserEntity user = JSONObjectHelper.getSimpleUser(json);
                    callback.onSuccess(user);
                } catch (Exception e) {
                    callback.onError(e);
                }
            }
        });
    }

    // Modify own profile
    public void modifyProfile(SimpleUserEntity newProfile, android.net.Uri imageUri,
                              android.content.Context context, ServiceCallback<Boolean> callback) {
        String url = "/users";

        try {
            org.json.JSONObject userInfoJson = new org.json.JSONObject();
            userInfoJson.put("username", newProfile.getUsername());
            userInfoJson.put("description", newProfile.getDescription());

            okhttp3.MultipartBody.Builder builder = new okhttp3.MultipartBody.Builder()
                    .setType(okhttp3.MultipartBody.FORM)
                    .addFormDataPart("userInfo", null,
                            okhttp3.RequestBody.create(
                                    userInfoJson.toString(),
                                    okhttp3.MediaType.get("application/json; charset=utf-8")));

            // If a new profile picture was selected, attach it as a multipart image part
            if (imageUri != null) {
                java.io.InputStream inputStream = context.getContentResolver().openInputStream(imageUri);
                if (inputStream != null) {
                    java.io.ByteArrayOutputStream buffer = new java.io.ByteArrayOutputStream();
                    byte[] chunk = new byte[4096];
                    int bytesRead;
                    while ((bytesRead = inputStream.read(chunk)) != -1) {
                        buffer.write(chunk, 0, bytesRead);
                    }
                    inputStream.close();
                    byte[] imageBytes = buffer.toByteArray();
                    builder.addFormDataPart("profilePicture", "avatar.jpg",
                            okhttp3.RequestBody.create(imageBytes,
                                    okhttp3.MediaType.get("image/jpeg")));
                }
            }

            networkService.patchMultipartRequest(url, builder.build(), new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    callback.onError(e);
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    callback.onSuccess(response.isSuccessful());
                }
            });
        } catch (Exception e) {
            callback.onError(e);
        }
    }

    // Delete own account
    public void deleteAccount(ServiceCallback<Boolean> callback) {
        String url = "/users";

        networkService.deleteRequest(url, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                callback.onError(e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                callback.onSuccess(response.isSuccessful());
            }
        });
    }
}