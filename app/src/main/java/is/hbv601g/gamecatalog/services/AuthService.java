package is.hbv601g.gamecatalog.services;

import android.content.Context;

import org.json.JSONObject;

import java.io.IOException;

import is.hbv601g.gamecatalog.entities.extras.LogInCredentials;
import is.hbv601g.gamecatalog.entities.game.DetailedGameEntity;
import is.hbv601g.gamecatalog.helpers.JSONObjectHelper;
import is.hbv601g.gamecatalog.storage.TokenManager;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class AuthService {
    private final NetworkService networkService;

    private final TokenManager tokenManager;

    public AuthService(NetworkService networkService, Context context) {
        this.networkService = networkService;
        this.tokenManager = new TokenManager(context);
    }

    public void logIn(LogInCredentials credentials){
        String url = "/login";

        String credentialString = JSONObjectHelper.convertCredentialsToJsonString(credentials);
        System.out.println("LOGIN BODY: " + credentialString);

        networkService.postRequest(url, credentialString, new Callback(){
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(response.isSuccessful()){
                    String token = response.body().string();
                    tokenManager.saveToken(token);
                    System.out.println("Login successful, Token saved!");
                }
                else{
                    System.out.println("Login failed" + response.code());
                }
            }
        });
    }

    public void register(LogInCredentials credentials){
        String url = "/register";

        String credentialString = JSONObjectHelper.convertCredentialsToJsonString(credentials);
        System.out.println("LOGIN BODY: " + credentialString);

        networkService.postRequest(url, credentialString, new Callback(){
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(response.isSuccessful()){
                    String token = response.body().string();
                    tokenManager.saveToken(token);
                    System.out.println("Login successful, Token saved!");
                }
                else{
                    System.out.println("Login failed" + response.code());
                }
            }
        });
    }


}
