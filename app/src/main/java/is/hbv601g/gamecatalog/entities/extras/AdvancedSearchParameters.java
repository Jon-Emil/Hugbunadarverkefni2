package is.hbv601g.gamecatalog.entities.extras;

import java.util.ArrayList;
import java.util.List;

public class AdvancedSearchParameters {

    private Float minPrice = null;
    private Float maxPrice = null;
    private String releasedAfter = "";
    private String releasedBefore = "";
    private String developer = "";
    private String publisher = "";
    private List<String> genres = new ArrayList<>();

    public AdvancedSearchParameters(
            Float minPrice,
            Float maxPrice,
            String releasedAfter,
            String releasedBefore,
            String developer,
            String publisher,
            List<String> genres
    ) {
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
        this.releasedAfter = releasedAfter;
        this.releasedBefore = releasedBefore;
        this.developer = developer;
        this.publisher = publisher;
        this.genres = genres;
    }

    public AdvancedSearchParameters() {}

    public Float getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(Float minPrice) {
        this.minPrice = minPrice;
    }

    public Float getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(Float maxPrice) {
        this.maxPrice = maxPrice;
    }

    public String getReleasedAfter() {
        return releasedAfter;
    }

    public void setReleasedAfter(String releasedAfter) {
        this.releasedAfter = releasedAfter;
    }

    public String getReleasedBefore() {
        return releasedBefore;
    }

    public void setReleasedBefore(String releasedBefore) {
        this.releasedBefore = releasedBefore;
    }

    public String getDeveloper() {
        return developer;
    }

    public void setDeveloper(String developer) {
        this.developer = developer;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public List<String> getGenres() {
        return genres;
    }

    public void setGenres(List<String> genres) {
        this.genres = genres;
    }
}
