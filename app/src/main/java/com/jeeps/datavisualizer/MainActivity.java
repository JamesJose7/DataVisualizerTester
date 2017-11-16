package com.jeeps.datavisualizer;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.hookedonplay.decoviewlib.DecoView;
import com.hookedonplay.decoviewlib.charts.SeriesItem;
import com.hookedonplay.decoviewlib.events.DecoEvent;

public class MainActivity extends AppCompatActivity {

    private TextView mTextMessage;
    private Button mButton;
    private SeekBar mSeekBar;

    private int mProgress = 0;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    mTextMessage.setText(R.string.title_home);
                    homeTab();
                    return true;
                case R.id.navigation_dashboard:
                    mTextMessage.setText(R.string.title_dashboard);
                    return true;
                case R.id.navigation_notifications:
                    mTextMessage.setText(R.string.title_notifications);
                    return true;
            }
            return false;
        }

    };

    private void homeTab() {
        final DecoView arcView = (DecoView) findViewById(R.id.dynamicArcView);
        DecoView arcView2 = (DecoView) findViewById(R.id.dynamicArcView2);
        DecoView arcView3 = (DecoView) findViewById(R.id.dynamicArcView3);
        DecoView arcView4 = (DecoView) findViewById(R.id.dynamicArcView4);


// Create background track
        arcView.addSeries(new SeriesItem.Builder(Color.argb(255, 218, 218, 218))
                .setRange(0, 100, 100)
                .setInitialVisibility(false)
                .setLineWidth(100f)
                .build());

//Create data series track
        final SeriesItem seriesItem1 = new SeriesItem.Builder(Color.argb(255, 64, 196, 0))
                .setRange(0, 100, 0)
                .setLineWidth(100f)
                .build();

        final int series1Index = arcView.addSeries(seriesItem1);

        arcView.addEvent(new DecoEvent.Builder(DecoEvent.EventType.EVENT_SHOW, true)
                .setDelay(2000)
                .setDuration(2000)
                .build());



        final String format = "%.0f%%";

        seriesItem1.addArcSeriesItemListener(new SeriesItem.SeriesItemListener() {
            @Override
            public void onSeriesItemAnimationProgress(float percentComplete, float currentPosition) {
                if (format.contains("%%")) {
                    float percentFilled = ((currentPosition - seriesItem1.getMinValue()) / (seriesItem1.getMaxValue() - seriesItem1.getMinValue()));
                    mTextMessage.setText(String.format(format, percentFilled * 100f));
                } else {
                    mTextMessage.setText(String.format(format, currentPosition));
                }
            }

            @Override
            public void onSeriesItemDisplayProgress(float percentComplete) {

            }
        });

        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mProgress = progress;
                arcView.addEvent(new DecoEvent.Builder(mProgress).setIndex(series1Index).setDelay(0).build());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        ///////////////

        // Create background track
        arcView2.addSeries(new SeriesItem.Builder(Color.argb(255, 218, 218, 218))
                .setRange(0, 100, 100)
                .setInitialVisibility(false)
                .setLineWidth(100f)
                .build());

//Create data series track
        SeriesItem seriesItem12 = new SeriesItem.Builder(Color.argb(255, 64, 196, 0))
                .setRange(0, 100, 0)
                .setLineWidth(100f)
                .build();

        int series1Index2 = arcView2.addSeries(seriesItem12);

        arcView2.addEvent(new DecoEvent.Builder(DecoEvent.EventType.EVENT_SHOW, true)
                .setDelay(1000)
                .setDuration(2000)
                .build());

        arcView2.addEvent(new DecoEvent.Builder(25).setIndex(series1Index2).setDelay(3000).build());
        arcView2.addEvent(new DecoEvent.Builder(50).setIndex(series1Index2).setDelay(4000).build());
        arcView2.addEvent(new DecoEvent.Builder(75).setIndex(series1Index2).setDelay(7000).build());
        arcView2.addEvent(new DecoEvent.Builder(15).setIndex(series1Index2).setDelay(90000).build());


        ////////////////

        // Create background track
        arcView3.addSeries(new SeriesItem.Builder(Color.argb(255, 218, 218, 218))
                .setRange(0, 100, 100)
                .setInitialVisibility(false)
                .setLineWidth(100f)
                .build());

//Create data series track
        SeriesItem seriesItem13 = new SeriesItem.Builder(Color.argb(255, 64, 196, 0))
                .setRange(0, 100, 0)
                .setLineWidth(100f)
                .build();

        int series1Index3 = arcView3.addSeries(seriesItem13);

        arcView3.addEvent(new DecoEvent.Builder(DecoEvent.EventType.EVENT_SHOW, true)
                .setDelay(500)
                .setDuration(2000)
                .build());

        arcView3.addEvent(new DecoEvent.Builder(25).setIndex(series1Index3).setDelay(1000).build());
        arcView3.addEvent(new DecoEvent.Builder(50).setIndex(series1Index3).setDelay(4000).build());
        arcView3.addEvent(new DecoEvent.Builder(75).setIndex(series1Index3).setDelay(6000).build());
        arcView3.addEvent(new DecoEvent.Builder(15).setIndex(series1Index3).setDelay(80000).build());


        //////////////////////

        // Create background track
        arcView4.addSeries(new SeriesItem.Builder(Color.argb(255, 218, 218, 218))
                .setRange(0, 100, 100)
                .setInitialVisibility(false)
                .setLineWidth(100f)
                .build());

//Create data series track
        SeriesItem seriesItem14 = new SeriesItem.Builder(Color.argb(255, 64, 196, 0))
                .setRange(0, 100, 0)
                .setLineWidth(100f)
                .build();

        int series1Index4 = arcView4.addSeries(seriesItem14);

        arcView4.addEvent(new DecoEvent.Builder(DecoEvent.EventType.EVENT_SHOW, true)
                .setDelay(1500)
                .setDuration(2000)
                .build());

        arcView4.addEvent(new DecoEvent.Builder(25).setIndex(series1Index4).setDelay(3000).build());
        arcView4.addEvent(new DecoEvent.Builder(50).setIndex(series1Index4).setDelay(3500).build());
        arcView4.addEvent(new DecoEvent.Builder(75).setIndex(series1Index4).setDelay(5000).build());
        arcView4.addEvent(new DecoEvent.Builder(15).setIndex(series1Index4).setDelay(80000).build());


    }

    private void displayDataActivity() {
        Intent intent = new Intent(this, DisplayData.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextMessage = (TextView) findViewById(R.id.message);
        mButton = (Button) findViewById(R.id.button);
        mSeekBar = (SeekBar) findViewById(R.id.seekBar);

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayDataActivity();
            }
        });

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

}
