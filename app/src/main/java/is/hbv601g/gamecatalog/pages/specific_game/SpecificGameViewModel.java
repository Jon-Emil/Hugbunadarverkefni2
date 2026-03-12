package is.hbv601g.gamecatalog.pages.specific_game;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

import is.hbv601g.gamecatalog.entities.user.DetailedUserEntity;
import is.hbv601g.gamecatalog.entities.user.SimpleUserEntity;
import is.hbv601g.gamecatalog.helpers.GameCollections;

import is.hbv601g.gamecatalog.entities.game.DetailedGameEntity;
import is.hbv601g.gamecatalog.helpers.EmptyCallBack;
import is.hbv601g.gamecatalog.helpers.ServiceCallback;
import is.hbv601g.gamecatalog.services.GameService;
import is.hbv601g.gamecatalog.services.UserService;

public class SpecificGameViewModel extends ViewModel {

    private GameService gameService;
    private UserService userService;

    private long gameID;
    private boolean collectionsInitialized = false;
    private final MutableLiveData<DetailedGameEntity> game = new MutableLiveData<>();
    private final MutableLiveData<DetailedUserEntity> user = new MutableLiveData<>();

    public LiveData<DetailedGameEntity> getGame() { return game; }

    // favorite
    private final MutableLiveData<Boolean> isInFavorites = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isProcessingFavorites = new MutableLiveData<>(true);

    public LiveData<Boolean> getIsInFavorites() { return isInFavorites; }
    public MutableLiveData<Boolean> getIsProcessingFavorites() { return isProcessingFavorites; }

    // wantToPlay
    private final MutableLiveData<Boolean> isInWantToPlay = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isProcessingWantToPlay = new MutableLiveData<>(true);

    public LiveData<Boolean> getIsInWantToPlay() { return isInWantToPlay; }
    public MutableLiveData<Boolean> getIsProcessingWantToPlay() { return isProcessingWantToPlay; }

    // hasPlayed
    private final MutableLiveData<Boolean> isInHasPlayed = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isProcessingHasPlayed = new MutableLiveData<>(true);

    public LiveData<Boolean> getIsInHasPlayed() { return isInHasPlayed; }
    public MutableLiveData<Boolean> getIsProcessingHasPlayed() { return isProcessingHasPlayed; }

    // the reason we need this instead of just attempting to call the methods when either the user
    // or game is fetched is because postValue is async so we need to actually observe
    // the mutable live datas instead and this also works for the fragment in order to hide
    // the add / remove collections buttons when there was no user found
    private final MediatorLiveData<Boolean> userAndGameExist = new MediatorLiveData<>(false);
    public MediatorLiveData<Boolean> getUserAndGameExist() { return userAndGameExist; }

    public void init(GameService gameService, UserService userService, long gameID) {
        if (game.getValue() != null) {
            return;
        }

        this.gameID = gameID;
        this.gameService = gameService;
        this.userService = userService;

        userAndGameExist.addSource(game, g -> tryInitializingCollections());
        userAndGameExist.addSource(user, u -> tryInitializingCollections());

        fetchGame(gameID);
        fetchUser();
    }

    private void fetchGame(long gameId) {
        gameService.getSpecificGame(gameId, new ServiceCallback<DetailedGameEntity>() {
            @Override
            public void onError(Exception e) {
                e.printStackTrace();
            }

            @Override
            public void onSuccess(DetailedGameEntity fetchedGame) {
                game.postValue(fetchedGame);
            }
        });
    }

    private void fetchUser() {
        userService.getMyProfile(new ServiceCallback<DetailedUserEntity>() {
            @Override
            public void onError(Exception e) {
                Log.w("DetailedGameViewModel", "Could not fetch user");
            }

            @Override
            public void onSuccess(DetailedUserEntity fetchedUser) {
                user.postValue(fetchedUser);
            }
        });
    }

    public void addToCollection(GameCollections selectedCollection) {

        switch(selectedCollection) {
            case FAVORITE:
                isProcessingFavorites.postValue(true);
                break;
            case WANT_TO_PLAY:
                isProcessingWantToPlay.postValue(true);
                break;
            case HAS_PLAYED:
                isProcessingHasPlayed.postValue(true);
                break;
        }

        gameService.addGameToCollection(gameID, selectedCollection, new EmptyCallBack() {
            @Override
            public void onError(Exception e) {
                Log.e("SpecificGameViewModel", "add to collection error");
                switch(selectedCollection) {
                    case FAVORITE:
                        isInFavorites.postValue(null);
                        isProcessingFavorites.postValue(false);
                        break;
                    case WANT_TO_PLAY:
                        isInWantToPlay.postValue(null);
                        isProcessingWantToPlay.postValue(false);
                        break;
                    case HAS_PLAYED:
                        isInHasPlayed.postValue(null);
                        isProcessingHasPlayed.postValue(false);
                        break;
                }
            }

            @Override
            public void onSuccess() {
                switch(selectedCollection) {
                    case FAVORITE:
                        isInFavorites.postValue(true);
                        isProcessingFavorites.postValue(false);
                        break;
                    case WANT_TO_PLAY:
                        isInWantToPlay.postValue(true);
                        isProcessingWantToPlay.postValue(false);
                        break;
                    case HAS_PLAYED:
                        isInHasPlayed.postValue(true);
                        isProcessingHasPlayed.postValue(false);
                        break;
                }
                fetchGame(gameID); // update collection amount displays
            }
        });
    }

    public void removeFromCollection(GameCollections selectedCollection) {

        switch(selectedCollection) {
            case FAVORITE:
                isProcessingFavorites.postValue(true);
                break;
            case WANT_TO_PLAY:
                isProcessingWantToPlay.postValue(true);
                break;
            case HAS_PLAYED:
                isProcessingHasPlayed.postValue(true);
                break;
        }


        gameService.removeGameFromCollection(gameID, selectedCollection, new EmptyCallBack() {
            @Override
            public void onError(Exception e) {
                Log.e("SpecificGameViewModel", "remove from collection error");
                switch(selectedCollection) {
                    case FAVORITE:
                        isInFavorites.postValue(null);
                        isProcessingFavorites.postValue(false);
                        break;
                    case WANT_TO_PLAY:
                        isInWantToPlay.postValue(null);
                        isProcessingWantToPlay.postValue(false);
                        break;
                    case HAS_PLAYED:
                        isInHasPlayed.postValue(null);
                        isProcessingHasPlayed.postValue(false);
                        break;
                }
            }

            @Override
            public void onSuccess() {
                switch(selectedCollection) {
                    case FAVORITE:
                        isInFavorites.postValue(false);
                        isProcessingFavorites.postValue(false);
                        break;
                    case WANT_TO_PLAY:
                        isInWantToPlay.postValue(false);
                        isProcessingWantToPlay.postValue(false);
                        break;
                    case HAS_PLAYED:
                        isInHasPlayed.postValue(false);
                        isProcessingHasPlayed.postValue(false);
                        break;
                }
                fetchGame(gameID); // update collection amount displays
            }
        });
    }

    // This should not be done in our frontend
    // we should've had a get method in our backend that tells us if the logged in user
    // has the provided game in their specified collection
    // but this is fine and not worth updating our backend over
    public boolean doesUserHaveGameInCollection(GameCollections selectedCollection) {
        if (user.getValue() == null) {
            return false;
        }

        List<SimpleUserEntity> usersWithGameInCollection = new ArrayList<>();
        if (game.getValue() != null) {
            switch (selectedCollection) {
                case FAVORITE:
                    usersWithGameInCollection = game.getValue().getFavoriteOf();
                    break;
                case WANT_TO_PLAY:
                    usersWithGameInCollection = game.getValue().getWantToPlay();
                    break;
                case HAS_PLAYED:
                    usersWithGameInCollection = game.getValue().getHavePlayed();
                    break;
            }
        }

        long userID = user.getValue().getId();
        boolean userFound = false;
        for (int i = 0; i < usersWithGameInCollection.size(); i++) {
            SimpleUserEntity iteratedUser = usersWithGameInCollection.get(i);
            if (iteratedUser.getId() == userID) {
                userFound = true;
                break;
            }
        }
        return userFound;
    }

    public void refreshGame() {
        fetchGame(gameID);
    }


    private void tryInitializingCollections() {
        if (collectionsInitialized) { return; }
        if (game.getValue() == null || user.getValue() == null) {
            return;
        }
        collectionsInitialized = true;

        isInFavorites.postValue(
                doesUserHaveGameInCollection(GameCollections.FAVORITE)
        );
        isProcessingFavorites.postValue(false);

        isInWantToPlay.postValue(
                doesUserHaveGameInCollection(GameCollections.WANT_TO_PLAY)
        );
        isProcessingWantToPlay.postValue(false);

        isInHasPlayed.postValue(
                doesUserHaveGameInCollection(GameCollections.HAS_PLAYED)
        );
        isProcessingHasPlayed.postValue(false);

        userAndGameExist.postValue(true);
    }
}
