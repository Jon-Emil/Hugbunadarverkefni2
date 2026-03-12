package is.hbv601g.gamecatalog.pages.other_user_profile;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import is.hbv601g.gamecatalog.entities.user.DetailedUserEntity;
import is.hbv601g.gamecatalog.helpers.ServiceCallback;
import is.hbv601g.gamecatalog.services.NetworkService;
import is.hbv601g.gamecatalog.services.UserService;

/**
 * ViewModel for OtherUserProfileFragment.
 * Fetches another user's public profile via GET /users/{userId}.
 * Email will be null since NormalUserDTO doesn't include it.
 */
public class OtherUserProfileViewModel extends AndroidViewModel {

    private final UserService userService;

    private final MutableLiveData<DetailedUserEntity> user = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);

    public OtherUserProfileViewModel(@NonNull Application application) {
        super(application);
        userService = new UserService(new NetworkService(application));
    }

    public LiveData<DetailedUserEntity> getUser() { return user; }
    public LiveData<String> getErrorMessage() { return errorMessage; }
    public LiveData<Boolean> getIsLoading() { return isLoading; }
    public void clearErrorMessage() { errorMessage.setValue(null); }

    public void loadProfile(long userId) {
        isLoading.setValue(true);
        userService.getOtherUserProfile(userId, new ServiceCallback<DetailedUserEntity>() {
            @Override
            public void onSuccess(DetailedUserEntity result) {
                isLoading.setValue(false);
                user.setValue(result);
            }

            @Override
            public void onError(Exception e) {
                isLoading.setValue(false);
                errorMessage.setValue("Failed to load profile");
            }
        });
    }
}
