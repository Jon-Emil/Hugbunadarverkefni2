package is.hbv601g.gamecatalog.pages.login;

import androidx.lifecycle.ViewModel;

import is.hbv601g.gamecatalog.entities.extras.LogInCredentials;
import is.hbv601g.gamecatalog.services.AuthService;

public class LogInViewModel extends ViewModel {

    private AuthService authService;

    private LogInCredentials logInCredentials;

    public void init(AuthService authService) {
        this.authService = authService;
        logInCredentials = new LogInCredentials();
    }

    public void logIn() {
        authService.logIn(logInCredentials);
    }

    public void register() {
        authService.register(logInCredentials);
    }

    public void setEmail(String email) {
        logInCredentials.setEmail(email);
    }

    public void setPassword(String password) {
        logInCredentials.setPassword(password);
    }
}
