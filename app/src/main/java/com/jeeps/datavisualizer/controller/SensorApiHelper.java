package com.jeeps.datavisualizer.controller;

import android.app.Activity;
import android.os.Handler;

import com.jeeps.datavisualizer.DisplaySensorData;
import com.jeeps.datavisualizer.model.SensorData;

import org.json.JSONException;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
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
    private boolean isLuminosityCallComplete = false;
    private boolean isHourlyTempComplete = false;
    private boolean isWeeklyTempComplete = false;
    private boolean isHourlyHumComplete = false;
    private boolean isWeeklyHumComplete = false;
    private boolean isHourlyLumComplete = false;
    private boolean isWeeklyLumComplete = false;

    protected static final int HUMIDITY = 0;
    protected static final int TEMPERATURE = 1;
    protected static final int LUMINOSITY = 2;
    protected static final int HOURLY_TEMP = 3;
    protected static final int WEEKLY_TEMP = 4;
    protected static final int HOURLY_HUM = 5;
    protected static final int WEEKLY_HUM = 6;
    protected static final int HOURLY_LUM = 7;
    protected static final int WEEKLY_LUM = 8;

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

    public void openConnection(Date date) {
        try {
            mListener.started();
            updateStatus(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateStatus(Date date) throws Exception {
        mSensorData = new SensorData();
        mSensorDataParser = new SensorDataParser(mSensorData);
        requestApi(date);
    }

    public void requestApi(Date date) throws Exception {
        String currentDate = mDateFormat.format(new Date());
        String chosenDate = mDateFormat.format(date);

        //Current Humidity callback
        String humidityUrl = ApiBuilder.buildSensorUrl("node_01", ApiBuilder.HUMIDITY_SENSOR, chosenDate);
        Request humidityRequest = new Request.Builder()
                .url(humidityUrl)
                .build();
        makeCall(humidityRequest, HUMIDITY);

        //Current Temperature callback
        String temperatureUrl = ApiBuilder.buildSensorUrl("node_01", ApiBuilder.TEMPERATURE_SENSOR, chosenDate);
        Request temperatureRequest = new Request.Builder()
                .url(temperatureUrl)
                .build();
        makeCall(temperatureRequest, TEMPERATURE);

        //Current Luminosity callback
        String luminosityUrl = ApiBuilder.buildSensorUrl("node_01", ApiBuilder.LUMINOSITY_SENSOR, chosenDate);
        Request luminosityRequest = new Request.Builder()
                .url(luminosityUrl)
                .build();
        makeCall(luminosityRequest, LUMINOSITY);

        /* GRAPHS */
        String previousDate = mDateFormat.format(SensorDataParser.getPreviousDayDate(6));
        //Hourly Temperature callback
        String hourlyTemperatureUrl = ApiBuilder.buildValuesByHourUrl("node_01", ApiBuilder.TEMPERATURE_SENSOR, currentDate);
        Request hourlyTemperatureRequest = new Request.Builder()
                .url(hourlyTemperatureUrl)
                .build();
        makeCall(hourlyTemperatureRequest, HOURLY_TEMP);

        //Weekly Temperature callback
        String weeklyTemperatureUrl = ApiBuilder.buildValuesByDateRangeUrl("node_01", ApiBuilder.TEMPERATURE_SENSOR, previousDate, currentDate);
        Request weeklyTemperatureRequest = new Request.Builder()
                .url(weeklyTemperatureUrl)
                .build();
        makeCall(weeklyTemperatureRequest, WEEKLY_TEMP);

        //Hourly Humidity callback
        String hourlyHumidityUrl = ApiBuilder.buildValuesByHourUrl("node_01", ApiBuilder.HUMIDITY_SENSOR, currentDate);
        Request hourlyHumidityRequest = new Request.Builder()
                .url(hourlyHumidityUrl)
                .build();
        makeCall(hourlyHumidityRequest, HOURLY_HUM);

        //Weekly Humidity callback
        String weeklyHumidityUrl = ApiBuilder.buildValuesByDateRangeUrl("node_01", ApiBuilder.HUMIDITY_SENSOR, previousDate, currentDate);
        Request weeklyHumidityRequest = new Request.Builder()
                .url(weeklyHumidityUrl)
                .build();
        makeCall(weeklyHumidityRequest, WEEKLY_HUM);

        //Hourly Luminosity callback
        String hourlyLuminosityUrl = ApiBuilder.buildValuesByHourUrl("node_01", ApiBuilder.LUMINOSITY_SENSOR, currentDate);
        Request hourlyLuminosityRequest = new Request.Builder()
                .url(hourlyLuminosityUrl)
                .build();
        makeCall(hourlyLuminosityRequest, HOURLY_LUM);

        //Weekly Luminosity callback
        String weeklyLuminosityUrl = ApiBuilder.buildValuesByDateRangeUrl("node_01", ApiBuilder.LUMINOSITY_SENSOR, previousDate, currentDate);
        Request weeklyLuminosityRequest = new Request.Builder()
                .url(weeklyLuminosityUrl)
                .build();
        makeCall(weeklyLuminosityRequest, WEEKLY_LUM);
    }

    public SensorData requestPreviousDayData(Date date, int type) throws IOException, JSONException {
        String formattedDate = mDateFormat.format(date);

        //Get appropriate graph
        int selectedGraph = HOURLY_TEMP;
        String sensor = ApiBuilder.TEMPERATURE_SENSOR;
        if (type == DisplaySensorData.HUM_GRAPH) {
            selectedGraph = HOURLY_HUM;
            sensor = ApiBuilder.HUMIDITY_SENSOR;
        } else if (type == DisplaySensorData.LUM_GRAPH) {
            selectedGraph = HOURLY_LUM;
            sensor = ApiBuilder.LUMINOSITY_SENSOR;
        }

        //New sensor data to be returned
        SensorData sensorData = new SensorData();
        SensorDataParser sensorDataParser = new SensorDataParser(sensorData);

        //callback
        String temperatureUrl = ApiBuilder.buildValuesByHourUrl("node_01", sensor, formattedDate);
        Request request = new Request.Builder()
                .url(temperatureUrl)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            sensorDataParser.parseHourlyValues(response.body().string(), selectedGraph);
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
                        case LUMINOSITY:
                            //Parse data
                            mSensorDataParser.parseLuminosity(jsonData);
                            //Mark completion
                            isLuminosityCallComplete = true;
                            break;

                            /* GRAPH */
                        case HOURLY_TEMP:
                            //Parse data
                            mSensorDataParser.parseHourlyValues(jsonData, HOURLY_TEMP);
                            //Mark completion
                            isHourlyTempComplete = true;
                            break;
                        case WEEKLY_TEMP:
                            //Parse data
                            mSensorDataParser.parseWeeklyValues(jsonData, WEEKLY_TEMP);
                            //Mark completion
                            isWeeklyTempComplete = true;
                            break;
                        case HOURLY_HUM:
                            //Parse data
                            mSensorDataParser.parseHourlyValues(jsonData, HOURLY_HUM);
                            //Mark completion
                            isHourlyHumComplete = true;
                            break;
                        case WEEKLY_HUM:
                            //Parse data
                            mSensorDataParser.parseWeeklyValues(jsonData, WEEKLY_HUM);
                            //Mark completion
                            isWeeklyHumComplete = true;
                            break;
                        case HOURLY_LUM:
                            //Parse data
                            mSensorDataParser.parseHourlyValues(jsonData, HOURLY_LUM);
                            //Mark completion
                            isHourlyLumComplete = true;
                            break;
                        case WEEKLY_LUM:
                            //Parse data
                            mSensorDataParser.parseWeeklyValues(jsonData, WEEKLY_LUM);
                            //Mark completion
                            isWeeklyLumComplete = true;
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
        if (isHumidityCallComplete && isTemperatureCallComplete && isLuminosityCallComplete
                && isHourlyTempComplete && isWeeklyTempComplete
                && isHourlyHumComplete && isWeeklyHumComplete
                && isHourlyLumComplete && isWeeklyLumComplete) {
            isHumidityCallComplete = false;
            isTemperatureCallComplete = false;
            isLuminosityCallComplete = false;
            isHourlyTempComplete = false;
            isWeeklyTempComplete = false;
            isHourlyHumComplete = false;
            isWeeklyHumComplete = false;
            isHourlyLumComplete = false;
            isWeeklyLumComplete = false;

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
