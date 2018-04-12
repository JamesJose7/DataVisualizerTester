package com.jeeps.datavisualizer.controller;

/**
 * Created by jeeps on 2/19/2018.
 */

public class ApiBuilder {
    private static final String API_KEY = "123517327a7985dfd25c58c5b291123";

    public static final String TEMPERATURE_SENSOR = "TCA";
    public static final String HUMIDITY_SENSOR = "HUMA";
    public static final String LUMINOSITY_SENSOR = "LUM";

    private static final String BASE_SENSOR_URL = "http://carbono.utpl.edu.ec:8080/smartlandiotv2/webresources/entidades.datos/getdatatable?ak=";

    private static final String SENSOR_DAY_RESUME_URL = "http://carbono.utpl.edu.ec:8080/smartlandiotv2/webresources/entidades.datos/sensordayresume?ak=";
    private static final String RESUME_BY_RANGE_DAYS_URL = "http://carbono.utpl.edu.ec:8080/smartlandiotv2/webresources/entidades.datos/resumebyrangedays?ak=";

    @Deprecated
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

    public static String buildValuesByHourUrl(String nodeName, String sensorName, String date) {
        StringBuilder builder = new StringBuilder();
        //Base url
        builder.append(SENSOR_DAY_RESUME_URL);
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

    public static String buildValuesByDateRangeUrl(String nodeName, String sensorName, String date1, String date2) {
        StringBuilder builder = new StringBuilder();
        //Base url
        builder.append(RESUME_BY_RANGE_DAYS_URL);
        builder.append(API_KEY);
        //Node name
        builder.append("&node=");
        builder.append(nodeName);
        //Sensor name
        builder.append("&sensor=");
        builder.append(sensorName);
        //Date 1
        builder.append("&fecha1=");
        builder.append(date1);
        //Date 2
        builder.append("&fecha2=");
        builder.append(date2);

        return builder.toString();
    }


}
