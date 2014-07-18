package gs.meetin.connector.services;

import android.util.Log;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Objects;

import gs.meetin.connector.Constants;
import gs.meetin.connector.dto.ApiError;
import gs.meetin.connector.dto.CalendarSuggestion;
import gs.meetin.connector.dto.SourceContainer;
import gs.meetin.connector.dto.SuggestionSource;
import retrofit.Callback;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.converter.GsonConverter;
import retrofit.http.Body;
import retrofit.http.POST;
import retrofit.http.Path;

public class SuggestionService {

    private String userId;
    private SuggestionRouter suggestionService;

    public interface SuggestionRouter {
        @POST("/users/{userId}/suggestion_sources/set_container_batch")
        void updateSources(@Path("userId") String userId, @Body SourceContainer body, Callback<SourceContainer> cb);

        @POST("/users/{userId}/suggested_meetings/set_for_source_batch")
        void updateSuggestions(@Path("userId") String userId, @Body CalendarSuggestion body, Callback<Response> cb);

    }

    public SuggestionService(RestAdapter restAdapter) {
        suggestionService = restAdapter.create(SuggestionRouter.class);
    }

    public void updateSources(SourceContainer sourceContainer) {
        suggestionService.updateSources(userId, sourceContainer, new Callback<SourceContainer>() {
            @Override
            public void success(SourceContainer result, Response response) {
                ApiError r = result.error;
                Log.d("Mtn.gs", "Update successful");
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

    public void updateSuggestions(CalendarSuggestion suggestion) {
        suggestionService.updateSuggestions(userId, suggestion, new Callback<Response>() {
            @Override
            public void success(Response result, Response response) {

            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }
}
