package com.jeeps.datavisualizer.controller;

import com.jeeps.datavisualizer.model.SensorData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.jeeps.datavisualizer.controller.SensorApiHelper.*;

/**
 * Created by jeeps on 2/19/2018.
 */

public class SensorDataParser {
    private SensorData mSensorData;
    private SimpleDateFormat currentTimeFormatter = new SimpleDateFormat("HH");
    private String currentTime;

    public SensorDataParser(SensorData sensorData) {
        mSensorData = sensorData;
        currentTime = currentTimeFormatter.format(new Date());
    }

    public void parseHumidity(String jsonData) throws JSONException {
        JSONObject mainObject = new JSONObject(jsonData);
        //Get data array
        JSONArray data = mainObject.getJSONArray("data");
        //Get humidity from first item
        String humidityValue = data.getJSONObject(data.length() - 1).getString("valor");
        //Get register time
        for (int i = data.length() - 1; i >= 0; i--) {
            String register = data.getJSONObject(i).getString("register");
            if (register.contains(" " + currentTime + ":")) {
                humidityValue = data.getJSONObject(i).getString("valor");
                break;
            }
        }

        double humidity = Double.parseDouble(humidityValue);

        //Set value to object
        mSensorData.setHumidity(humidity);
    }

    public void parseTemperature(String jsonData) throws JSONException {
        JSONObject mainObject = new JSONObject(jsonData);
        //Get data array
        JSONArray data = mainObject.getJSONArray("data");
        //Get temperature from first item
        String temperatureValue = data.getJSONObject(data.length() - 1).getString("valor");
        //Get register time
        for (int i = data.length() - 1; i >= 0; i--) {
            String register = data.getJSONObject(i).getString("register");
            if (register.contains(" " + currentTime + ":")) {
                temperatureValue = data.getJSONObject(i).getString("valor");
                break;
            }
        }

        double temperature = Double.parseDouble(temperatureValue);

        //Set value to object
        mSensorData.setTemperature(temperature);
    }

    public void parseLuminosity(String jsonData) throws JSONException{
        JSONObject mainObject = new JSONObject(jsonData);
        //Get data array
        JSONArray data = mainObject.getJSONArray("data");
        //Get temperature from first item
        String luminosityValue = data.getJSONObject(data.length() - 1).getString("valor");
        //Get register time
        for (int i = data.length() - 1; i >= 0; i--) {
            String register = data.getJSONObject(i).getString("register");
            if (register.contains(" " + currentTime + ":")) {
                luminosityValue = data.getJSONObject(i).getString("valor");
                break;
            }
        }

        double luminosity = Double.parseDouble(luminosityValue);

        //Set value to object
        mSensorData.setLuminosity(luminosity);
    }

    public void parseHourlyValues(String jsonData, int type) throws JSONException {
        JSONObject mainObject = new JSONObject(jsonData);
        //Get data array
        JSONArray data = mainObject.getJSONArray("data");

        List<Float> maxs = new ArrayList<>();
        List<Float> mins = new ArrayList<>();

        for (int i = 0; i < data.length(); i++) {
            JSONObject jsonObject = data.getJSONObject(i);
            String valueMax = jsonObject.getString("value_max");
            String valueMin = jsonObject.getString("value_min");

            maxs.add(Float.parseFloat(valueMax));
            mins.add(Float.parseFloat(valueMin));
        }

        switch (type) {
            case HOURLY_TEMP:
                mSensorData.setHourlyTemperature(maxs);
                break;
            case HOURLY_HUM:
                mSensorData.setHourlyHumidity(maxs);
                break;
            case HOURLY_LUM:
                mSensorData.setHourlyLuminosity(maxs);
                break;
        }
    }

    public void parseWeeklyValues(String jsonData, int type) throws JSONException {
        JSONObject mainObject = new JSONObject(jsonData);
        //Get data array
        JSONArray data = mainObject.getJSONArray("data");

        List<Float> mins = new ArrayList<>();
        List<Float> maxs = new ArrayList<>();

        for (int i = 0; i < data.length(); i++) {
            JSONObject currentObject = data.getJSONObject(i);
            //Get min and max
            String max = currentObject.getString("value_max");
            String min = currentObject.getString("value_min");

            maxs.add(Float.parseFloat(max));
            mins.add(Float.parseFloat(min));
        }

        //Set value to object
        switch (type) {
            case WEEKLY_TEMP:
                mSensorData.setWeeklyTemperatureMax(maxs);
                mSensorData.setWeeklyTemperatureMin(mins);
                break;
            case WEEKLY_HUM:
                mSensorData.setWeeklyHumidityMax(maxs);
                mSensorData.setWeeklyHumidityMin(mins);
                break;
            case WEEKLY_LUM:
                mSensorData.setWeeklyLuminosityMax(maxs);
                mSensorData.setWeeklyLuminosityMin(mins);
                break;
        }
    }

    public SensorData getSensorData() {
        return mSensorData;
    }

    public static Date getPreviousDayDate(int previousDays) {
        final Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, - previousDays);
        return cal.getTime();
    }

    public static Date getPreviousHourDate(int previousHours) {
        final Calendar cal = Calendar.getInstance();
        cal.add(Calendar.HOUR_OF_DAY, - previousHours);
        return cal.getTime();
    }
}
