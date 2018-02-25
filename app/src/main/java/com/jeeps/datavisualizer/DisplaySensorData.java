package com.jeeps.datavisualizer;

import android.graphics.Color;
import android.graphics.PointF;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.db.chart.animation.Animation;
import com.db.chart.model.LineSet;
import com.db.chart.view.HorizontalStackBarChartView;
import com.db.chart.view.LineChartView;
import com.hookedonplay.decoviewlib.DecoView;
import com.hookedonplay.decoviewlib.charts.SeriesItem;
import com.hookedonplay.decoviewlib.events.DecoEvent;
import com.jeeps.datavisualizer.controller.SensorApiHelper;
import com.jeeps.datavisualizer.controller.SensorDataParser;
import com.jeeps.datavisualizer.model.SensorData;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DisplaySensorData extends AppCompatActivity implements SensorApiHelper.SensorApiListener {

    public static final String TAG = "DISPLAY_SENSOR_DATA";

    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.humidity_decoview)
    DecoView mHumidityGraph;
    @BindView(R.id.humidity_text)
    TextView mHumidityText;
    @BindView(R.id.week_temp_linechart)
    LineChartView mWeekTempChart;
    @BindView(R.id.hourly_temp_linechart)
    LineChartView mHourlyTempChart;
    @BindView(R.id.week_temp_barchart)
    HorizontalStackBarChartView mHorizontalBarChart;
    @BindView(R.id.current_temp_text)
    TextView mCurrentTempText;
    @BindView(R.id.thermometer_image)
    ImageView mThermometerImage;
    @BindView(R.id.main_progress_bar)
    ProgressBar mMainProgressBar;
    @BindView(R.id.temp_chart_week_button)
    Button mWeekTempChartButton;
    @BindView(R.id.temp_chart_hours_button)
    Button mHoursTempChartButton;
    @BindView(R.id.temp_chart_compare_button)
    Button mCompareTempChartButton;

    private int humidityDataIndex;
    private String[] weekLabels;
    private String[] hourLabels;
    private final float[] weekDefault = {50,50,50,50,50,50,50};
    private final float[] weekDefaultNeg = {-50,-50,-50,-50,-50,-50,-50};
    private float[] mWeekTempMax;
    private float[] mWeekTempMin;

    private float[] mWeekTempMinNegative;
    private SimpleDateFormat dayFormatter = new SimpleDateFormat("E", new Locale("es", "EC"));
    private SimpleDateFormat hourFormatter = new SimpleDateFormat("h aa", Locale.US);

    private SensorApiHelper mSensorApiHelper;
    private boolean firstLoad = true;
    private LineSet mWeekTempSet0;
    private LineSet mWeekTempSet1;
    private LineSet mHourTempSet0;
    private LineSet mHourTempSet1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_sensor_data);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ButterKnife.bind(this);
        //Change action bar title
        getSupportActionBar().setTitle("Sensores UTPL");

        //Create chart labels
        weekLabels = getWeekLabels();
        hourLabels = getHourLabels();

        initializeGraphs();

        //Initialize connection with api
        mSensorApiHelper = new SensorApiHelper(this, this);
        mSensorApiHelper.openConnection();

        //Initiales swipe refresher listener
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSensorApiHelper.openConnection();
            }
        });

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Update data
                Snackbar.make(view, "Data updated", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
    }

    /**
     * Initializes all graphs with default values on data sets and applies custom styles
     */
    private void initializeGraphs() {
        //Humidity chart
        mHumidityGraph.addSeries(getBackgroundTrack(0, 0, 100f));
        //Create data series track
        humidityDataIndex = mHumidityGraph.addSeries(createDataSeries(0, 0, 100f, 0, "#56d1c0"));

        //Weekly temperature
        mWeekTempSet0 = new LineSet(weekLabels, weekDefault);
        mWeekTempSet1 = new LineSet(weekLabels, weekDefault);

        //Datasets config
        mWeekTempChart.addData(mWeekTempSet0);
        mWeekTempChart.addData(mWeekTempSet1);

        mWeekTempSet0.setColor(Color.parseColor("#53c1bd"));
        mWeekTempSet1.setColor(Color.parseColor("#5b5cbd"));

        //Line chart config
        mWeekTempChart
                .setStep(5)
                .show(new Animation());

        //Hourly temperature
        mHourTempSet0 = new LineSet(hourLabels, weekDefault);
        mHourTempSet1 = new LineSet(hourLabels, weekDefault);

        //Datasets config
        mHourlyTempChart.addData(mHourTempSet0);
        mHourlyTempChart.addData(mHourTempSet1);

        mHourTempSet0.setColor(Color.parseColor("#53c1bd"));
        mHourTempSet1.setColor(Color.parseColor("#5b5cbd"));

        //Line chart config
        mHourlyTempChart
                .setStep(5)
                .show(new Animation());
    }

    /**
     * Listens to changes in the {@link SensorApiHelper} and updates the view every time the model
     * has been changed
     * @param sensorData Contains the sensor data to be displayed on each graph
     * @see SensorApiHelper.SensorApiListener
     */
    @Override
    public void update(SensorData sensorData) {
        Toast.makeText(this, "Updated", Toast.LENGTH_SHORT).show();
        //Current humidity
        int humidity = (int) sensorData.getHumidity();
        //Display percentage
        String humidityPercent = String.format("%d%%", humidity);
        mHumidityText.setText(humidityPercent);
        //Animate
        mHumidityGraph.addEvent(new DecoEvent.Builder(humidity).
                setColor(getHumidityColor(humidity)).
                setIndex(humidityDataIndex).setDelay(500).build());

        //Current temperature
        int currentTemp = (int) sensorData.getTemperature();
        mCurrentTempText.setText(String.format("%d\u00b0", currentTemp));
        //Set thermometer image accordingly
        mThermometerImage.setImageResource(getPercentageTermometer(currentTemp));

        //Weekly temperature
        //Arrays needed for both the linear and bar chart
        mWeekTempMin = listToArray(sensorData.getWeeklyTemperatureMin());
        mWeekTempMax = listToArray(sensorData.getWeeklyTemperatureMax());
        //Convert min temperature array to negative values for the left portion of the horizontal bar chart
        mWeekTempMinNegative = listToArray(sensorData.getWeeklyTemperatureMin());
        for (int i = 0; i < mWeekTempMinNegative.length; i++)
            mWeekTempMinNegative[i] *= -1;

        //Delay the first data load on graphs since they take some time to initialize properly
        if (firstLoad) {
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    updateCharts();
                }
            }, 2000);
            firstLoad = false;
        } else {
            updateCharts();

        }
        mSwipeRefreshLayout.setRefreshing(false);
    }

    /**
     * Notifies all charts that their data has been updated
     */
    private void updateCharts() {
        //Min temp
        mWeekTempChart.dismissAllTooltips();
        mWeekTempChart.updateValues(0, mWeekTempMin);
        //Max temp
        mWeekTempChart.updateValues(1, mWeekTempMax);
        mWeekTempChart.notifyDataUpdate();
    }

    private float[] listToArray(List<Float> weeklyTemperature) {
        float weekTemp[] = new float[weeklyTemperature.size()];

        for (int i = 0; i < weeklyTemperature.size(); i++)
            weekTemp[i] = weeklyTemperature.get(i);

        return weekTemp;
    }

    /**
     * Returns the appropriate color for the humidity graph based on percentages
     * @param humidity the current humidity percentage
     * @return the color in hexadecimal format
     */
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

    /**
     * Used to get a dark background for the circular graph
     * @param xIn inset value for the x axis
     * @param yIn inset value for the y axis
     * @param width background width
     * @return the {@link SeriesItem} to be applied on the graph
     */
    private SeriesItem getBackgroundTrack(float xIn, float yIn, float width) {
        return new SeriesItem.Builder(Color.argb(255, 218, 218, 218))
                .setRange(0, 100, 100)
                .setInset(new PointF(xIn, yIn))
                .setLineWidth(width)
                .build();
    }

    /**
     * Transforms the data into a seriesItem that can be used in the circular graph
     * @param xIn inset value for the x axis
     * @param yIn inset value for the y axis
     * @param width series width
     * @param initialValue initial value for the series
     * @param color color in hexadecimal format
     * @return the {@link SeriesItem} to be graphed
     */
    private SeriesItem createDataSeries(float xIn, float yIn, float width, int initialValue,
                                        String color) {
        return new SeriesItem.Builder(Color.parseColor(color))
                .setRange(0, 100, initialValue)
                .setInset(new PointF(xIn, yIn))
                .setLineWidth(width)
                .build();
    }

    /**
     * Calculates based on the temperature different ranges to change the thermometer image
     * @param percent percentage used for 5 different stages
     * @return the appropriate {@link android.graphics.drawable.Drawable} for each stage
     */
    private int getPercentageTermometer(int percent) {
        if (percent >= 85)
            return R.drawable.thermometer;
        else if (percent >= 60)
            return R.drawable.thermometer_75;
        else if (percent >= 35)
            return R.drawable.thermometer_50;
        else if (percent >= 10)
            return R.drawable.thermometer_25;
        return R.drawable.thermometer_0;
    }

    private String[] getWeekLabels() {
        String[] labels = new String[7];
        for (int i = 0; i < 7; i++) {
            Date date = SensorDataParser.getPreviousDayDate(i);
            String day = dayFormatter.format(date);
            //Format it
            if (day.contains("."))
                day = day.substring(0, day.length() - 1);
            day = day.substring(0, 1).toUpperCase() + day.substring(1, day.length()).toLowerCase();
            labels[i] = day;
        }
        return labels;
    }

    public String[] getHourLabels() {
        String[] labels = new String[7];
        for (int i = 0; i < 7; i++) {
            Date date = SensorDataParser.getPreviousHourDate(i);
            String hour = hourFormatter.format(date);
            labels[i] = hour;
        }
        return labels;
    }

    private void toggleTempChartButton(boolean activated) {
        mCompareTempChartButton.setClickable(activated);
        mHoursTempChartButton.setClickable(activated);
        mWeekTempChartButton.setClickable(activated);
    }

    @OnClick(R.id.temp_chart_week_button)
    protected void changeTempChartToWeek() {
        mWeekTempChart.setVisibility(View.VISIBLE);
        mHourlyTempChart.setVisibility(View.INVISIBLE);
    }

    @OnClick(R.id.temp_chart_hours_button)
    protected void changeTempChartToHours() {
        mWeekTempChart.setVisibility(View.INVISIBLE);
        mHourlyTempChart.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.temp_chart_compare_button)
    protected void changeTempChartToCompare() {

    }

    @Override
    public void started() {
        mMainProgressBar.setVisibility(View.VISIBLE);
        toggleTempChartButton(false);
    }

    @Override
    public void finished() {
        mMainProgressBar.setVisibility(View.INVISIBLE);
        toggleTempChartButton(true);
    }
}
