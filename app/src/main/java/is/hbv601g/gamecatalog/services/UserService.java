package is.hbv601g.gamecatalog.services;

import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
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
                new Handler(Looper.getMainLooper()).post(() -> callback.onError(e));
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
                    JSONObject data = new JSONObject(body).getJSONArray("data").getJSONObject(0);
                    // MyselfUserDTO includes email; pass it explicitly to the shared parser.
                    DetailedUserEntity user = parseUserData(data, data.optString("email", ""));

                    new Handler(Looper.getMainLooper()).post(() -> callback.onSuccess(user));

                } catch (Exception e) {
                    new Handler(Looper.getMainLooper()).post(() -> callback.onError(e));
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

    /* Fetches another user's public profile via GET /users/{userId}. */
    public void getOtherUserProfile(long userId, ServiceCallback<DetailedUserEntity> callback) {
        String url = "/users/" + userId;

        networkService.getRequest(url, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                new Handler(Looper.getMainLooper()).post(() -> callback.onError(e));
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

                    // Backend returns { status, message, data: [ NormalUserDTO ] }
                    // NormalUserDTO does not include email — pass null to the shared parser.
                    JSONObject data = new JSONObject(body).getJSONArray("data").getJSONObject(0);
                    DetailedUserEntity user = parseUserData(data, null);

                    new Handler(Looper.getMainLooper()).post(() -> callback.onSuccess(user));

                } catch (Exception e) {
                    new Handler(Looper.getMainLooper()).post(() -> callback.onError(e));
                }
            }
        });
    }

    /*
     * Fetches only the followers or following list for a user via GET /users/{userId}.
     *
     * UserListFragment only needs one of the two (follows or followedBy). Avoids parsing games and reviews entirely, so it's cheaper than calling getOtherUserProfile() and discarding most of the result.
     *
     * @param listType "followers" → returns followedBy array; "following" → returns follows array
     */
    public void getUserConnections(long userId, String listType,
                                   ServiceCallback<List<SimpleUserEntity>> callback) {
        String url = "/users/" + userId;

        networkService.getRequest(url, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                new Handler(Looper.getMainLooper()).post(() -> callback.onError(e));
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

                    JSONObject data = new JSONObject(body).getJSONArray("data").getJSONObject(0);
                    // "followers" |-> followedBy array; "following" |-> follows.
                    String key = "followers".equals(listType) ? "followedBy" : "follows";
                    JSONArray arr = data.optJSONArray(key);
                    List<SimpleUserEntity> list = arr != null
                            ? JSONArrayHelper.makeUserList(arr) : new ArrayList<>();

                    new Handler(Looper.getMainLooper()).post(() -> callback.onSuccess(list));

                } catch (Exception e) {
                    new Handler(Looper.getMainLooper()).post(() -> callback.onError(e));
                }
            }
        });
    }

    /*
     * Parses the common fields from either a MyselfUserDTO or NormalUserDTO JSON object.
     * The only difference between the two DTOs is the email field, passed in explicitly: the caller supplies the value from the JSON for own-profile requests,
     * or null for other-user requests where the backend does not expose the email.
     */
    private static DetailedUserEntity parseUserData(JSONObject data, String email) throws Exception {
        Long id = data.getLong("id");
        String username = data.optString("username", "");
        String profilePictureURL = data.isNull("profilePictureURL") ? "" : data.optString("profilePictureURL", "");
        String description = data.optString("description", "");

        JSONArray followsArr = data.optJSONArray("follows");
        int followingCount = followsArr != null ? followsArr.length() : 0;
        List<SimpleUserEntity> followingList = followsArr != null
                ? JSONArrayHelper.makeUserList(followsArr) : new ArrayList<>();

        JSONArray followedByArr = data.optJSONArray("followedBy");
        int followerCount = followedByArr != null ? followedByArr.length() : 0;
        List<SimpleUserEntity> followersList = followedByArr != null
                ? JSONArrayHelper.makeUserList(followedByArr) : new ArrayList<>();

        JSONArray favoritesJson = data.optJSONArray("favorites");
        List<SimpleGameEntity> favoriteGames = favoritesJson != null
                ? JSONArrayHelper.makeGameList(favoritesJson) : new ArrayList<>();

        JSONArray wantToPlayJson = data.optJSONArray("wantsToPlay");
        List<SimpleGameEntity> wantToPlayGames = wantToPlayJson != null
                ? JSONArrayHelper.makeGameList(wantToPlayJson) : new ArrayList<>();

        JSONArray hasPlayedJson = data.optJSONArray("hasPlayed");
        List<SimpleGameEntity> havePlayedGames = hasPlayedJson != null
                ? JSONArrayHelper.makeGameList(hasPlayedJson) : new ArrayList<>();

        JSONArray reviewsJson = data.optJSONArray("reviews");
        List<SimpleReviewEntity> reviews = reviewsJson != null
                ? JSONArrayHelper.makeReviewList(reviewsJson) : new ArrayList<>();

        return new DetailedUserEntity(
                id, username, email, profilePictureURL, description,
                followerCount, followingCount,
                reviews, favoriteGames, wantToPlayGames, havePlayedGames,
                followersList, followingList
        );
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

    //using for stacking own review on top but reviews have a author wich is used
    public void getLoggedInUsername(ServiceCallback<String> callback) {
        getUserOwnProfile(new ServiceCallback<SimpleUserEntity>() {
            @Override
            public void onSuccess(SimpleUserEntity user) {
                callback.onSuccess(user.getUsername());
            }

            @Override
            public void onError(Exception e) {
                callback.onError(e);
            }
        });
    }

}