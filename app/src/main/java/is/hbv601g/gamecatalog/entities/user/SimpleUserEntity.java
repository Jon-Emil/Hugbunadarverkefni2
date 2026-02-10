package is.hbv601g.gamecatalog.entities.user;

public class SimpleUserEntity {
    private Long id;
    private String username;
    private String profilePictureURL;
    private String description;

    public SimpleUserEntity(
            Long id,
            String username,
            String profilePictureURL,
            String description
    ) {
        this.id = id;
        this.username = username;
        this.profilePictureURL = profilePictureURL;
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getProfilePictureURL() {
        return profilePictureURL;
    }

    public void setProfilePictureURL(String profilePictureURL) {
        this.profilePictureURL = profilePictureURL;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
