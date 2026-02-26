package is.hbv601g.gamecatalog.entities.user;

import java.util.ArrayList;
import java.util.List;

import is.hbv601g.gamecatalog.entities.game.SimpleGameEntity;
import is.hbv601g.gamecatalog.entities.review.SimpleReviewEntity;

public class DetailedUserEntity {
    private Long id;
    private String username;
    private String email;
    private String profilePictureURL;
    private String description;

    private int followerCount;
    private int followingCount;

    private List<SimpleReviewEntity> reviews = new ArrayList<>();
    private List<SimpleGameEntity> favoriteGames = new ArrayList<>();
    private List<SimpleGameEntity> wantToPlayGames = new ArrayList<>();
    private List<SimpleGameEntity> havePlayedGames = new ArrayList<>();

    public DetailedUserEntity(
            Long id,
            String username,
            String email,
            String profilePictureURL,
            String description,
            int followerCount,
            int followingCount,
            List<SimpleReviewEntity> reviews,
            List<SimpleGameEntity> favoriteGames,
            List<SimpleGameEntity> wantToPlayGames,
            List<SimpleGameEntity> havePlayedGames
    ) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.profilePictureURL = profilePictureURL;
        this.description = description;
        this.followerCount = followerCount;
        this.followingCount = followingCount;
        this.reviews = reviews;
        this.favoriteGames = favoriteGames;
        this.wantToPlayGames = wantToPlayGames;
        this.havePlayedGames = havePlayedGames;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getProfilePictureURL() { return profilePictureURL; }
    public void setProfilePictureURL(String profilePictureURL) { this.profilePictureURL = profilePictureURL; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getFollowerCount() { return followerCount; }
    public void setFollowerCount(int followerCount) { this.followerCount = followerCount; }

    public int getFollowingCount() { return followingCount; }
    public void setFollowingCount(int followingCount) { this.followingCount = followingCount; }

    public List<SimpleReviewEntity> getReviews() { return reviews; }
    public void setReviews(List<SimpleReviewEntity> reviews) { this.reviews = reviews; }

    public List<SimpleGameEntity> getFavoriteGames() { return favoriteGames; }
    public void setFavoriteGames(List<SimpleGameEntity> favoriteGames) { this.favoriteGames = favoriteGames; }

    public List<SimpleGameEntity> getWantToPlayGames() { return wantToPlayGames; }
    public void setWantToPlayGames(List<SimpleGameEntity> wantToPlayGames) { this.wantToPlayGames = wantToPlayGames; }

    public List<SimpleGameEntity> getHavePlayedGames() { return havePlayedGames; }
    public void setHavePlayedGames(List<SimpleGameEntity> havePlayedGames) { this.havePlayedGames = havePlayedGames; }
}