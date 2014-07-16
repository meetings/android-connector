package gs.meetin.connector;



import org.apache.http.message.BasicNameValuePair;

import gs.meetin.connector.dto.LoginRequest;
import gs.meetin.connector.dto.PinRequest;
import retrofit.RestAdapter;
import retrofit.http.Body;
import retrofit.http.POST;

public class LoginHandler {

    private LoginService loginService;

    public interface LoginService {
        @POST("/login")
        LoginRequest.LoginResult login(@Body LoginRequest body);

        @POST("/login")
        PinRequest.PinResult requestPin(@Body PinRequest body);
    }

    public LoginHandler() {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(Constants.apiBaseURL)
                .build();

        loginService = restAdapter.create(LoginService.class);
    }

    public boolean requestPin(String email) {
        PinRequest.PinResult pr = loginService.requestPin(new PinRequest(email));
        return true;
    }

    public BasicNameValuePair login(String email, String pin) {
        LoginRequest.LoginResult lr = loginService.login(new LoginRequest(email, pin));
        LoginRequest.User user = lr.result;

        if(user == null) {
            throw new LoginException(lr.error.message);
        }

        return new BasicNameValuePair(user.user_id, user.token);
    }
}
