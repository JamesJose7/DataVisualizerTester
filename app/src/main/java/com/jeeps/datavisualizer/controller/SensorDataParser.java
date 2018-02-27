package com.jeeps.datavisualizer.controller;

import com.jeeps.datavisualizer.model.SensorData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

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

        //Get hourly temperature
        SimpleDateFormat hourFormatter = new SimpleDateFormat("H", Locale.US);
        //Get last 7 hours
        int[] lastHours = new int[7];
        float[] hourlyTemperature = new float[7];
        for (int i = 0; i < lastHours.length; i++) {
            String hour = hourFormatter.format(getPreviousHourDate(i));
            lastHours[i] = Integer.parseInt(hour);
        }
        //Get the temperature of each hour
        for (int i = 0; i < data.length(); i++) {
            JSONObject jsonObject = data.getJSONObject(i);
            //Get current temperature
            Float hourTemperature = Float.parseFloat(jsonObject.getString("valor"));
            //Get saved hour
            String time = jsonObject.getString("hora");
            int hour = Integer.parseInt(time.split(":")[0]);

            //Save each temperature on it's according hour
            for (int j = 0; j < hourlyTemperature.length; j++) {
                if (hour == lastHours[j])
                    hourlyTemperature[j] = hourTemperature;
            }
        }
        Float[] newHourlyTemp = new Float[hourlyTemperature.length];
        for (int i = 0; i < hourlyTemperature.length; i++)
            newHourlyTemp[i] = hourlyTemperature[i];
        mSensorData.setHourlyTemperature(Arrays.asList(newHourlyTemp));
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
