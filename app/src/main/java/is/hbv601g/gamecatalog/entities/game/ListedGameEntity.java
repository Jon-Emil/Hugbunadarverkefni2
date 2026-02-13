package is.hbv601g.gamecatalog.entities.game;

import java.util.ArrayList;
import java.util.List;

import is.hbv601g.gamecatalog.entities.genre.SimpleGenreEntity;

public class ListedGameEntity {
    private long id;
    private String title;
    private String description;
    private String releaseDate;
    private float price;
    private String coverImage;
    private String developer;
    private String publisher;

    private int reviewAmount;
    private Float averageRating;
    private int favoriteAmount;
    private int wantToPlayAmount;
    private int havePlayedAmount;

    private List<SimpleGenreEntity> genres  = new ArrayList<>();

    public ListedGameEntity(
            long id,
            String title,
            String description,
            String releaseDate,
            float price,
            String coverImage,
            String developer,
            String publisher,
            int reviewAmount,
            Float averageRating,
            int favoriteAmount,
            int wantToPlayAmount,
            int havePlayedAmount,
            List<SimpleGenreEntity> genres
    ) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.releaseDate = releaseDate;
        this.price = price;
        this.coverImage = coverImage;
        this.developer = developer;
        this.publisher = publisher;
        this.genres = genres;
        this.reviewAmount = favoriteAmount;
        this.averageRating = averageRating;
        this.favoriteAmount = wantToPlayAmount;
        this.wantToPlayAmount = havePlayedAmount;
        this.havePlayedAmount = reviewAmount;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public String getCoverImage() {
        return coverImage;
    }

    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
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

    public int getReviewAmount() {
        return reviewAmount;
    }

    public void setReviewAmount(int reviewAmount) {
        this.reviewAmount = reviewAmount;
    }

    public Float getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(Float averageRating) {
        this.averageRating = averageRating;
    }

    public int getFavoriteAmount() {
        return favoriteAmount;
    }

    public void setFavoriteAmount(int favoriteAmount) {
        this.favoriteAmount = favoriteAmount;
    }

    public int getWantToPlayAmount() {
        return wantToPlayAmount;
    }

    public void setWantToPlayAmount(int wantToPlayAmount) {
        this.wantToPlayAmount = wantToPlayAmount;
    }

    public int getHavePlayedAmount() {
        return havePlayedAmount;
    }

    public void setHavePlayedAmount(int havePlayedAmount) {
        this.havePlayedAmount = havePlayedAmount;
    }

    public List<SimpleGenreEntity> getGenres() {
        return genres;
    }

    public void setGenres(List<SimpleGenreEntity> genres) {
        this.genres = genres;
    }
}
