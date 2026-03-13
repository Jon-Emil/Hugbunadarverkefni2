package is.hbv601g.gamecatalog.entities.review;

public class SimpleReviewEntity {
    private Long id;
    private int rating;
    private String text;
    private String title;
    private String author;
    private String gameTitle;
    private Long gameId;


    public SimpleReviewEntity(
            Long id,
            int rating,
            String text,
            String title,
            String author,
            String gameTitle,
            Long gameId
    ) {
        this.id = id;
        this.rating = rating;
        this.text = text;
        this.title = title;
        this.author = author;
        this.gameTitle = gameTitle;
        this.gameId = gameId;
    }

    public Long getGameId() { return gameId; }
    public void setGameId(Long gameId) { this.gameId = gameId; }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getGameTitle() {
        return gameTitle;
    }

    public void setGameTitle(String gameTitle) {
        this.gameTitle = gameTitle;
    }
}
