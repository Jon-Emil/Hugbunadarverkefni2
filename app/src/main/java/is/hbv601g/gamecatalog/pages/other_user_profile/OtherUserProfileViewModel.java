package is.hbv601g.gamecatalog.pages.other_user_profile;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import is.hbv601g.gamecatalog.entities.user.DetailedUserEntity;
import is.hbv601g.gamecatalog.helpers.EmptyCallBack;
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
    private final MutableLiveData<DetailedUserEntity> loggedInUser = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);

    //Follow logic
    private final MutableLiveData<Boolean> isFollowing = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isProcessingFollow = new MutableLiveData<>(true);

    public LiveData<Boolean> getIsFollowing() { return isFollowing; }
    public MutableLiveData<Boolean> getIsProcessingFollow() { return isProcessingFollow; }

    public OtherUserProfileViewModel(@NonNull Application application) {
        super(application);
        userService = new UserService(new NetworkService(application));

        //Initialize following status
        isFollowing.postValue(checkFollowStatus());
        isProcessingFollow.postValue(false);
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
    private void fetchLoggedInUser() {
        userService.getMyProfile(new ServiceCallback<DetailedUserEntity>() {
            @Override public void onError(Exception e) {
                Log.w("OtherUserProfileViewModel", "Could not fetch logged in user");
            }
            @Override public void onSuccess(DetailedUserEntity fetchedUser) {
                loggedInUser.postValue(fetchedUser);
            }
        });
    }
    public void followUser(long userId) {
        isProcessingFollow.postValue(true);
        userService.followUser(userId, new EmptyCallBack() {
            @Override
            public void onSuccess() {
                isFollowing.postValue(true);
                isProcessingFollow.postValue(false);
            }

            @Override
            public void onError(Exception e) {
                Log.e("OtherUserProfileViewModel", "following user error");
                isFollowing.postValue(null);
                isProcessingFollow.postValue(false);
            }
        });
        loadProfile(userId); //Updates following amount
    }

    public void unfollowUser(long userId) {
        isProcessingFollow.postValue(true);
        userService.unfollowUser(userId, new EmptyCallBack() {
            @Override
            public void onSuccess() {
                isFollowing.postValue(false);
                isProcessingFollow.postValue(false);
            }

            @Override
            public void onError(Exception e) {
                Log.e("OtherUserProfileViewModel", "unfollowing user error");
                isFollowing.postValue(null);
                isProcessingFollow.postValue(false);
            }
        });
        loadProfile(userId); //Updates following amount
    }

    public boolean checkFollowStatus() {
        fetchLoggedInUser();
        if(loggedInUser.getValue() == null) {
            return false;
        }
        return loggedInUser.getValue().getFollowingList().contains(user.getValue().getId());
    }
}
