package is.hbv601g.gamecatalog.helpers;

public interface ServiceCallback<T> {
    void onSuccess(T result);
    void onError(Exception e);
}
