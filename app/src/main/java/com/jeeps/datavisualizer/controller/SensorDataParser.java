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

    private Float[] weeklyTempMin = new Float[7];
    private Float[] weeklyTempMax = new Float[7];

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
        //Get min and max
        float[] minMax = getMinMax(data);
        //Weekly temp
        weeklyTempMin[0] = minMax[0];
        weeklyTempMax[0] = minMax[1];
    }

    public void parseWeeklyTemp(String jsonData, int dateCounter) throws JSONException {
        JSONObject mainObject = new JSONObject(jsonData);
        //Get data array
        JSONArray data = mainObject.getJSONArray("data");
        //Get temperature from first item
        String temperatureValue = data.getJSONObject(0).getString("valor");
        float temperature = Float.parseFloat(temperatureValue);

        //Set weekly temp
        //weeklyTemp[dateCounter] = temperature;
        //Get min and max
        float[] minMax = getMinMax(data);
        //Weekly temp
        weeklyTempMin[dateCounter] = minMax[0];
        weeklyTempMax[dateCounter] = minMax[1];
        //Set value to object
        mSensorData.setWeeklyTemperatureMax(Arrays.asList(weeklyTempMax));
        mSensorData.setWeeklyTemperatureMin(Arrays.asList(weeklyTempMin));
    }

    public SensorData getSensorData() {
        return mSensorData;
    }

    private float[] getMinMax(JSONArray array) throws JSONException {
        //Get first value
        float min = Float.parseFloat(array.getJSONObject(0).getString("valor"));
        float max = Float.parseFloat(array.getJSONObject(0).getString("valor"));
        for (int i = 1; i < array.length(); i++) {
            float currentValue = Float.parseFloat(array.getJSONObject(i).getString("valor"));
            if (currentValue < min)
                min = currentValue;
            if (currentValue > max)
                max = currentValue;
        }
        return new float[]{min, max};
    }
}
