package com.jeeps.datavisualizer.controller;

import com.jeeps.datavisualizer.model.SensorData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

/**
 * Created by jeeps on 2/19/2018.
 */

public class SensorDataParser {
    private SensorData mSensorData;

    private Float[] weeklyTemp = new Float[7];

    public SensorDataParser(SensorData sensorData) {
        mSensorData = sensorData;
    }

    public void parseHumidity(String jsonData) throws JSONException {
        JSONObject mainObject = new JSONObject(jsonData);
        //Get data array
        JSONArray data = mainObject.getJSONArray("data");
        //Get humidity from first item
        String humidityValue = data.getJSONObject(0).getString("valor");
        double humidity = Double.parseDouble(humidityValue);

        //Set value to object
        mSensorData.setHumidity(humidity);
    }

    public void parseTemperature(String jsonData) throws JSONException {
        JSONObject mainObject = new JSONObject(jsonData);
        //Get data array
        JSONArray data = mainObject.getJSONArray("data");
        //Get temperature from first item
        String temperatureValue = data.getJSONObject(0).getString("valor");
        double temperature = Double.parseDouble(temperatureValue);

        //Set value to object
        mSensorData.setTemperature(temperature);
        //Weekly temp
        weeklyTemp[0] = (float) temperature;
    }

    public void parseWeeklyTemp(String jsonData, int dateCounter) throws JSONException {
        JSONObject mainObject = new JSONObject(jsonData);
        //Get data array
        JSONArray data = mainObject.getJSONArray("data");
        //Get temperature from first item
        String temperatureValue = data.getJSONObject(0).getString("valor");
        float temperature = Float.parseFloat(temperatureValue);

        //Set weekly temp
        weeklyTemp[dateCounter] = temperature;
        //Set value to object
        mSensorData.setWeeklyTemperatureMax(Arrays.asList(weeklyTemp));
        mSensorData.setWeeklyTemperatureMin(Arrays.asList(weeklyTemp));
    }

    public SensorData getSensorData() {
        return mSensorData;
    }
}
