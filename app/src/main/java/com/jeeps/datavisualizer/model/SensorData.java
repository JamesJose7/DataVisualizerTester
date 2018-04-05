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
    private double luminosity;
    private List<Float> weeklyTemperatureMin;
    private List<Float> weeklyTemperatureMax;
    private List<Float> hourlyTemperature;
    private List<Float> weeklyHumidityMin;
    private List<Float> weeklyHumidityMax;
    private List<Float> hourlyHumidity;
    private List<Float> weeklyLuminosityMin;
    private List<Float> weeklyLuminosityMax;
    private List<Float> hourlyLuminosity;

    public SensorData(double temperature, double humidity, double luminosity, List<Float> weeklyTemperatureMin, List<Float> weeklyTemperatureMax) {
        this.temperature = temperature;
        this.humidity = humidity;
        this.weeklyTemperatureMin = weeklyTemperatureMin;
        this.weeklyTemperatureMax = weeklyTemperatureMax;
    }

    public SensorData() {
        weeklyTemperatureMin = new ArrayList<>();
        weeklyTemperatureMax = new ArrayList<>();
        hourlyTemperature = new ArrayList<>();
        weeklyHumidityMin = new ArrayList<>();
        weeklyHumidityMax = new ArrayList<>();
        hourlyHumidity = new ArrayList<>();
        weeklyLuminosityMin = new ArrayList<>();
        weeklyLuminosityMax = new ArrayList<>();
        hourlyLuminosity = new ArrayList<>();
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

    public double getLuminosity() {
        return luminosity;
    }

    public void setLuminosity(double luminosity) {
        this.luminosity = luminosity;
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
        return hourlyTemperature;
    }

    public void setHourlyTemperature(List<Float> hourlyTemperature) {
        this.hourlyTemperature = hourlyTemperature;
    }

    public List<Float> getWeeklyHumidityMin() {
        return weeklyHumidityMin;
    }

    public void setWeeklyHumidityMin(List<Float> weeklyHumidityMin) {
        this.weeklyHumidityMin = weeklyHumidityMin;
    }

    public List<Float> getWeeklyHumidityMax() {
        return weeklyHumidityMax;
    }

    public void setWeeklyHumidityMax(List<Float> weeklyHumidityMax) {
        this.weeklyHumidityMax = weeklyHumidityMax;
    }

    public List<Float> getHourlyHumidity() {
        return hourlyHumidity;
    }

    public void setHourlyHumidity(List<Float> hourlyHumidity) {
        this.hourlyHumidity = hourlyHumidity;
    }

    public List<Float> getWeeklyLuminosityMin() {
        return weeklyLuminosityMin;
    }

    public void setWeeklyLuminosityMin(List<Float> weeklyLuminosityMin) {
        this.weeklyLuminosityMin = weeklyLuminosityMin;
    }

    public List<Float> getWeeklyLuminosityMax() {
        return weeklyLuminosityMax;
    }

    public void setWeeklyLuminosityMax(List<Float> weeklyLuminosityMax) {
        this.weeklyLuminosityMax = weeklyLuminosityMax;
    }

    public List<Float> getHourlyLuminosity() {
        return hourlyLuminosity;
    }

    public void setHourlyLuminosity(List<Float> hourlyLuminosity) {
        this.hourlyLuminosity = hourlyLuminosity;
    }
}

