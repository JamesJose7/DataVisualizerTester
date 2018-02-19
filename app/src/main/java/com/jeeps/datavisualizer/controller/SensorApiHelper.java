package com.jeeps.datavisualizer.controller;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jeeps.datavisualizer.DisplaySensorData;
import com.jeeps.datavisualizer.model.SensorData;

import org.json.JSONException;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Created by jeeps on 1/25/2018.
 */

public class SensorApiHelper {

    private Activity mActivity;

    private int mInterval = 10 * 1000;
    private Handler mHandler;
    private Runnable mStatusChecker;
    private final OkHttpClient client = new OkHttpClient();

    private SensorData mSensorData;
    private SensorDataParser mSensorDataParser;
    private SensorApiListener mListener;

    private DateFormat mDateFormat = new SimpleDateFormat("YYYY-MM-dd");

    private boolean isHumidityCallComplete = false;
    private boolean isTemperatureCallComplete = false;
    public static final int HUMIDITY = 0;
    public static final int TEMPERATURE = 1;

    public interface SensorApiListener {
        void update(SensorData sensorData);
    }

    public SensorApiHelper(SensorApiListener listener, Activity activity) {
        mListener = listener;
        mHandler = new Handler();
        mActivity = activity;
    }

    public void openConnection() {
        mStatusChecker = new Runnable() {
            @Override
            public void run() {
                try {
                    updateStatus();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    mHandler.postDelayed(mStatusChecker, mInterval);
                }
            }
        };

        startRepeatingTask();
    }

    private void updateStatus() throws Exception {
        mSensorData = new SensorData();
        mSensorDataParser = new SensorDataParser(mSensorData);
        requestApi();
    }

    public void startRepeatingTask() {
        mStatusChecker.run();
    }

    public void stopRepeatingTask() {
        mHandler.removeCallbacks(mStatusChecker);
    }

    public void requestApi() throws Exception {
        String currentDate = mDateFormat.format(new Date());

        //Humidity callback
        String humidityUrl = ApiBuilder.buildSensorUrl("node_01", ApiBuilder.HUMIDITY_SENSOR, currentDate);
        Request humidityRequest = new Request.Builder()
                .url(humidityUrl)
                .build();
        makeCall(humidityRequest, HUMIDITY);

        //Temperature callback
        String temperatureUrl = ApiBuilder.buildSensorUrl("node_01", ApiBuilder.TEMPERATURE_SENSOR, currentDate);
        Request temperatureRequest = new Request.Builder()
                .url(temperatureUrl)
                .build();
        makeCall(temperatureRequest, TEMPERATURE);

    }

    private void makeCall(Request request, final int type) {
        client.newCall(request).enqueue(new Callback() {
            @Override public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override public void onResponse(Call call, Response response) throws IOException {
                try (ResponseBody responseBody = response.body()) {
                    if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                    String jsonData = responseBody.string();

                    //Check type and act accordingly
                    switch (type) {
                        case HUMIDITY:
                            //Parse data
                            mSensorDataParser.parseHumidity(jsonData);

                            //Mark completion
                            isHumidityCallComplete = true;
                            break;
                        case TEMPERATURE:
                            //Parse data
                            mSensorDataParser.parseTemperature(jsonData);

                            //Mark completion
                            isTemperatureCallComplete = true;
                            break;
                        default:
                    }

                    checkCompletion();

                    /*Headers responseHeaders = response.headers();
                    for (int i = 0, size = responseHeaders.size(); i < size; i++) {
                        System.out.println(responseHeaders.name(i) + ": " + responseHeaders.value(i));
                    }

                    System.out.println(responseBody.string());*/
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void checkCompletion() {
        if (isHumidityCallComplete && isTemperatureCallComplete) {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mListener.update(mSensorDataParser.getSensorData());
                }
            });
        }
    }
}
