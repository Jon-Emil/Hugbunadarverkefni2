package is.hbv601g.gamecatalog.pages.profile;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import is.hbv601g.gamecatalog.entities.user.DetailedUserEntity;
import is.hbv601g.gamecatalog.services.NetworkService;
import is.hbv601g.gamecatalog.services.UserService;

public class ProfileViewModel extends AndroidViewModel {

    private final UserService userService;

    private final MutableLiveData<DetailedUserEntity> user = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);

    public ProfileViewModel(@NonNull Application application) {
        super(application);
        NetworkService networkService = new NetworkService(application);
        userService = new UserService(networkService);
    }

    public LiveData<DetailedUserEntity> getUser() { return user; }
    public LiveData<String> getErrorMessage() { return errorMessage; }
    public LiveData<Boolean> getIsLoading() { return isLoading; }

    public void loadProfile() {
        isLoading.setValue(true);
        userService.getMyProfile(new is.hbv601g.gamecatalog.helpers.ServiceCallback<DetailedUserEntity>() {
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
}