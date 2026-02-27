package is.hbv601g.gamecatalog.services;

import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import java.util.List;

import is.hbv601g.gamecatalog.entities.game.DetailedGameEntity;
import is.hbv601g.gamecatalog.entities.game.ListedGameEntity;
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

    // get own profile
    public void getUserOwnProfile(ServiceCallback<SimpleUserEntity> callback){
        String url = "/user/profile";

        networkService.getRequest(url, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                callback.onError(e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                try {
                    String body = response.body().string();
                    JSONObject json = new JSONObject(body).getJSONObject("data");
                    SimpleUserEntity user = JSONObjectHelper.getSimpleUser(json);
                    callback.onSuccess(user);
                } catch (Exception e) {
                    callback.onError(e);
                }

            }
        });

    }

    public void modifyProfile(SimpleUserEntity newProfile, ServiceCallback<Boolean> callback) {
        String url = "/users/profile";

        // Convert to JSON string
        String jsonBody = JSONObjectHelper.simpleUserToJson(newProfile).toString();

        networkService.putRequest(url, jsonBody, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onError(e);
            }

            @Override
            public void onResponse(Call call, Response response) {
                callback.onSuccess(response.isSuccessful());
            }
        });
    }



    // Delete my account
    public void deleteAccount(ServiceCallback<Boolean> callback) {
        String url = "/users/profile";

        networkService.deleteRequest(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onError(e);
            }
            @Override
            public void onResponse(Call call, Response response) {
                callback.onSuccess(response.isSuccessful()); } }); }
}
