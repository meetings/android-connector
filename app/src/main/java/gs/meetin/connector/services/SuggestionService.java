package gs.meetin.connector.services;

import android.util.Log;

import com.google.gson.Gson;

import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.List;

import de.greenrobot.event.EventBus;
import gs.meetin.connector.dto.ApiError;
import gs.meetin.connector.dto.MtnResponse;
import gs.meetin.connector.dto.SourceContainer;
import gs.meetin.connector.dto.SuggestionBatch;
import gs.meetin.connector.dto.SuggestionSource;
import gs.meetin.connector.events.ErrorEvent;
import gs.meetin.connector.events.SuggestionEvent;
import gs.meetin.connector.events.UIEvent;
import gs.meetin.connector.utils.Device;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.mime.MimeUtil;
import retrofit.mime.TypedByteArray;
import retrofit.mime.TypedInput;

import static gs.meetin.connector.events.Event.EventType.SET_LAST_SYNC_TIME;
import static gs.meetin.connector.events.Event.EventType.SET_BUTTONS_ENABLED;

public class SuggestionService {

    private String userId;
    private SuggestionRouter suggestionService;

    public interface SuggestionRouter {
        @GET("/users/{userId}/suggestion_sources")
        void getSources(@Path("userId") String userId, Callback<List<SuggestionSource>> cb);

        @POST("/users/{userId}/suggestion_sources/set_container_batch")
        void updateSources(@Path("userId") String userId, @Body SourceContainer body, Callback<SourceContainer> cb);

        @POST("/users/{userId}/suggested_meetings/set_for_source_batch")
        void updateSuggestions(@Path("userId") String userId, @Body SuggestionBatch body, Callback<SuggestionBatch> cb);

    }

    public SuggestionService(RestAdapter restAdapter, String userId) {
        suggestionService = restAdapter.create(SuggestionRouter.class);
        this.userId = userId;
    }

    public void getSources() {
        suggestionService.getSources(userId, new Callback<List<SuggestionSource>>() {
            @Override
            public void success(List<SuggestionSource> result, Response response) {
                Log.d("Mtn.gs", "Fetched sources successfully");
                EventBus.getDefault().post(new UIEvent(SET_BUTTONS_ENABLED));

                long lastSync = 0;
                for(Iterator<SuggestionSource> i = result.iterator(); i.hasNext(); ) {
                    SuggestionSource source = i.next();
                    if(source.getContainerName().equals(Device.getDeviceName())) {
                        if(source.getLastUpdateEpoch() > lastSync) {
                            lastSync = source.getLastUpdateEpoch();
                        }
                    }
                }

                UIEvent uiEvent = new UIEvent(SET_LAST_SYNC_TIME);
                uiEvent.putLong("lastSync", lastSync);

                EventBus.getDefault().post(uiEvent);
            }

            @Override
            public void failure(RetrofitError error) {
                // TODO Move error handling to custom error handler
                Log.e("Mtn.gs", error.getMessage());

                TypedInput body = error.getResponse().getBody();

                byte[] bodyBytes = ((TypedByteArray) body).getBytes();

                String bodyMime = body.mimeType();
                String bodyCharset = MimeUtil.parseCharset(bodyMime);
                try {
                    String errorStr = new String(bodyBytes, bodyCharset);

                    MtnResponse response = new Gson().fromJson(errorStr, MtnResponse.class);
                    ApiError apiError = response.getError();

                    EventBus.getDefault().post(new ErrorEvent(apiError.code, "Sorry!", apiError.message));

                } catch (UnsupportedEncodingException e) {
                    Log.e("Mtn.gs", e.getMessage());
                }
            }
        });
    }

    public void updateSources(SourceContainer sourceContainer, final Callback cb) {
        suggestionService.updateSources(userId, sourceContainer, new Callback<SourceContainer>() {
            @Override
            public void success(SourceContainer result, Response response) {

                if(result.getError() != null) {
                    EventBus.getDefault().post(new ErrorEvent("Sorry!", result.getError().message));
                }

                Log.d("Mtn.gs", "Updated sources successfully");

                if (cb != null)
                    cb.success(null, null);
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e("Mtn.gs", error.getMessage());
                EventBus.getDefault().post(new ErrorEvent("Sorry!", error.getMessage()));
            }
        });
    }

    public void updateSuggestions(SuggestionBatch batch) {
        suggestionService.updateSuggestions(userId, batch, new Callback<SuggestionBatch>() {
            @Override
            public void success(SuggestionBatch result, Response response) {

                if(result.getError() != null) {
                    EventBus.getDefault().post(new ErrorEvent("Sorry!", result.getError().message));
                    return;
                }

                Log.d("Mtn.gs", "Suggestion batch updated successfully");
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e("Mtn.gs", error.getMessage());
                EventBus.getDefault().post(new ErrorEvent("Sorry!", error.getMessage()));
            }
        });
    }
}
