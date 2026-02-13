package is.hbv601g.gamecatalog.services;

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

    public NetworkService() {
        this.client = new OkHttpClient();
    }

    public Call getRequest(String endpoint, Callback callback) {
        Request request = new Request.Builder()
                .url(baseUrl + endpoint)
                .get()
                .build();

        Call call = client.newCall(request);
        call.enqueue(callback);
        return call;
    }

    public Call postRequest(String endpoint, String jsonBody, Callback callback) {
        RequestBody body = RequestBody.create(jsonBody, JSON);

        Request request = new Request.Builder()
                .url(baseUrl + endpoint)
                .post(body)
                .build();

        Call call = client.newCall(request);
        call.enqueue(callback);
        return call;
    }

    public Call patchRequest(String endpoint, String jsonBody, Callback callback) {
        RequestBody body = RequestBody.create(jsonBody, JSON);

        Request request = new Request.Builder()
                .url(baseUrl + endpoint)
                .patch(body)
                .build();

        Call call = client.newCall(request);
        call.enqueue(callback);
        return call;
    }

    public Call deleteRequest(String endpoint, Callback callback) {
        Request request = new Request.Builder()
                .url(baseUrl + endpoint)
                .delete()
                .build();

        Call call = client.newCall(request);
        call.enqueue(callback);
        return call;
    }
}
