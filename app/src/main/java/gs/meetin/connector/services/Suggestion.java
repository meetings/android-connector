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

public class Suggestion {

    private SuggestionService suggestionService;

    public interface SuggestionService {
        @POST("/users/:id/suggestion_sources/set_container_batch")
        void updateSources(@Body SourceContainer body, Callback<SourceContainer> cb);

        @POST("/users/:id/suggested_meetings/set_for_source_batch")
        void updateSuggestions(@Body CalendarSuggestion body, Callback<Response> cb);

    }

    public Suggestion(final String userId, final String token) {

        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();


        RestAdapter restAdapter = new RestAdapter.Builder()
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

        suggestionService = restAdapter.create(SuggestionService.class);
    }

    public void updateSources(SourceContainer sourceContainer) {
        suggestionService.updateSources(sourceContainer, new Callback<SourceContainer>() {
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
        suggestionService.updateSuggestions(suggestion, new Callback<Response>() {
            @Override
            public void success(Response result, Response response) {

            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }
}
