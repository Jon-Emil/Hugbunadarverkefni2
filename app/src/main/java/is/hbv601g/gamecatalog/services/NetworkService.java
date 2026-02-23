package is.hbv601g.gamecatalog.services;

import android.content.Context;

import is.hbv601g.gamecatalog.storage.TokenManager;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class NetworkService {

    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    private static final String baseUrl = "https://hbv1-gamecatalog.onrender.com";
    private final OkHttpClient client;
    private final TokenManager tokenManager;


    public NetworkService(Context context) {

        this.client = new OkHttpClient();
        this.tokenManager = new TokenManager(context);
    }

    public Call getRequest(String endpoint, Callback callback) {
        Request.Builder builder = new Request.Builder()
                .url(baseUrl + endpoint)
                .get();

        String token = tokenManager.getToken();
        if (token != null) {
            builder.addHeader("Authorization", "Bearer " + token);
        }

        Request request = builder.build();

        Call call = client.newCall(request);
        call.enqueue(callback);
        return call;
    }

    public Call postRequest(String endpoint, String jsonBody, Callback callback) {
        RequestBody body = RequestBody.create(jsonBody, JSON);

        Request.Builder builder = new Request.Builder()
                .url(baseUrl + endpoint)
                .post(body);

        String token = tokenManager.getToken();
        if (token != null) {
            builder.addHeader("Authorization", "Bearer " + token);
        }

        Request request = builder.build();

        Call call = client.newCall(request);
        call.enqueue(callback);
        return call;
    }

    public Call patchRequest(String endpoint, String jsonBody, Callback callback) {
        RequestBody body = RequestBody.create(jsonBody, JSON);

        Request.Builder builder = new Request.Builder()
                .url(baseUrl + endpoint)
                .patch(body);

        String token = tokenManager.getToken();
        if (token != null) {
            builder.addHeader("Authorization", "Bearer " + token);
        }

        Request request = builder.build();

        Call call = client.newCall(request);
        call.enqueue(callback);
        return call;
    }

    public Call deleteRequest(String endpoint, Callback callback) {
        Request.Builder builder = new Request.Builder()
                .url(baseUrl + endpoint)
                .delete();

        String token = tokenManager.getToken();
        if (token != null) {
            builder.addHeader("Authorization", "Bearer " + token);
        }

        Request request = builder.build();

        Call call = client.newCall(request);
        call.enqueue(callback);
        return call;
    }
}
