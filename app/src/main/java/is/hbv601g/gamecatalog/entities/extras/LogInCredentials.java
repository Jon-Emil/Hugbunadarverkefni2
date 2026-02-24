package is.hbv601g.gamecatalog.entities.extras;

public class LogInCredentials {
    private String email;
    private String password;

    public LogInCredentials(String email, String password){
        this.email = email;
        this.password = password;
    }

    public LogInCredentials(){}

    // Getters and setters
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
