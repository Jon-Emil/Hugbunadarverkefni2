package is.hbv601g.gamecatalog.pages.modify_user;

import android.app.Application;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import is.hbv601g.gamecatalog.entities.user.SimpleUserEntity;
import is.hbv601g.gamecatalog.helpers.ServiceCallback;
import is.hbv601g.gamecatalog.services.UserService;

// Extends AndroidViewModel so we can access Application context for reading the image URI
public class ModifyUserViewModel extends AndroidViewModel {

    private final MutableLiveData<SimpleUserEntity> userProfile = new MutableLiveData<>();
    private final MutableLiveData<String> statusMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> userDeleted = new MutableLiveData<>(false);
    private UserService userService;

    public ModifyUserViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<SimpleUserEntity> getUserProfile() { return userProfile; }
    public LiveData<String> getStatusMessage() { return statusMessage; }
    public LiveData<Boolean> isUserDeleted() { return userDeleted; }

    public void init(UserService userService) {
        this.userService = userService;
        if (userProfile.getValue() == null) {
            fetchUserProfile();
        }
    }

    private void fetchUserProfile() {
        userService.getUserOwnProfile(new ServiceCallback<SimpleUserEntity>() {
            @Override
            public void onSuccess(SimpleUserEntity user) {
                userProfile.postValue(user);
            }
            @Override
            public void onError(Exception e) {
                statusMessage.postValue("Error loading profile");
            }
        });
    }

    // imageUri is null when the user did not pick a new picture
    public void updateProfile(String username, String description, Uri imageUri) {
        SimpleUserEntity current = userProfile.getValue();
        if (current == null) return;

        current.setUsername(username);
        current.setDescription(description);

        userService.modifyProfile(current, imageUri, getApplication(), new ServiceCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean success) {
                statusMessage.postValue(success ? "Profile Update Successful" : "Update Failed");
            }
            @Override
            public void onError(Exception e) {
                statusMessage.postValue("Error");
            }
        });
    }

    public void deleteAccount() {
        userService.deleteAccount(new ServiceCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean success) {
                if (success) userDeleted.postValue(true);
            }
            @Override
            public void onError(Exception e) {
                statusMessage.postValue("Error deleting account");
            }
        });
    }
}