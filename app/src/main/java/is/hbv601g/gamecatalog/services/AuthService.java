package is.hbv601g.gamecatalog.services;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import is.hbv601g.gamecatalog.entities.extras.LogInCredentials;
import is.hbv601g.gamecatalog.helpers.JSONObjectHelper;
import is.hbv601g.gamecatalog.helpers.LoginCallback;
import is.hbv601g.gamecatalog.storage.TokenManager;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class AuthService {
    private final NetworkService networkService;

    private final TokenManager tokenManager;

    public AuthService(NetworkService networkService, Context context) {
        this.networkService = networkService;
        this.tokenManager = new TokenManager(context);
    }

    public void logIn(LogInCredentials credentials, LoginCallback callback){
        String url = "/login";

        String credentialString = JSONObjectHelper.convertCredentialsToJsonString(credentials);


        networkService.postRequest(url, credentialString, new Callback(){
            @Override
            public void onFailure(Call call, IOException e) {
                //Code from ChatGPT to make sure the callback is executed on the main thread
                new Handler(Looper.getMainLooper()).post(() ->
                        callback.onError("Network error")
                );
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String body = response.body().string();

                if(response.isSuccessful()){
                    try {
                        //Parse token response to save only token string
                        JSONObject json = new JSONObject(body);
                        JSONArray dataArray = json.getJSONArray("data");
                        if (dataArray.length() > 0) {
                            String token = dataArray.getString(0);
                            tokenManager.saveToken(token);
                            callback.onSuccess();
                        }
                    }catch (JSONException e){
                        Log.e("AuthService", "Failed to parse login response", e);
                        new Handler(Looper.getMainLooper()).post(() ->
                                callback.onError("Unexpected server response")
                        );
                    }
                }
                else if(response.code() == 401){
                    callback.onError("Invalid credentials!");
                }
                else{
                    callback.onError("Login failed, something went wrong!");
                }
            }
        });
    }

    public void register(LogInCredentials credentials, LoginCallback callback){
        String url = "/register";

        String credentialString = JSONObjectHelper.convertCredentialsToJsonString(credentials);

        networkService.postRequest(url, credentialString, new Callback(){
            @Override
            public void onFailure(Call call, IOException e) {
                //Code from ChatGPT to make sure the callback is executed on the main thread
                new Handler(Looper.getMainLooper()).post(() ->
                        callback.onError("Network error")
                );
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String body = response.body().string();

                if(response.isSuccessful()){
                    try {
                        //Parse token response to save only token string
                        JSONObject json = new JSONObject(body);
                        JSONArray dataArray = json.getJSONArray("data");
                        if (dataArray.length() > 0) {
                            String token = dataArray.getString(0);
                            tokenManager.saveToken(token);
                            callback.onSuccess();
                        }
                    }catch (JSONException e){
                        Log.e("AuthService", "Failed to parse register response", e);
                        new Handler(Looper.getMainLooper()).post(() ->
                                callback.onError("Unexpected server response")
                        );
                    }
                }
                else if(response.code() == 401){
                    callback.onError("Invalid credentials!");
                }
                else{
                    callback.onError("Registration failed, something went wrong!");
                }
            }
        });
    }


}
