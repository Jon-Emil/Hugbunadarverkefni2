package is.hbv601g.gamecatalog.helpers;

import java.util.List;

public interface PaginatedCallback<T> {
    void onSuccess(List<T> result, int pageAmount);
    void onError(Exception e);
}
