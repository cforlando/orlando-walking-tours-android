package com.codefororlando.orlandowalkingtours.data.repository;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.codefororlando.orlandowalkingtours.data.DatabaseHelper;
import com.codefororlando.orlandowalkingtours.data.model.HistoricLandmark;
import com.codefororlando.orlandowalkingtours.data.model.RemoteLandmark;
import com.codefororlando.orlandowalkingtours.event.OnQueryLandmarksEvent;
import com.codefororlando.orlandowalkingtours.event.RxBus;
import com.codefororlando.orlandowalkingtours.log.Logger;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class LandmarkRepositoryImpl
        implements LandmarkRepository,
        Response.Listener<List<RemoteLandmark>>,
        Response.ErrorListener {
    private final DatabaseHelper databaseHelper;
    private final RequestQueue requestQueue;
    private final RxBus bus;
    private final Logger logger;

    // Landmarks for a given city is low and should not stress device memory therefore can be cached
    private final Map<Long, HistoricLandmark> cache = new ConcurrentHashMap<>();
    private final Map<String, List<HistoricLandmark>> queryCache = new ConcurrentHashMap<>();

    private final Action1<List<HistoricLandmark>> landmarkPublisher =
            new Action1<List<HistoricLandmark>>() {
                @Override
                public void call(List<HistoricLandmark> landmarks) {
                    bus.publish(new OnQueryLandmarksEvent(landmarks));
                }
            };

    public LandmarkRepositoryImpl(DatabaseHelper databaseHelper,
                                  RequestQueue requestQueue,
                                  RxBus rxBus,
                                  Logger logger) {
        this.databaseHelper = databaseHelper;
        this.requestQueue = requestQueue;
        bus = rxBus;
        this.logger = logger;
    }

    @Override
    public void load() {
        queryCache.clear();

        Observable.create(new Observable.OnSubscribe<List<HistoricLandmark>>() {
            @Override
            public void call(Subscriber<? super List<HistoricLandmark>> subscriber) {
                subscriber.onNext(getLandmarks(""));
                subscriber.onCompleted();
            }
        })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(landmarkPublisher);
    }

    @Override
    public List<HistoricLandmark> getLandmarks(String query) {
        // Default query is blank string
        if (query == null) {
            query = "";
        }

        synchronized (queryCache) {
            if (queryCache.containsKey(query)) {
                return queryCache.get(query);
            }
        }

        List<HistoricLandmark> landmarks = databaseHelper.getLandmarks(query);

        // Landmarks have been downloaded or result set is empty
        if (queryCache.size() > 0 || landmarks.size() > 0) {
            cacheLandmarks(query, landmarks);
            return landmarks;
        }

        logger.debug("Downloading remote landmarks");

        // Download landmarks
        RemoteLandmarkRequest remoteDownloadRequest =
                new RemoteLandmarkRequest(LandmarkRepositoryImpl.this, LandmarkRepositoryImpl.this);
        requestQueue.add(remoteDownloadRequest);
        return new ArrayList<>(0);
    }

    @Override
    public HistoricLandmark getLandmark(long id) {
        HistoricLandmark landmark = cache.get(id);
        // Either landmarks have not been queried or landmarks have changed
        if (landmark == null) {
            logger.error("Unknown landmark", new Exception(id + ""));
        }
        return landmark;
    }

    private void cacheLandmarks(String query, List<HistoricLandmark> landmarks) {
        for (HistoricLandmark landmark : landmarks) {
            cache.put(landmark.id, landmark);
        }
        queryCache.put(query, landmarks);
    }

    // Response.Listener<List<RemoteLandmark>>

    @Override
    public void onResponse(List<RemoteLandmark> response) {
        Observable.just(response)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Func1<List<RemoteLandmark>, List<HistoricLandmark>>() {
                    @Override
                    public List<HistoricLandmark> call(List<RemoteLandmark> remoteLandmarks) {
                        List<HistoricLandmark> landmarks =
                                databaseHelper.saveLandmarks(remoteLandmarks);
                        cacheLandmarks("", landmarks);
                        return landmarks;
                    }
                })
                .subscribe(landmarkPublisher);
    }

    // Response.ErrorListener

    @Override
    public void onErrorResponse(VolleyError error) {
        logger.error("Network error", error);
        // TODO Send event for UI to react
    }

    public static class RemoteLandmarkRequest extends Request<List<RemoteLandmark>> {
        private final Response.Listener<List<RemoteLandmark>> listener;

        public RemoteLandmarkRequest(Response.Listener<List<RemoteLandmark>> listener,
                                     Response.ErrorListener errorListener) {
            super(Method.GET, RemoteLandmark.DATA_URL, errorListener);
            this.listener = listener;
        }

        @Override
        protected Response<List<RemoteLandmark>> parseNetworkResponse(NetworkResponse response) {
            try {
                String json = new String(
                        response.data,
                        HttpHeaderParser.parseCharset(response.headers));
                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS").create();
                JsonObject root = gson.fromJson(json, JsonObject.class);
                List<RemoteLandmark> data = new ArrayList<>();
                for (Map.Entry<String, JsonElement> entry : root.entrySet()) {
                    String city = entry.getKey();
                    JsonObject cityData = entry.getValue().getAsJsonObject();
                    for (Map.Entry<String, JsonElement> landmarkEntry : cityData.entrySet()) {
                        String id = landmarkEntry.getKey();
                        JsonObject landmarkData = landmarkEntry.getValue().getAsJsonObject();
                        landmarkData.addProperty("id", id);
                        data.add(gson.fromJson(landmarkData, RemoteLandmark.class));
                    }
                }
                return Response.success(data, HttpHeaderParser.parseCacheHeaders(response));
            } catch (UnsupportedEncodingException e) {
                return Response.error(new ParseError(e));
            } catch (JsonSyntaxException e) {
                return Response.error(new ParseError(e));
            }
        }

        @Override
        protected void deliverResponse(List<RemoteLandmark> response) {
            listener.onResponse(response);
        }
    }
}
