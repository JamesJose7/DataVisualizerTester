package com.jeeps.datavisualizer;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.db.chart.animation.Animation;
import com.db.chart.listener.OnEntryClickListener;
import com.db.chart.model.LineSet;
import com.db.chart.view.LineChartView;
import com.hookedonplay.decoviewlib.DecoView;
import com.hookedonplay.decoviewlib.charts.SeriesItem;
import com.hookedonplay.decoviewlib.events.DecoEvent;
import com.jeeps.datavisualizer.adapters.MoreInfoTempCompareListAdapter;
import com.jeeps.datavisualizer.adapters.MoreInfoTempListAdapter;
import com.jeeps.datavisualizer.adapters.SimpleDividerItemDecoration;
import com.jeeps.datavisualizer.controller.SensorApiHelper;
import com.jeeps.datavisualizer.controller.SensorDataParser;
import com.jeeps.datavisualizer.model.SensorData;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.json.JSONException;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DisplaySensorData extends AppCompatActivity implements SensorApiHelper.SensorApiListener, DatePickerDialog.OnDateSetListener {

    public static final String TAG = "DISPLAY_SENSOR_DATA";
    public static final int TEMP_WEEK_GRAPH = 0;
    public static final int TEMP_HOURS_GRAPH = 1;
    public static final int TEMP_COMPARE_GRAPH = 2;
    private static final int TEMP_DATE_X = 0;
    private static final int TEMP_DATE_Y = 1;

    @BindView(R.id.swipe_refresh_layout) SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.humidity_decoview) DecoView mHumidityGraph;
    @BindView(R.id.humidity_text) TextView mHumidityText;
    @BindView(R.id.week_temp_linechart) LineChartView mWeekTempChart;
    @BindView(R.id.hourly_temp_linechart) LineChartView mHourlyTempChart;
    @BindView(R.id.compare_temp_linechart) LineChartView mCompareTempChart;
    @BindView(R.id.current_temp_text) TextView mCurrentTempText;
    @BindView(R.id.last_7_days_text) TextView mTempChartDescriptionText;
    @BindView(R.id.thermometer_image) ImageView mThermometerImage;
    @BindView(R.id.main_progress_bar) ProgressBar mMainProgressBar;
    @BindView(R.id.temp_chart_week_button) Button mWeekTempChartButton;
    @BindView(R.id.temp_chart_hours_button) Button mHoursTempChartButton;
    @BindView(R.id.temp_chart_compare_button) Button mCompareTempChartButton;
    @BindView(R.id.temp_choose_x_day) Button mChooseTempDateXButton;
    @BindView(R.id.temp_choose_y_day) Button mChooseTempDateYButton;
    @BindView(R.id.compare_temp_controls_container) RelativeLayout mTempCompareControlsLayout;
    @BindView(R.id.compare_loading_progressbar) ProgressBar mTempCompareProgressBar;
    @BindView(R.id.fill_graph_button) ImageButton mFillTempChartLinesButton;

    /* More info layout */
    @BindView(R.id.more_info_layout) RelativeLayout mMoreInfoLayout;
    @BindView(R.id.more_info_temp_card) CardView mMoreInfoCard;
    @BindView(R.id.more_info_temp_card_subtitle) TextView mMoreInfoCardTitle;
    @BindView(R.id.more_info_temp_list_view) RecyclerView mMoreInfoTempRecyclerView;
    @BindView(R.id.more_info_hourly_temp_list_view) RecyclerView mMoreInfoHourlyTempRecyclerView;
    @BindView(R.id.more_info_compare_temp_list_view) RecyclerView mMoreInfoCompareTempRecyclerView;
    private MoreInfoTempListAdapter mMoreInfoTempListAdapter;
    private MoreInfoTempListAdapter mMoreInfoHourlyTempListAdapter;
    private MoreInfoTempCompareListAdapter mMoreInfoTempCompareListAdapter;

    /* Temp chart */
    @BindView(R.id.temp_chart_value_card) CardView mTempCardValueCard;
    @BindView(R.id.temp_chart_value_card_text) TextView mTempCardValueCardText;

    /* Graph legend */
    @BindView(R.id.temp_legend_a_container) LinearLayout mTempLegendAContainer;
    @BindView(R.id.temp_legend_b_container) LinearLayout mTempLegendBContainer;
    @BindView(R.id.temp_legend_a_view) TextView mTempLegendAView;
    @BindView(R.id.temp_legend_a) TextView mTempLegendA;
    @BindView(R.id.temp_legend_b_view) TextView mTempLegendBView;
    @BindView(R.id.temp_legend_b) TextView mTempLegendB;

    private int humidityDataIndex;
    private String[] weekLabels;
    private String[] hourLabels;
    private final float[] weekDefault = {40,40,40,40,40,40,40};
    private float[] mWeekTempMax;
    private float[] mWeekTempMin;
    private float[] mHourlyTemp;

    private SimpleDateFormat dayFormatter = new SimpleDateFormat("E", new Locale("es", "EC"));
    private SimpleDateFormat hourFormatter = new SimpleDateFormat("h aa", Locale.US);

    private SensorApiHelper mSensorApiHelper;
    private boolean firstLoad = true;
    private boolean fillTempChartLines = false;
    private int currentTempGraph = TEMP_WEEK_GRAPH;
    private int choosingDate;
    private Date tempCompareDateX;
    private Date tempCompareDateY;

    private LineSet mWeekTempSet0;
    private LineSet mWeekTempSet1;
    private LineSet mHourTempSet0;
    private LineSet mHourTempSet1;
    private LineSet mCompareTempSet0;
    private LineSet mCompareTempSet1;
    private AnimatorSet mFadeInAndOut;
    private AnimatorSet mExpandFromMiddleCardAnimation;

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

        //Compare buttons
        tempCompareDateX = new Date();
        tempCompareDateY = SensorDataParser.getPreviousDayDate(1);
        changeCompareButtonsText();

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

        //Hide more info layout onclick
        mMoreInfoLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMoreInfoLayout.setVisibility(View.INVISIBLE);
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
        //Change legend
        showTempLegend(TEMP_WEEK_GRAPH);

        //Humidity chart
        mHumidityGraph.addSeries(getBackgroundTrack(0, 0, 100f));
        //Create data series track
        humidityDataIndex = mHumidityGraph.addSeries(createDataSeries(0, 0, 100f, 0, "#56d1c0"));

        /* ******Weekly temperature******* */
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

        /* ******Hourly temperature******* */
        mHourTempSet0 = new LineSet(hourLabels, weekDefault);
        mHourTempSet1 = new LineSet(hourLabels, weekDefault);

        //Datasets config
        mHourlyTempChart.addData(mHourTempSet0);
        //mHourlyTempChart.addData(mHourTempSet1);

        mHourTempSet0.setColor(Color.parseColor("#FFD700"));
        mHourTempSet1.setColor(Color.parseColor("#5b5cbd"));

        //Line chart config
        mHourlyTempChart
                .setStep(5)
                .show(new Animation());

        /* ******Compare temperature******* */
        mCompareTempSet0 = new LineSet(hourLabels, weekDefault);
        mCompareTempSet1 = new LineSet(hourLabels, weekDefault);

        //Datasets config
        mCompareTempChart.addData(mCompareTempSet0);
        mCompareTempChart.addData(mCompareTempSet1);

        mCompareTempSet0.setColor(Color.parseColor("#c15357"));
        mCompareTempSet1.setColor(Color.parseColor("#53C186"));

        //Line chart config
        mCompareTempChart
                .setStep(5)
                .show(new Animation());

        //Temp chart entry listener
        //Weekly
        mWeekTempChart.setOnEntryClickListener(new OnEntryClickListener() {
            @Override
            public void onClick(int setIndex, int entryIndex, Rect rect) {
                showAppropriateTempCardValues(setIndex, entryIndex, mWeekTempSet0, mWeekTempSet1);
            }
        });

        //Hourly
        mHourlyTempChart.setOnEntryClickListener(new OnEntryClickListener() {
            @Override
            public void onClick(int setIndex, int entryIndex, Rect rect) {
                //Toast.makeText(DisplaySensorData.this, "Set: " + setIndex + " Entry: " + entryIndex, Toast.LENGTH_SHORT).show();
                showAppropriateTempCardValues(setIndex, entryIndex, mHourTempSet0, mHourTempSet1);
            }
        });

        //Daily
        mCompareTempChart.setOnEntryClickListener(new OnEntryClickListener() {
            @Override
            public void onClick(int setIndex, int entryIndex, Rect rect) {
                //Toast.makeText(DisplaySensorData.this, "Set: " + setIndex + " Entry: " + entryIndex, Toast.LENGTH_SHORT).show();
                showAppropriateTempCardValues(setIndex, entryIndex, mCompareTempSet0, mCompareTempSet1);
            }
        });
    }

    private void showAppropriateTempCardValues(int setIndex, int entryIndex, LineSet set1, LineSet set2) {
        if (setIndex == 0) {
            //Set value to the text view
            mTempCardValueCardText.setText(String.format("%.2f\u00b0", set1.getValue(entryIndex)));
        } else if (setIndex == 1) {
            //Set value to the text view
            mTempCardValueCardText.setText(String.format("%.2f\u00b0", set2.getValue(entryIndex)));
        }
        //Show card
        showTempCardValueAnimation();
    }

    private void showTempCardValueAnimation() {
        if (mFadeInAndOut != null)
            mFadeInAndOut.cancel();

        mFadeInAndOut = new AnimatorSet();
        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(mTempCardValueCard, "alpha", 0f, 1f);
        ObjectAnimator fadeOut = ObjectAnimator.ofFloat(mTempCardValueCard, "alpha", 1f, 0f);
        fadeOut.setStartDelay(1800);

        //Set alpha to 0 and show the card before animating
        mTempCardValueCard.setAlpha(0);
        mTempCardValueCard.setVisibility(View.VISIBLE);

        //Listener to hide card after animation
        fadeOut.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {}

            @Override
            public void onAnimationEnd(Animator animator) {
                //Hide card
                //Toast.makeText(DisplaySensorData.this, "Finished animation", Toast.LENGTH_SHORT).show();
                mTempCardValueCard.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animator) {}

            @Override
            public void onAnimationRepeat(Animator animator) {}
        });

        //Listener for cancelling animation
        mFadeInAndOut.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {}

            @Override
            public void onAnimationEnd(Animator animator) {}

            @Override
            public void onAnimationCancel(Animator animator) {
                mTempCardValueCard.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animator animator) {}
        });

        mFadeInAndOut.playSequentially(fadeIn, fadeOut);
        mFadeInAndOut.start();
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
        //Compare temp
        getComparisonData(tempCompareDateX, tempCompareDateY);
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

        //Hourly temperature
        mHourlyTemp = listToArray(sensorData.getHourlyTemperature());

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

        //More Info temperature
        mMoreInfoTempRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(this));
        mMoreInfoHourlyTempRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(this));

        mMoreInfoTempListAdapter = new MoreInfoTempListAdapter(this, sensorData.getWeeklyTemperatureMax(), sensorData, MoreInfoTempListAdapter.LAST_7_DAYS);
        mMoreInfoHourlyTempListAdapter = new MoreInfoTempListAdapter(this, sensorData.getWeeklyTemperatureMax(), sensorData, MoreInfoTempListAdapter.LAST_7_HOURS);

        mMoreInfoTempRecyclerView.setAdapter(mMoreInfoTempListAdapter);
        mMoreInfoHourlyTempRecyclerView.setAdapter(mMoreInfoHourlyTempListAdapter);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(DisplaySensorData.this);
        RecyclerView.LayoutManager layoutManager2 = new LinearLayoutManager(DisplaySensorData.this);
        mMoreInfoTempRecyclerView.setLayoutManager(layoutManager);
        mMoreInfoTempRecyclerView.setHasFixedSize(true);
        mMoreInfoHourlyTempRecyclerView.setLayoutManager(layoutManager2);
        mMoreInfoHourlyTempRecyclerView.setHasFixedSize(true);

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

        //Hourly
        mHourlyTempChart.dismissAllTooltips();
        mHourlyTempChart.updateValues(0, mHourlyTemp);
        mHourlyTempChart.notifyDataUpdate();
    }

    private void getComparisonData(final Date dateX, final Date dateY) {
        mTempCompareProgressBar.setVisibility(View.VISIBLE);
        //Compare temp chart
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    SensorData dataX = mSensorApiHelper.requestPreviousDayData(dateX);
                    SensorData dataY = mSensorApiHelper.requestPreviousDayData(dateY);

                    //Get data arrays
                    final float[] dataTempX = listToArray(dataX.getHourlyTemperature());
                    final float[] dataTempY = listToArray(dataY.getHourlyTemperature());

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mTempCompareProgressBar.setVisibility(View.INVISIBLE);
                            updateCompareCharts(dataTempX, dataTempY);

                            //More Info temperature

                            mMoreInfoCompareTempRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(DisplaySensorData.this));
                            mMoreInfoTempCompareListAdapter = new MoreInfoTempCompareListAdapter(DisplaySensorData.this, dataTempX, dataTempY, dateX, dateY);
                            mMoreInfoCompareTempRecyclerView.setAdapter(mMoreInfoTempCompareListAdapter);

                            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(DisplaySensorData.this);
                            mMoreInfoCompareTempRecyclerView.setLayoutManager(layoutManager);
                            mMoreInfoCompareTempRecyclerView.setHasFixedSize(true);
                        }
                    });



                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();
    }

    private void updateCompareCharts(float[] tempX, float[] tempY) {
        //Temp chart
        mCompareTempChart.dismissAllTooltips();
        //Data X
        mCompareTempChart.updateValues(0, tempX);
        //Data Y
        mCompareTempChart.updateValues(1, tempY);
        mCompareTempChart.notifyDataUpdate();
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
    public static int getPercentageTermometer(int percent) {
        if (percent >= 40)
            return R.drawable.thermometer;
        else if (percent >= 30)
            return R.drawable.thermometer_75;
        else if (percent >= 20)
            return R.drawable.thermometer_50;
        else if (percent >= 10)
            return R.drawable.thermometer_25;
        return R.drawable.thermometer_0;
    }

    private String[] getWeekLabels() {
        String[] labels = new String[7];
        int reverseCounter = labels.length -1;
        for (int i = 0; i < 7; i++) {
            Date date = SensorDataParser.getPreviousDayDate(reverseCounter);
            String day = dayFormatter.format(date);
            //Format it
            if (day.contains("."))
                day = day.substring(0, day.length() - 1);
            day = day.substring(0, 1).toUpperCase() + day.substring(1, day.length()).toLowerCase();
            labels[i] = day;

            reverseCounter--;
        }
        return labels;
    }

    public String[] getHourLabels() {
        String[] labels = new String[7];
        int reverseCounter = labels.length -1;
        for (int i = 0; i < 7; i++) {
            Date date = SensorDataParser.getPreviousHourDate(reverseCounter);
            String hour = hourFormatter.format(date);
            labels[i] = hour;

            reverseCounter--;
        }
        return labels;
    }

    private void fadeInOutViews(final View exitingView, final View enteringView) {
        //Fade in and out two views
        ObjectAnimator fadeOut = ObjectAnimator.ofFloat(exitingView, "alpha", 1, 0);
        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(enteringView, "alpha", 0, 1);

        AnimatorSet set = new AnimatorSet();
        set.playTogether(fadeOut, fadeIn);

        //Hide and show views
        set.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                //Set alpha value to zero and show entering view
                enteringView.setAlpha(0);
                enteringView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                //Set alpha value to 1 and hide exiting view
                exitingView.setVisibility(View.INVISIBLE);
                exitingView.setAlpha(1);
            }

            @Override
            public void onAnimationCancel(Animator animator) {}

            @Override
            public void onAnimationRepeat(Animator animator) {}
        });

        set.start();
    }

    private void showTempCompareControlls() {
        mTempCompareControlsLayout.setScaleY(0);
        mTempCompareControlsLayout.setAlpha(0);
        mTempCompareControlsLayout.setVisibility(View.VISIBLE);

        ObjectAnimator scaley = ObjectAnimator.ofFloat(mTempCompareControlsLayout, "scaleY", 0, 1);
        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(mTempCompareControlsLayout, "alpha", 0, 1);
        AnimatorSet set = new AnimatorSet();
        set.setInterpolator(new AccelerateDecelerateInterpolator());
        set.setDuration(150);
        set.playTogether(scaley, fadeIn);
        set.start();
    }

    private void createDatePicker() {
        Calendar now = Calendar.getInstance();
        DatePickerDialog dpd = DatePickerDialog.newInstance(
                this,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        dpd.setMaxDate(cal);
        dpd.show(getFragmentManager(), "Datepickerdialog");
    }

    private void changeCompareButtonsText() {
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        mChooseTempDateXButton.setText(dateFormatter.format(tempCompareDateX));
        mChooseTempDateYButton.setText(dateFormatter.format(tempCompareDateY));
    }

    private void toggleTempChartButton(boolean activated) {
        mCompareTempChartButton.setClickable(activated);
        mHoursTempChartButton.setClickable(activated);
        mWeekTempChartButton.setClickable(activated);

        mFillTempChartLinesButton.setClickable(activated);
    }

    private void showTempLegend(int type) {
        mTempLegendBContainer.setVisibility(View.VISIBLE);
        switch (type) {
            case TEMP_WEEK_GRAPH:
                changeTintBackground(mTempLegendAView, "#53c1bd");
                changeTintBackground(mTempLegendBView, "#5b5cbd");

                //Text
                mTempLegendA.setText("Mínima");
                mTempLegendB.setText("Máxima");
                break;
            case TEMP_HOURS_GRAPH:
                changeTintBackground(mTempLegendAView, "#FFD700");
                mTempLegendBContainer.setVisibility(View.INVISIBLE);

                //Text
                mTempLegendA.setText("Promedio");
                break;
            case TEMP_COMPARE_GRAPH:
                changeTintBackground(mTempLegendAView, "#c15357");
                changeTintBackground(mTempLegendBView, "#53C186");

                //Text
                mTempLegendA.setText("Día 1");
                mTempLegendB.setText("Día 2");
                break;
            default:
        }
    }

    private void changeTintBackground(View view, String color) {
        view.getBackground().setColorFilter(Color.parseColor(color), PorterDuff.Mode.SRC_ATOP);
    }

    @OnClick(R.id.temp_chart_week_button)
    protected void changeTempChartToWeek() {
        //Hide compare controlls
        mTempCompareControlsLayout.setVisibility(View.GONE);

        mTempChartDescriptionText.setText("Últimos 7 días");
        if (currentTempGraph != TEMP_WEEK_GRAPH) {
            if (currentTempGraph == TEMP_HOURS_GRAPH)
                fadeInOutViews(mHourlyTempChart, mWeekTempChart);
            else
                fadeInOutViews(mCompareTempChart, mWeekTempChart);
        }

        //Change legend
        showTempLegend(TEMP_WEEK_GRAPH);

        //Set current graph flag
        currentTempGraph = TEMP_WEEK_GRAPH;

        //Show appropriate data for more info card
        mMoreInfoCardTitle.setText("Últimos 7 días");
        mMoreInfoTempRecyclerView.setVisibility(View.VISIBLE);
        mMoreInfoHourlyTempRecyclerView.setVisibility(View.INVISIBLE);
        mMoreInfoCompareTempRecyclerView.setVisibility(View.INVISIBLE);
    }

    @OnClick(R.id.temp_chart_hours_button)
    protected void changeTempChartToHours() {
        //Hide compare controlls
        mTempCompareControlsLayout.setVisibility(View.GONE);

        mTempChartDescriptionText.setText("Últimas 7 horas");
        if (currentTempGraph != TEMP_HOURS_GRAPH) {
            if (currentTempGraph == TEMP_WEEK_GRAPH)
                fadeInOutViews(mWeekTempChart, mHourlyTempChart);
            else
                fadeInOutViews(mCompareTempChart, mHourlyTempChart);
        }

        //Change legend
        showTempLegend(TEMP_HOURS_GRAPH);

        //Set current graph flag
        currentTempGraph = TEMP_HOURS_GRAPH;

        //Show appropriate data for more info card
        mMoreInfoCardTitle.setText("Últimas 7 horas");
        mMoreInfoHourlyTempRecyclerView.setVisibility(View.VISIBLE);
        mMoreInfoTempRecyclerView.setVisibility(View.INVISIBLE);
        mMoreInfoCompareTempRecyclerView.setVisibility(View.INVISIBLE);
    }

    @OnClick(R.id.temp_chart_compare_button)
    protected void changeTempChartToCompare() {
        mTempChartDescriptionText.setText("Comparar días");

        if (currentTempGraph != TEMP_COMPARE_GRAPH) {
            //Show controlls
            showTempCompareControlls();
            if (currentTempGraph == TEMP_WEEK_GRAPH)
                fadeInOutViews(mWeekTempChart, mCompareTempChart);
            else
                fadeInOutViews(mHourlyTempChart, mCompareTempChart);
        }

        //Change legend
        showTempLegend(TEMP_COMPARE_GRAPH);

        //Set current graph flag
        currentTempGraph = TEMP_COMPARE_GRAPH;

        //Show appropriate data for more info card
        mMoreInfoCardTitle.setText("Comparar días");
        mMoreInfoCompareTempRecyclerView.setVisibility(View.VISIBLE);
        mMoreInfoHourlyTempRecyclerView.setVisibility(View.INVISIBLE);
        mMoreInfoTempRecyclerView.setVisibility(View.INVISIBLE);
    }

    @OnClick(R.id.temp_choose_x_day)
    protected void chooseXTemp() {
        choosingDate = TEMP_DATE_X;
        createDatePicker();
    }

    @OnClick(R.id.temp_choose_y_day)
    protected void chooseYTemp() {
        choosingDate = TEMP_DATE_Y;
        createDatePicker();
    }

    @OnClick(R.id.temp_compare_button)
    protected void compareTemps() {
        getComparisonData(tempCompareDateX, tempCompareDateY);
    }

    @OnClick(R.id.fill_graph_button)
    protected void fillTempChartLines() {
        if (!fillTempChartLines) {
            mWeekTempSet0.setFill(Color.parseColor("#5553c1bd"));
            mWeekTempSet1.setFill(Color.parseColor("#555b5cbd"));
            mHourTempSet0.setFill(Color.parseColor("#55FFD700"));
            mHourTempSet1.setFill(Color.parseColor("#5553c1bd"));
            mCompareTempSet0.setFill(Color.parseColor("#55c15357"));
            mCompareTempSet1.setFill(Color.parseColor("#5553C186"));

            //Update graphs
            mWeekTempChart.show();
            mHourlyTempChart.show();
            mCompareTempChart.show();

            //Change button
            mFillTempChartLinesButton.setImageResource(R.drawable.color_graph);

            fillTempChartLines = true;
        } else {
            mWeekTempSet0.setFill(Color.parseColor("#0053c1bd"));
            mWeekTempSet1.setFill(Color.parseColor("#005b5cbd"));
            mHourTempSet0.setFill(Color.parseColor("#00FFD700"));
            mHourTempSet1.setFill(Color.parseColor("#0053c1bd"));
            mCompareTempSet0.setFill(Color.parseColor("#00c15357"));
            mCompareTempSet1.setFill(Color.parseColor("#0053C186"));
            mWeekTempChart.show();

            //Update graphs
            mWeekTempChart.show();
            mHourlyTempChart.show();
            mCompareTempChart.show();

            //Change button
            mFillTempChartLinesButton.setImageResource(R.drawable.dark_graph);

            fillTempChartLines = false;
        }
    }

    @OnClick(R.id.display_more_info_graph_button)
    protected void displayMoreInfoTempChart() {
        mMoreInfoLayout.setVisibility(View.VISIBLE);

        if (mExpandFromMiddleCardAnimation != null)
            mExpandFromMiddleCardAnimation.cancel();

        final int cardTop = mMoreInfoCard.getTop();
        final int cardBottom = mMoreInfoCard.getBottom();
        int cardMiddle = (cardBottom / 2) + cardTop;

        //Top
        ObjectAnimator topOut = ObjectAnimator.ofInt(mMoreInfoCard, "top", cardMiddle, cardTop);
        //topOut.setDuration(4000);
        topOut.setInterpolator(new AccelerateDecelerateInterpolator());
        //Bottom
        ObjectAnimator bottomOut = ObjectAnimator.ofInt(mMoreInfoCard, "bottom", cardMiddle, cardBottom);
        //bottomOut.setDuration(4000);
        bottomOut.setInterpolator(new AccelerateDecelerateInterpolator());

        //Set initial values
        mMoreInfoCard.setBottom(cardMiddle);
        mExpandFromMiddleCardAnimation = new AnimatorSet();
        mExpandFromMiddleCardAnimation.playTogether(topOut, bottomOut);

        mExpandFromMiddleCardAnimation.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {}

            @Override
            public void onAnimationEnd(Animator animator) {}

            @Override
            public void onAnimationCancel(Animator animator) {
                mMoreInfoCard.setBottom(cardBottom);
                mMoreInfoCard.setTop(cardTop);
            }

            @Override
            public void onAnimationRepeat(Animator animator) {}
        });

        mExpandFromMiddleCardAnimation.start();
    }

    @Override
    public void started() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mMainProgressBar.setVisibility(View.VISIBLE);
            }
        });
        toggleTempChartButton(false);
    }

    @Override
    public void finished() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mMainProgressBar.setVisibility(View.INVISIBLE);
            }
        });
        toggleTempChartButton(true);
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        Date date = new GregorianCalendar(year, monthOfYear, dayOfMonth).getTime();
        if (choosingDate == TEMP_DATE_X)
            tempCompareDateX = date;
        else if (choosingDate == TEMP_DATE_Y)
            tempCompareDateY = date;

        changeCompareButtonsText();
    }
}
