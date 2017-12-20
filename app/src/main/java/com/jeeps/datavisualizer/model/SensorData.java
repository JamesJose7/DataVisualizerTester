package com.jeeps.datavisualizer.model;

import java.util.List;

/**
 * Created by jeeps on 12/20/2017.
 */

public class SensorData {
    private double temperature;
    private double humidity;
    private List<Double> weeklyTemperature;

    public SensorData(double temperature, double humidity, List<Double> weeklyTemperature) {
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

    public List<Double> getWeeklyTemperature() {
        return weeklyTemperature;
    }

    public void setWeeklyTemperature(List<Double> weeklyTemperature) {
        this.weeklyTemperature = weeklyTemperature;
    }
}

