package is.hbv601g.gamecatalog.entities.game;

import java.util.ArrayList;
import java.util.List;

import is.hbv601g.gamecatalog.entities.genre.SimpleGenreEntity;
import is.hbv601g.gamecatalog.entities.review.SimpleReviewEntity;
import is.hbv601g.gamecatalog.entities.user.SimpleUserEntity;

public class DetailedGameEntity {

    private long id;
    private String title;
    private String description;
    private String releaseDate;
    private float price;
    private String coverImage;
    private String developer;
    private String publisher;

    private List<SimpleGenreEntity> genres = new ArrayList<>();
    private List<SimpleReviewEntity> reviews = new ArrayList<>();
    private List<SimpleUserEntity> favoriteOf = new ArrayList<>();
    private List<SimpleUserEntity> wantToPlay = new ArrayList<>();
    private List<SimpleUserEntity> havePlayed = new ArrayList<>();

    public DetailedGameEntity(
            String title,
            String description,
            String releaseDate,
            float price,
            String coverImage,
            String developer,
            String publisher,
            List<SimpleGenreEntity> genres,
            List<SimpleReviewEntity> reviews,
            List<SimpleUserEntity> favoriteOf,
            List<SimpleUserEntity> wantToPlay,
            List<SimpleUserEntity> havePlayed
    ) {
        this.title = title;
        this.description = description;
        this.releaseDate = releaseDate;
        this.price = price;
        this.coverImage = coverImage;
        this.developer = developer;
        this.publisher = publisher;
        this.genres = genres;
        this.reviews = reviews;
        this.favoriteOf = favoriteOf;
        this.wantToPlay = wantToPlay;
        this.havePlayed = havePlayed;
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

    public List<SimpleGenreEntity> getGenres() {
        return genres;
    }

    public void setGenres(List<SimpleGenreEntity> genres) {
        this.genres = genres;
    }

    public List<SimpleReviewEntity> getReviews() {
        return reviews;
    }

    public void setReviews(List<SimpleReviewEntity> reviews) {
        this.reviews = reviews;
    }

    public List<SimpleUserEntity> getFavoriteOf() {
        return favoriteOf;
    }

    public void setFavoriteOf(List<SimpleUserEntity> favoriteOf) {
        this.favoriteOf = favoriteOf;
    }

    public List<SimpleUserEntity> getWantToPlay() {
        return wantToPlay;
    }

    public void setWantToPlay(List<SimpleUserEntity> wantToPlay) {
        this.wantToPlay = wantToPlay;
    }

    public List<SimpleUserEntity> getHavePlayed() {
        return havePlayed;
    }

    public void setHavePlayed(List<SimpleUserEntity> havePlayed) {
        this.havePlayed = havePlayed;
    }
}
