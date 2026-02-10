package is.hbv601g.gamecatalog.entities.genre;

public class SimpleGenreEntity {
    private long id;
    private String title;
    private String description;
    private int gameAmount;

    public SimpleGenreEntity(
            long id,
            String title,
            String description,
            int gameAmount
    ) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.gameAmount = gameAmount;
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

    public int getGameAmount() {
        return gameAmount;
    }

    public void setGameAmount(int gameAmount) {
        this.gameAmount = gameAmount;
    }
}
