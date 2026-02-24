package is.hbv601g.gamecatalog.pages.login;

import static java.security.AccessController.getContext;

import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import is.hbv601g.gamecatalog.entities.extras.LogInCredentials;
import is.hbv601g.gamecatalog.helpers.LoginCallback;
import is.hbv601g.gamecatalog.services.AuthService;

public class LogInViewModel extends ViewModel {

    private AuthService authService;

    private LogInCredentials logInCredentials;

    // MutableLiveData for error messages and success state
    // Login error and success code inspired from ChatGPT
    private final MutableLiveData<String> loginError = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loginSuccess = new MutableLiveData<>();

    public void init(AuthService authService) {
        this.authService = authService;
        logInCredentials = new LogInCredentials();
    }

    public void logIn() {
        authService.logIn(logInCredentials, new LoginCallback(){
            @Override
            public void onSuccess() {
                // Handle successful login
                System.out.println("Login successful, Token saved!");
                loginSuccess.postValue(true);

            }

            @Override
            public void onError(String errorMessage) {
                // Handle login error
                System.out.println("ERROR" + errorMessage);
                loginError.postValue(errorMessage);
            }
        });
    }

    public void register() {
        authService.register(logInCredentials, new LoginCallback(){
            @Override
            public void onSuccess() {
                // Handle successful login
                System.out.println("Registration successful, Token saved!");
                loginSuccess.postValue(true);
            }

            @Override
            public void onError(String errorMessage) {
                // Handle login error
                System.out.println("ERROR" + errorMessage);
                loginError.postValue(errorMessage);
            }
        });
    }

    public void setEmail(String email) {
        logInCredentials.setEmail(email);
    }

    public void setPassword(String password) {
        logInCredentials.setPassword(password);
    }

    public LiveData<String> getLoginError() {
        return loginError;
    }

    public LiveData<Boolean> getLoginSuccess() {
        return loginSuccess;
    }
}
