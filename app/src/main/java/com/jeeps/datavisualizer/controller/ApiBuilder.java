package com.jeeps.datavisualizer.controller;

/**
 * Created by jeeps on 2/19/2018.
 */

public class ApiBuilder {
    private static final String API_KEY = "123517327a7985dfd25c58c5b291123";

    public static final String TEMPERATURE_SENSOR = "TCA";
    public static final String HUMIDITY_SENSOR = "HUMA";

    private static final String BASE_SENSOR_URL = "http://carbono.utpl.edu.ec:8080/smartlandiotv2/webresources/entidades.datos/getdatatable?ak=";

    public static String buildSensorUrl(String nodeName, String sensorName, String date) {
        StringBuilder builder = new StringBuilder();
        //Base url
        builder.append(BASE_SENSOR_URL);
        builder.append(API_KEY);
        //Node name
        builder.append("&node=");
        builder.append(nodeName);
        //Sensor name
        builder.append("&sensor=");
        builder.append(sensorName);
        //Date
        builder.append("&fecha=");
        builder.append(date);

        return builder.toString();
    }


}
