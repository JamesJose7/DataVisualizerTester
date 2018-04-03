package com.jeeps.datavisualizer.controller;

import com.jeeps.datavisualizer.model.SensorData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by jeeps on 2/19/2018.
 */

public class SensorDataParser {
    private SensorData mSensorData;

    public SensorDataParser(SensorData sensorData) {
        mSensorData = sensorData;
    }

    public void parseHumidity(String jsonData) throws JSONException {
        JSONObject mainObject = new JSONObject(jsonData);
        //Get data array
        JSONArray data = mainObject.getJSONArray("data");
        //Get humidity from first item
        String humidityValue = data.getJSONObject(data.length() - 1).getString("valor");
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
        double luminosity = Double.parseDouble(luminosityValue);

        //Set value to object
        mSensorData.setLuminosity(luminosity);
    }

    public void parseHourlyTemperature(String jsonData) throws JSONException {
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

        mSensorData.setHourlyTemperature(maxs);
    }

    public void parseWeeklyTemp(String jsonData) throws JSONException {
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
        mSensorData.setWeeklyTemperatureMax(maxs);
        mSensorData.setWeeklyTemperatureMin(mins);
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
