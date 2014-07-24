package gs.meetin.connector.services;


import android.util.Log;

import org.apache.http.message.BasicNameValuePair;

import de.greenrobot.event.EventBus;
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

import static gs.meetin.connector.events.Event.EventType.LOGIN_SUCCESSFUL;
import static gs.meetin.connector.events.Event.EventType.PIN_REQUEST_SUCCESSFUL;

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
            public void success(PinRequest result, Response response) {
                if(result.getError() != null) {
                    EventBus.getDefault().post(new ErrorEvent(result.getError().code, "Sorry!", result.getError().message));
                    return;
                }

                EventBus.getDefault().post(new SessionEvent(PIN_REQUEST_SUCCESSFUL));
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e("Mtn.gs", error.getMessage());
                EventBus.getDefault().post(new ErrorEvent("Sorry!", error.getMessage()));
            }
        });
    }

    public void login(String email, String pin) {
        loginService.login(new LoginRequest(email, pin), new Callback<LoginRequest>() {
            @Override
            public void success(LoginRequest result, Response response) {
                if(result.getError() != null) {
                    EventBus.getDefault().post(new ErrorEvent("Sorry!", result.getError().message));
                    return;
                }

                LoginRequest.User user = result.result;
                BasicNameValuePair userData =  new BasicNameValuePair(user.userId, user.token);

                EventBus.getDefault().post(new SessionEvent(LOGIN_SUCCESSFUL, userData));
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e("Mtn.gs", error.getMessage());
                EventBus.getDefault().post(new ErrorEvent("Sorry!", error.getMessage()));
            }
        });
    }
}
