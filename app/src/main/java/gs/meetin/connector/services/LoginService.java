package gs.meetin.connector.services;


import android.util.Log;

import org.apache.http.message.BasicNameValuePair;

import de.greenrobot.event.EventBus;
import gs.meetin.connector.Constants;
import gs.meetin.connector.dto.LoginRequest;
import gs.meetin.connector.dto.PinRequest;
import gs.meetin.connector.events.ErrorEvent;
import gs.meetin.connector.events.SessionEvent;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.POST;

public class LoginService {

    private LoginRouter loginService;

    public interface LoginRouter {
        @POST("/login")
        void login(@Body LoginRequest body, Callback<LoginRequest> cb);

        @POST("/login")
        void requestPin(@Body PinRequest body, Callback<PinRequest> cb);
    }

    public LoginService(RestAdapter restAdapter) {
        loginService = restAdapter.create(LoginRouter.class);
    }

    public void requestPin(String email) {
        loginService.requestPin(new PinRequest(email), new Callback<PinRequest>() {
            @Override
            public void success(PinRequest pinResult, Response response) {
                if(pinResult.error != null) {
                    EventBus.getDefault().post(new ErrorEvent("Sorry!", pinResult.error.message));
                    return;
                }

                EventBus.getDefault().post(new SessionEvent(SessionEvent.PIN_REQUEST_SUCCESSFUL));
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e("Mtn.gs", error.getMessage());
            }
        });
    }

    public void login(String email, String pin) {
        loginService.login(new LoginRequest(email, pin), new Callback<LoginRequest>() {
            @Override
            public void success(LoginRequest loginResult, Response response) {
                if(loginResult.error != null) {
                    EventBus.getDefault().post(new ErrorEvent("Sorry!", loginResult.error.message));
                    return;
                }

                LoginRequest.User user = loginResult.result;
                BasicNameValuePair userData =  new BasicNameValuePair(user.user_id, user.token);

                EventBus.getDefault().post(new SessionEvent(SessionEvent.LOGIN_SUCCESSFUL, userData));
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e("Mtn.gs", error.getMessage());
            }
        });
    }
}
