package com.jeeps.datavisualizer.model;

import java.util.List;

/**
 * Created by jeeps on 12/20/2017.
 */

public class SensorData {
    private double temperature;
    private double humidity;
    private List<Float> weeklyTemperature;

    public SensorData(double temperature, double humidity, List<Float> weeklyTemperature) {
        this.temperature = temperature;
        this.humidity = humidity;
        this.weeklyTemperature = weeklyTemperature;
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

    public List<Float> getWeeklyTemperature() {
        return weeklyTemperature;
    }

    public void setWeeklyTemperature(List<Float> weeklyTemperature) {
        this.weeklyTemperature = weeklyTemperature;
    }
}

