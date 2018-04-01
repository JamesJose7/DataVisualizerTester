package com.jeeps.datavisualizer.controller;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

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
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

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
    private int mInterval = 20 * 1000;

    private Handler mHandler;
    private Runnable mStatusChecker;
    private OkHttpClient client = new OkHttpClient();
    private SensorData mSensorData;

    private SensorDataParser mSensorDataParser;
    private SensorApiListener mListener;
    private DateFormat mDateFormat = new SimpleDateFormat("YYYY-MM-dd");

    private boolean isHumidityCallComplete = false;
    private boolean isTemperatureCallComplete = false;
    private boolean isHourlyTempComplete = false;
    private boolean isWeeklyTempComplete = false;
    private int sumCheck = 0;
    public static final int HUMIDITY = 0;
    public static final int TEMPERATURE = 1;
    private static final int HOURLY_TEMP = 2;
    private static final int WEEKLY_TEMP = 3;

    public interface SensorApiListener {
        void update(SensorData sensorData);
        void started();
        void finished();
    }

    public SensorApiHelper(SensorApiListener listener, Activity activity) {
        mListener = listener;
        mHandler = new Handler();
        mActivity = activity;
        client = new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .build();
    }

    public void openConnection() {
        try {
            mListener.started();
            updateStatus();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateStatus() throws Exception {
        mSensorData = new SensorData();
        mSensorDataParser = new SensorDataParser(mSensorData);
        requestApi();
    }

    public void requestApi() throws Exception {
        String currentDate = mDateFormat.format(new Date());

        //Current Humidity callback
        String humidityUrl = ApiBuilder.buildSensorUrl("node_01", ApiBuilder.HUMIDITY_SENSOR, currentDate);
        Request humidityRequest = new Request.Builder()
                .url(humidityUrl)
                .build();
        makeCall(humidityRequest, HUMIDITY);

        //Current Temperature callback
        String temperatureUrl = ApiBuilder.buildSensorUrl("node_01", ApiBuilder.TEMPERATURE_SENSOR, currentDate);
        Request temperatureRequest = new Request.Builder()
                .url(temperatureUrl)
                .build();
        makeCall(temperatureRequest, TEMPERATURE);

        //Hourly Temperature callback
        String hourlyTemperatureUrl = ApiBuilder.buildValuesByHourUrl("node_01", ApiBuilder.TEMPERATURE_SENSOR, currentDate);
        Request hourlyTemperatureRequest = new Request.Builder()
                .url(hourlyTemperatureUrl)
                .build();
        makeCall(hourlyTemperatureRequest, HOURLY_TEMP);

        //Weekly Temperature callback
        String previousDate = mDateFormat.format(SensorDataParser.getPreviousDayDate(6));
        String weeklyTemperatureUrl = ApiBuilder.buildValuesByDateRangeUrl("node_01", ApiBuilder.TEMPERATURE_SENSOR, previousDate, currentDate);
        Request weeklyTemperatureRequest = new Request.Builder()
                .url(weeklyTemperatureUrl)
                .build();
        makeCall(weeklyTemperatureRequest, WEEKLY_TEMP);    
    }

    public SensorData requestPreviousDayData(Date date) throws IOException, JSONException {
        String formattedDate = mDateFormat.format(date);

        //New sensor data to be returned
        SensorData sensorData = new SensorData();
        SensorDataParser sensorDataParser = new SensorDataParser(sensorData);

        //Temperature callback
        String temperatureUrl = ApiBuilder.buildSensorUrl("node_01", ApiBuilder.TEMPERATURE_SENSOR, formattedDate);
        Request request = new Request.Builder()
                .url(temperatureUrl)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            sensorDataParser.parseTemperature(response.body().string());
            return sensorDataParser.getSensorData();
        }
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
                        case HOURLY_TEMP:
                            //Parse data
                            mSensorDataParser.parseHourlyTemperature(jsonData);

                            //Mark completion
                            isHourlyTempComplete = true;
                            break;
                        case WEEKLY_TEMP:
                            //Parse data
                            mSensorDataParser.parseWeeklyTemp(jsonData);

                            //Mark completion
                            isWeeklyTempComplete = true;
                            break;
                        default:
                    }

                    checkCompletion();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void checkCompletion() {
        if (isHumidityCallComplete && isTemperatureCallComplete && isWeeklyTempComplete) {
            isHumidityCallComplete = false;
            isTemperatureCallComplete = false;
            isWeeklyTempComplete = false;

            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mListener.update(mSensorDataParser.getSensorData());
                }
            });
            mListener.finished();
        }
    }
}
