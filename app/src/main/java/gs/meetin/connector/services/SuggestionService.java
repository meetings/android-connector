package gs.meetin.connector.services;

import android.util.Log;

import de.greenrobot.event.EventBus;
import gs.meetin.connector.dto.SourceContainer;
import gs.meetin.connector.dto.SuggestionBatch;
import gs.meetin.connector.events.ErrorEvent;
import gs.meetin.connector.events.SuggestionEvent;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.POST;
import retrofit.http.Path;

import static gs.meetin.connector.events.Event.EventType.UPDATE_SUGGESTIONS;

public class SuggestionService {

    private String userId;
    private SuggestionRouter suggestionService;

    public interface SuggestionRouter {
        @POST("/users/{userId}/suggestion_sources/set_container_batch")
        void updateSources(@Path("userId") String userId, @Body SourceContainer body, Callback<SourceContainer> cb);

        @POST("/users/{userId}/suggested_meetings/set_for_source_batch")
        void updateSuggestions(@Path("userId") String userId, @Body SuggestionBatch body, Callback<SuggestionBatch> cb);

    }

    public SuggestionService(RestAdapter restAdapter, String userId) {
        suggestionService = restAdapter.create(SuggestionRouter.class);
        this.userId = userId;
    }

    public void updateSources(SourceContainer sourceContainer) {
        suggestionService.updateSources(userId, sourceContainer, new Callback<SourceContainer>() {
            @Override
            public void success(SourceContainer result, Response response) {

                if(result.error != null) {
                    EventBus.getDefault().post(new ErrorEvent("Sorry!", result.error.message));
                    return;
                }

                Log.d("Mtn.gs", "Updated sources successfully");
                EventBus.getDefault().post(new SuggestionEvent(UPDATE_SUGGESTIONS));


            }

            @Override
            public void failure(RetrofitError error) {
                Log.e("Mtn.gs", error.getMessage());
            }
        });
    }

    public void updateSuggestions(SuggestionBatch batch) {
        suggestionService.updateSuggestions(userId, batch, new Callback<SuggestionBatch>() {
            @Override
            public void success(SuggestionBatch result, Response response) {

                if(result.error != null) {
                    EventBus.getDefault().post(new ErrorEvent("Sorry!", result.error.message));
                    return;
                }

                Log.d("Mtn.gs", "Suggestion batch updated successfully");
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }
}
