package is.hbv601g.gamecatalog.pages.specific_game;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import is.hbv601g.gamecatalog.entities.game.DetailedGameEntity;
import is.hbv601g.gamecatalog.helpers.ServiceCallback;
import is.hbv601g.gamecatalog.services.GameService;

public class SpecificGameViewModel extends ViewModel {

    private final MutableLiveData<DetailedGameEntity> game = new MutableLiveData<>();
    private GameService gameService;

    public LiveData<DetailedGameEntity> getGame() {
        return game;
    }

    public void init(GameService gameService, long gameId) {
        if (game.getValue() != null) {
            return;
        }

        this.gameService = gameService;
        fetchGame(gameId);
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
}
