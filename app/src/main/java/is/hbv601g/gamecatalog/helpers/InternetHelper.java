package is.hbv601g.gamecatalog.helpers;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;

//Class inspired from a conversation with ChatGPT about how to check for internet
// connection in Android applications
public class InternetHelper {
    public static boolean networkDetected(Context context){
        //Gets the system service for connectivity to the network
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        //checks the capabilities of that network
        NetworkCapabilities networkCapabilities =
                connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());

        //Checks if the network exists and is connected
        if(networkCapabilities != null){
            //Only returns true if device is connected to Wi-Fi or mobile data
            return networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                    networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR);
        }
        //Returns false if no network is detected
        return false;
    }
}
