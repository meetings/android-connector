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
import gs.meetin.connector.events.UIEvent;
import gs.meetin.connector.utils.Device;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.Headers;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.mime.MimeUtil;
import retrofit.mime.TypedByteArray;
import retrofit.mime.TypedInput;

import static gs.meetin.connector.events.Event.EventType.SET_BUTTONS_ENABLED;
import static gs.meetin.connector.events.Event.EventType.SET_LAST_SYNC_TIME;

public class SuggestionService {

    private String userId;
    private SuggestionRouter suggestionService;

    public interface SuggestionRouter {
        @Headers( { "x-meetings-unmanned: {unmanned}" } )
        @GET("/users/{userId}/suggestion_sources")
        void getSources(@Header("unmanned") String unmanned, @Path("userId") String userId, Callback<List<SuggestionSource>> cb);

        @Headers( { "x-meetings-unmanned: {unmanned}" } )
        @POST("/users/{userId}/suggestion_sources/set_container_batch")
        void updateSources(@Header("unmanned") String unmanned, @Path("userId") String userId, @Body SourceContainer body, Callback<SourceContainer> cb);

        @Headers( { "x-meetings-unmanned: {unmanned}" } )
        @POST("/users/{userId}/suggested_meetings/set_for_source_batch")
        void updateSuggestions(@Header("unmanned") String unmanned, @Path("userId") String userId, @Body SuggestionBatch body, Callback<SuggestionBatch> cb);

    }

    public SuggestionService(RestAdapter restAdapter, String userId) {
        suggestionService = restAdapter.create(SuggestionRouter.class);
        this.userId = userId;
    }

    public void getSources(short unmanned) {
        suggestionService.getSources(String.valueOf(unmanned), userId, new Callback<List<SuggestionSource>>() {
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

    public void updateSources(short unmanned, SourceContainer sourceContainer, final Callback cb) {
        suggestionService.updateSources(String.valueOf(unmanned), userId, sourceContainer, new Callback<SourceContainer>() {
            @Override
            public void success(SourceContainer result, Response response) {

                if(result.getError() != null) {
                    EventBus.getDefault().post(new ErrorEvent("Sorry!", result.getError().message));
                }

                Log.d("Mtn.gs", "Updated sources successfully");

                if (cb != null)
                    // Null parameters because only information about successful request is needed
                    cb.success(null, null);
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e("Mtn.gs", error.getMessage());
                EventBus.getDefault().post(new ErrorEvent("Sorry!", error.getMessage()));
            }
        });
    }

    public void updateSuggestions(short unmanned, SuggestionBatch batch, final Callback cb) {
        suggestionService.updateSuggestions(String.valueOf(unmanned), userId, batch, new Callback<SuggestionBatch>() {
            @Override
            public void success(SuggestionBatch result, Response response) {

                if(result.getError() != null) {
                    EventBus.getDefault().post(new ErrorEvent("Sorry!", result.getError().message));
                    return;
                }

                Log.d("Mtn.gs", "Suggestion batch updated successfully");

                // Null parameters because only information about successful request is needed
                cb.success(null, null);
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e("Mtn.gs", error.getMessage());
                EventBus.getDefault().post(new ErrorEvent("Sorry!", error.getMessage()));
            }
        });
    }
}
