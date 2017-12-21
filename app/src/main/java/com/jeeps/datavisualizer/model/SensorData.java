package com.jeeps.datavisualizer.model;

import java.util.List;

/**
 * Created by jeeps on 12/20/2017.
 */

public class SensorData {
    private double temperature;
    private double humidity;
    private List<Float> weeklyTemperatureMin;
    private List<Float> weeklyTemperatureMax;

    public SensorData(double temperature, double humidity, List<Float> weeklyTemperatureMin, List<Float> weeklyTemperatureMax) {
        this.temperature = temperature;
        this.humidity = humidity;
        this.weeklyTemperatureMin = weeklyTemperatureMin;
        this.weeklyTemperatureMax = weeklyTemperatureMax;
    }

    public SensorData() {}

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
        return weeklyTemperatureMin;
    }

    public void setWeeklyTemperatureMin(List<Float> weeklyTemperatureMin) {
        this.weeklyTemperatureMin = weeklyTemperatureMin;
    }

    public List<Float> getWeeklyTemperatureMax() {
        return weeklyTemperatureMax;
    }

    public void setWeeklyTemperatureMax(List<Float> weeklyTemperatureMax) {
        this.weeklyTemperatureMax = weeklyTemperatureMax;
    }
}

