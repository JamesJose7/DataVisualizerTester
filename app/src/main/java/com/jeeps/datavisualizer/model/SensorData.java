package com.jeeps.datavisualizer.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by jeeps on 12/20/2017.
 */

public class SensorData {
    private double temperature;
    private double humidity;
    private List<Float> weeklyTemperatureMin;
    private List<Float> weeklyTemperatureMax;
    private List<Float> hourlyTemperature;

    public SensorData(double temperature, double humidity, List<Float> weeklyTemperatureMin, List<Float> weeklyTemperatureMax) {
        this.temperature = temperature;
        this.humidity = humidity;
        this.weeklyTemperatureMin = weeklyTemperatureMin;
        this.weeklyTemperatureMax = weeklyTemperatureMax;
    }

    public SensorData() {
        weeklyTemperatureMin = new ArrayList<>();
        weeklyTemperatureMax = new ArrayList<>();
        hourlyTemperature = new ArrayList<>();
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public double getHumidity() {
        return humidity;
    }

    public void setHumidity(double humidity) {
        this.humidity = humidity;
    }

    public List<Float> getWeeklyTemperatureMin() {
        Collections.reverse(weeklyTemperatureMin);
        return weeklyTemperatureMin;
    }

    public void setWeeklyTemperatureMin(List<Float> weeklyTemperatureMin) {
        this.weeklyTemperatureMin = weeklyTemperatureMin;
    }

    public List<Float> getWeeklyTemperatureMax() {
        Collections.reverse(weeklyTemperatureMax);
        return weeklyTemperatureMax;
    }

    public void setWeeklyTemperatureMax(List<Float> weeklyTemperatureMax) {
        this.weeklyTemperatureMax = weeklyTemperatureMax;
    }

    public List<Float> getHourlyTemperature() {
        Collections.reverse(hourlyTemperature);
        return hourlyTemperature;
    }

    public void setHourlyTemperature(List<Float> hourlyTemperature) {
        this.hourlyTemperature = hourlyTemperature;
    }
}

