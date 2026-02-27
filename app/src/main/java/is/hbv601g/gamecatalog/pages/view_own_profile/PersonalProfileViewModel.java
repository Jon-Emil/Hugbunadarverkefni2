package is.hbv601g.gamecatalog.pages.view_own_profile;

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
    private final MutableLiveData<Boolean> accountDeleted = new MutableLiveData<>();

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
    public LiveData<Boolean> getAccountDeleted() { return accountDeleted; }

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
                // if error 404 or unauthorized, remove token and direct to log in
                tokenManager.removeToken();
                loggedOut.postValue(true);
            }
        });
    }

    public void deleteAccount() {
        userService.deleteAccount(new ServiceCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean success) {
                if (Boolean.TRUE.equals(success)) {
                    tokenManager.removeToken();
                    accountDeleted.postValue(true);
                } else {
                    errorMessage.postValue("Failed to delete account");
                }
            }

            @Override
            public void onError(Exception e) {
                errorMessage.postValue("Error deleting account: " + e.getMessage());
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