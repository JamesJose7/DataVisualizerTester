package com.jeeps.datavisualizer;

import android.graphics.Color;
import android.graphics.PointF;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.db.chart.animation.Animation;
import com.db.chart.model.LineSet;
import com.db.chart.view.LineChartView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hookedonplay.decoviewlib.DecoView;
import com.hookedonplay.decoviewlib.charts.SeriesItem;
import com.hookedonplay.decoviewlib.events.DecoEvent;
import com.jeeps.datavisualizer.model.SensorData;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DisplaySensorData extends AppCompatActivity {

    private static final String TAG = "DISPLAY_SENSOR_DATA";

    @BindView(R.id.humidity_decoview)
    DecoView mHumidityGraph;
    @BindView(R.id.humidity_text)
    TextView mHumidityText;
    @BindView(R.id.week_temp_linechart)
    LineChartView mWeekTempChart;

    private int humidityDataIndex;

    private FirebaseDatabase mDatabase;
    private DatabaseReference mSensorData;

    private final String[] weekLabels = {"M", "T", "W", "T", "F", "S", "S"};
    private final float[] weekDefault = {99,99,99,99,99,99,99};

    private boolean FIRST_LOAD = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_sensor_data);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ButterKnife.bind(this);

        initializeGraphs();
        setFirebaseDatabase();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Data updated", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                Random random = new Random();
                List<Float> weekMin = new ArrayList<>();
                List<Float> weekMax = new ArrayList<>();

                for (int i = 0; i < 7; i++)
                    weekMin.add((float) getRandomDouble(0, 70));

                //Generate random max numbers
                for (int i = 0; i < 7; i++) {
                    int minValue = weekMin.get(i).intValue() + 10;
                    weekMax.add((float) getRandomDouble(minValue, 100));
                }
                int humidity = random.nextInt(100);

                //mMyRef.setValue("Hello, World! " + rand);

                SensorData sensorData = new SensorData(weekMin.get(0), humidity, weekMin, weekMax);
                mSensorData.setValue(sensorData);
            }
        });
    }

    private void initializeGraphs() {
        //Humidity chart
        mHumidityGraph.addSeries(getBackgroundTrack(0, 0, 100f));
        //Create data series track
        humidityDataIndex = mHumidityGraph.addSeries(createDataSeries(0, 0, 100f, 0, "#56d1c0"));

        //Weekly temperature
        LineSet dataset = new LineSet(weekLabels, weekDefault);
        LineSet dataset2 = new LineSet(weekLabels, weekDefault);

        mWeekTempChart.addData(dataset);
        mWeekTempChart.addData(dataset2);

        /*dataset.setColor(Color.parseColor("#53c1bd"))
                .setFill(Color.parseColor("#3d6c73"))
                .setGradientFill(new int[]{Color.parseColor("#364d5a"), Color.parseColor("#3f7178")},
                        null);*/
        dataset.setColor(Color.parseColor("#53c1bd"));
        dataset2.setColor(Color.parseColor("#5b5cbd"));


        mWeekTempChart
                .setStep(10)
                .show(new Animation());
    }

    private void setFirebaseDatabase() {
        //Sensor data
        mDatabase = FirebaseDatabase.getInstance();

        mSensorData = mDatabase.getReference("object");
        mSensorData.addValueEventListener(new ValueEventListener() {

            private float[] mWeekTempMax;
            private float[] mWeekTempMin;

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                SensorData value = dataSnapshot.getValue(SensorData.class);
                Log.d(TAG, "Value is: " + value);

                int humidity = (int) value.getHumidity();
                //Display percentage
                String humidityPercent = String.format("%d%%", humidity);
                mHumidityText.setText(humidityPercent);
                //Animate
                mHumidityGraph.addEvent(new DecoEvent.Builder(humidity).
                        setColor(getHumidityColor(humidity)).
                        setIndex(humidityDataIndex).setDelay(500).build());

                //Weekly temperature
                mWeekTempMin = listToArray(value.getWeeklyTemperatureMin());
                mWeekTempMax = listToArray(value.getWeeklyTemperatureMax());

                //Weekly temperature
                if (FIRST_LOAD) {
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //Min temp
                            mWeekTempChart.dismissAllTooltips();
                            mWeekTempChart.updateValues(0, mWeekTempMin);
                            //Max temp
                            mWeekTempChart.updateValues(1, mWeekTempMax);
                            mWeekTempChart.notifyDataUpdate();

                        }
                    }, 2000);
                    FIRST_LOAD = false;
                } else {
                    //Min temp
                    mWeekTempChart.dismissAllTooltips();
                    mWeekTempChart.updateValues(0, mWeekTempMin);
                    //Max temp
                    mWeekTempChart.updateValues(1, mWeekTempMax);
                    mWeekTempChart.notifyDataUpdate();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }

    private double getRandomDouble(int min, int max) {
        Random random = new Random();
        return random.nextInt((max - min) + 1) + min;
    }

    private float[] listToArray(List<Float> weeklyTemperature) {
        float weekTemp[] = new float[weeklyTemperature.size()];

        for (int i = 0; i < weeklyTemperature.size(); i++)
            weekTemp[i] = weeklyTemperature.get(i);

        return weekTemp;
    }

    private int getHumidityColor(int humidity) {
        String below = "#00b3e3";
        String average = "#0f61a0";
        String above = "#253362";

        if (humidity >= 66)
            return Color.parseColor(above);
        else if (humidity >= 33)
            return Color.parseColor(average);
        else
            return Color.parseColor(below);
    }

    private SeriesItem getBackgroundTrack(float xIn, float yIn, float width) {
        return new SeriesItem.Builder(Color.argb(255, 218, 218, 218))
                .setRange(0, 100, 100)
                .setInset(new PointF(xIn, yIn))
                .setLineWidth(width)
                .build();
    }

    private SeriesItem createDataSeries(float xIn, float yIn, float width, int initialValue,
                                        String color) {
        return new SeriesItem.Builder(Color.parseColor(color))
                .setRange(0, 100, initialValue)
                .setInset(new PointF(xIn, yIn))
                .setLineWidth(width)
                .build();
    }
}
