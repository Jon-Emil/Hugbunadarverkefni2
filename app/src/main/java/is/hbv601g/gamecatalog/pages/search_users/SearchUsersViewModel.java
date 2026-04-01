package is.hbv601g.gamecatalog.pages.search_users;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import is.hbv601g.gamecatalog.entities.extras.AdvancedSearchParameters;
import is.hbv601g.gamecatalog.entities.game.ListedGameEntity;
import is.hbv601g.gamecatalog.entities.genre.ListedGenreEntity;
import is.hbv601g.gamecatalog.entities.genre.SimpleGenreEntity;
import is.hbv601g.gamecatalog.entities.user.SimpleUserEntity;
import is.hbv601g.gamecatalog.helpers.JSONArrayHelper;
import is.hbv601g.gamecatalog.helpers.PaginatedCallback;
import is.hbv601g.gamecatalog.helpers.ServiceCallback;
import is.hbv601g.gamecatalog.services.UserService;
import is.hbv601g.gamecatalog.services.GenreService;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class SearchUsersViewModel extends ViewModel {

    private final MutableLiveData<List<SimpleUserEntity>> users = new MutableLiveData<>();
    private final MutableLiveData<Integer> pageAmount = new MutableLiveData<>();
    private final MutableLiveData<Integer> currentPage = new MutableLiveData<>(1);
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    private UserService userService;

    private boolean sortReverse = false;
    private String sortBy = "username";
    private String usernameParam = "";

    public void init(UserService userService) {
        this.userService = userService;
        if (users.getValue() == null) {
            Integer page = currentPage.getValue();
            fetchUsers(page == null ? 1 : page);
        }
    }

    public LiveData<List<SimpleUserEntity>> getUsers() {
        return users;
    }

    public LiveData<Integer> getPageAmount() {
        return pageAmount;
    }

    public LiveData<Integer> getCurrentPage() {
        return currentPage;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public void loadPage(int pageNr) {
        currentPage.postValue(pageNr);
        fetchUsers(pageNr);
    }

    public void refreshPage() {
        Integer page = currentPage.getValue();
        fetchUsers(page == null ? 1 : page);
    }

    public void nextPage() {
        Integer page = currentPage.getValue();
        Integer lastPage = pageAmount.getValue();
        if (page != null && (lastPage == null || page < lastPage)) {
            loadPage(page + 1);
        }
    }

    public void previousPage() {
        Integer page = currentPage.getValue();
        if (page != null && page > 1) {
            loadPage(page - 1);
        }
    }

    public void setUsernameParam(String newUsernameParam) {
        usernameParam = newUsernameParam;
    }

    public void setSortReverse(boolean sortReverse) {
        this.sortReverse = sortReverse;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    private void fetchUsers(int pageNr) {
        isLoading.postValue(true);
        userService.getSearchedUsers(usernameParam, pageNr, sortBy, sortReverse, new PaginatedCallback<SimpleUserEntity>() {
            @Override
            public void onError(Exception e) {
                errorMessage.postValue("Couldn't Fetch Users");
            }

            @Override
            public void onSuccess(List<SimpleUserEntity> fetchedUsers, int newPageAmount) {
                users.postValue(fetchedUsers);
                pageAmount.postValue(newPageAmount);
                isLoading.postValue(false);
            }
        });
    }
}
