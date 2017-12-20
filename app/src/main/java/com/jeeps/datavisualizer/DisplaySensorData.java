package com.jeeps.datavisualizer;

import android.graphics.Color;
import android.graphics.PointF;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hookedonplay.decoviewlib.DecoView;
import com.hookedonplay.decoviewlib.charts.SeriesItem;
import com.hookedonplay.decoviewlib.events.DecoEvent;
import com.jeeps.datavisualizer.model.SensorData;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DisplaySensorData extends AppCompatActivity {

    private static final String TAG = "DISPLAY_SENSOR_DATA";

    @BindView(R.id.humidity_decoview)
    DecoView mHumidityGraph;
    @BindView(R.id.humidity_text)
    TextView mHumidityText;

    private int humidityDataIndex;

    private FirebaseDatabase mDatabase;
    private DatabaseReference mSensorData;

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
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    private void setFirebaseDatabase() {
        //Sensor data
        mDatabase = FirebaseDatabase.getInstance();

        mSensorData = mDatabase.getReference("object");
        mSensorData.addValueEventListener(new ValueEventListener() {
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
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
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

    private void initializeGraphs() {
        mHumidityGraph.addSeries(getBackgroundTrack(0, 0, 100f));

        //Create data series track
        humidityDataIndex = mHumidityGraph.addSeries(createDataSeries(0, 0, 100f, 0, "#56d1c0"));
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
