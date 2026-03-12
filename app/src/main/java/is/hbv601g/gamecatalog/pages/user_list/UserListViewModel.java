package is.hbv601g.gamecatalog.pages.user_list;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import is.hbv601g.gamecatalog.entities.user.SimpleUserEntity;
import is.hbv601g.gamecatalog.helpers.ServiceCallback;
import is.hbv601g.gamecatalog.services.NetworkService;
import is.hbv601g.gamecatalog.services.UserService;

/*
 * ViewModel for UserListFragment.
 *
 * Calls getUserConnections() rather than getOtherUserProfile() so that only the
 * relevant follows/followedBy array is parsed — skipping games and reviews that
 * UserListFragment would never use. This avoids unnecessary work when the user
 * just wants to browse who follows or is followed by another user.
 */
public class UserListViewModel extends AndroidViewModel {

    private final UserService userService;

    private final MutableLiveData<List<SimpleUserEntity>> users = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);

    public UserListViewModel(@NonNull Application application) {
        super(application);
        userService = new UserService(new NetworkService(application));
    }

    public LiveData<List<SimpleUserEntity>> getUsers() { return users; }
    public LiveData<String> getErrorMessage() { return errorMessage; }
    public LiveData<Boolean> getIsLoading() { return isLoading; }
    public void clearErrorMessage() { errorMessage.setValue(null); }

    public void loadUserList(long userId, String listType) {
        isLoading.setValue(true);
        userService.getUserConnections(userId, listType, new ServiceCallback<List<SimpleUserEntity>>() {
            @Override
            public void onSuccess(List<SimpleUserEntity> result) {
                isLoading.setValue(false);
                users.setValue(result);
            }

            @Override
            public void onError(Exception e) {
                isLoading.setValue(false);
                errorMessage.setValue("Failed to load users");
            }
        });
    }
}
