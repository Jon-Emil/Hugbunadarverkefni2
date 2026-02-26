package is.hbv601g.gamecatalog.pages.profile;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import is.hbv601g.gamecatalog.entities.user.DetailedUserEntity;
import is.hbv601g.gamecatalog.helpers.ServiceCallback;
import is.hbv601g.gamecatalog.services.NetworkService;
import is.hbv601g.gamecatalog.services.UserService;
import is.hbv601g.gamecatalog.storage.TokenManager;

public class PersonalProfileViewModel extends AndroidViewModel {

    private final UserService userService;
    private final TokenManager tokenManager;

    private final MutableLiveData<DetailedUserEntity> user = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> loggedOut = new MutableLiveData<>();

    public PersonalProfileViewModel(@NonNull Application application) {
        super(application);
        NetworkService networkService = new NetworkService(application);
        userService = new UserService(networkService);
        tokenManager = new TokenManager(application);
    }

    public LiveData<DetailedUserEntity> getUser() { return user; }
    public LiveData<String> getErrorMessage() { return errorMessage; }
    public LiveData<Boolean> getIsLoading() { return isLoading; }
    public LiveData<Boolean> getLoggedOut() { return loggedOut; }

    public void loadProfile() {
        isLoading.setValue(true);
        userService.getMyProfile(new ServiceCallback<DetailedUserEntity>() {
            @Override
            public void onSuccess(DetailedUserEntity result) {
                isLoading.setValue(false);
                user.setValue(result);
            }

            @Override
            public void onError(Exception e) {
                isLoading.setValue(false);
                errorMessage.setValue("Failed to load profile: " + e.getMessage());
            }
        });
    }

    public void logOut() {
        tokenManager.removeToken();
        loggedOut.setValue(true);
    }

    public void clearLoggedOut() {
        loggedOut.setValue(null);
    }
}