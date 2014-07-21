package gs.meetin.connector.adapters;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import gs.meetin.connector.Constants;
import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;

public class LoginAdapter {

    public static RestAdapter build () {
        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();

        return new RestAdapter.Builder()
                .setConverter(new GsonConverter(gson))
                .setEndpoint(Constants.apiBaseURL)
                .build();
    }
}
