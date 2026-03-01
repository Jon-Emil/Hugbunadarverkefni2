package is.hbv601g.gamecatalog.pages.modify_user;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import is.hbv601g.gamecatalog.entities.user.SimpleUserEntity;
import is.hbv601g.gamecatalog.helpers.ServiceCallback;
import is.hbv601g.gamecatalog.services.UserService;

public class ModifyUserViewModel extends ViewModel {
    private final MutableLiveData<SimpleUserEntity> userProfile = new MutableLiveData<>();
    private final MutableLiveData<String> statusMessage = new MutableLiveData<>();
    private UserService userService;

    public LiveData<SimpleUserEntity> getUserProfile() { return userProfile; }
    public LiveData<String> getStatusMessage() { return statusMessage; }

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

    public void updateProfile(String username, String description) {
        SimpleUserEntity current = userProfile.getValue();
        if (current == null) return;

        current.setUsername(username);
        current.setDescription(description);

        userService.modifyProfile(current, new ServiceCallback<Boolean>() {
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
}