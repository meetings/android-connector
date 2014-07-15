package gs.meetin.connector;



import android.util.Log;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import retrofit.Callback;
import retrofit.ErrorHandler;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.POST;

public class LoginHandler {

    public final static String BASEURL = "https://api-dev.meetin.gs/v1";

    private LoginService loginService;

    public interface LoginService {
        @POST("/login")
        LoginResult login(@Body LoginRequest body);

        @POST("/login")
        PinResult requestPin(@Body PinRequest body);
    }

    public LoginHandler() {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(BASEURL)
                .build();

        loginService = restAdapter.create(LoginService.class);
    }

    public boolean requestPin(String email) {
        PinResult pr = loginService.requestPin(new PinRequest(email));
        return true;
    }

    public BasicNameValuePair login(String email, String pin) {
        LoginResult lr = loginService.login(new LoginRequest(email, pin));
        User user = lr.result;

        if(user == null) {
            throw new LoginException(lr.error.message);
        }

        return new BasicNameValuePair(user.user_id, user.token);
    }

    class LoginRequest {
        final String email;
        final String pin;

        LoginRequest(String email, String pin) {
            this.email = email;
            this.pin = pin;
        }
    }

    class LoginResult {
        public User result;
        public Err error;
    }

    class User {
        public String user_id;
        public String token;
    }

    class Err {
        public String code;
        public String message;
    }

    class PinRequest {
        final String email;
        final String include_pin;
        final String allow_register;

        PinRequest(String email) {
            this.email = email;
            this.include_pin = "1";
            this.allow_register = "1";
        }
    }

    class PinResult {

    }
}
