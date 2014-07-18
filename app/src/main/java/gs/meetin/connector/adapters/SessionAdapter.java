package gs.meetin.connector.adapters;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import gs.meetin.connector.Constants;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;

public class SessionAdapter {

    public static RestAdapter build (final String userId, final String token) {
        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();

        return new RestAdapter.Builder()
                .setRequestInterceptor(new RequestInterceptor() {
                    @Override
                    public void intercept(RequestFacade request) {
                        request.addHeader("user_id", userId);
                        request.addHeader("dic", token);
                    }
                })
                .setConverter(new GsonConverter(gson))
                .setEndpoint(Constants.apiBaseURL)
                .build();
    }
}
