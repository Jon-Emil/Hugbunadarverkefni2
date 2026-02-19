package is.hbv601g.gamecatalog.services;

import okhttp3.Callback;

public class GameService {

    private final NetworkService networkService;

    public GameService(NetworkService networkService) {
        this.networkService = networkService;
    }

    public void getSpecificGame(long gameID, Callback callback) {
        String idString = String.valueOf(gameID);
        String url = "/games/" + idString;

        networkService.getRequest(url, callback);
    }

    public void getAllGames(int page , String sortBy, boolean sortReverse, Callback callback) {
        String pageString = String.valueOf(page);
        String reverseString = sortReverse ? "true" : "false";
        String url = "/games?pageNr=" + pageString + "&sortBy=" + sortBy + "&sortReverse=" + reverseString;

        networkService.getRequest(url, callback);
    }

    public void getSearchedGames(String gameTitleParam, int page, String sortBy, boolean sortReverse, Callback callback) {
        String pageString = String.valueOf(page);
        String reverseString = sortReverse ? "true" : "false";
        String url = "/games/search?title=" + gameTitleParam + "&pageNr=" + pageString + "&sortBy=" + sortBy + "&sortReverse=" + reverseString;

        networkService.getRequest(url, callback);
    }
}
