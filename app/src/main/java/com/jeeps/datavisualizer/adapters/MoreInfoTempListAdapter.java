package com.jeeps.datavisualizer.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jeeps.datavisualizer.DisplaySensorData;
import com.jeeps.datavisualizer.R;
import com.jeeps.datavisualizer.controller.SensorDataParser;
import com.jeeps.datavisualizer.model.SensorData;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by jeeps on 3/19/2018.
 */

public class MoreInfoTempListAdapter extends RecyclerView.Adapter<MoreInfoTempListAdapter.MoreInfoTempViewHolder> {

    public static final int LAST_7_DAYS = 0;
    public static final int LAST_7_HOURS = 1;

    private SimpleDateFormat dayFormatter = new SimpleDateFormat("EEEE", new Locale("es", "EC"));
    private SimpleDateFormat dateFormatter = new SimpleDateFormat("MMMM, dd", new Locale("es", "EC"));
    private SimpleDateFormat hourFormatter = new SimpleDateFormat("HH", new Locale("es", "EC"));

    private Context mContext;
    private List<Float> mData;
    private SensorData mSensorData;
    private List<Float> mHourlyTemperatures;
    private int mType;
    private int mSelectedGraph;

    public MoreInfoTempListAdapter(Context context, List<Float> data, SensorData sensorData, int type, int selectedGraph) {
        mContext = context;
        mData = data;
        mSensorData = sensorData;
        mType = type;
        mSelectedGraph = selectedGraph;

        List<Float> hourlyTemperatures;
        if (selectedGraph == DisplaySensorData.TEMP_GRAPH) {
            hourlyTemperatures = mSensorData.getHourlyTemperature();
        } else if (selectedGraph == DisplaySensorData.HUM_GRAPH) {
            hourlyTemperatures = mSensorData.getHourlyHumidity();
        } else {
            hourlyTemperatures = mSensorData.getHourlyLuminosity();
        }

        mHourlyTemperatures = new ArrayList<>(hourlyTemperatures);
        Collections.reverse(mHourlyTemperatures);
    }

    @Override
    public MoreInfoTempViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.more_info_temp_list_item, parent, false);
        MoreInfoTempViewHolder viewHolder = new MoreInfoTempViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MoreInfoTempViewHolder holder, int position) {
        if (mType == LAST_7_DAYS)
            holder.bindSensorData(mSensorData, mData.get(position), position);
        else
            holder.bindSensorData(mSensorData, mHourlyTemperatures.get(position), position);

    }

    @Override
    public int getItemCount() {
        if (mType == LAST_7_DAYS)
            return mData.size();
        else
            return mHourlyTemperatures.size();
    }


    public class MoreInfoTempViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.temp_item_main_description) TextView mMainDescription;
        @BindView(R.id.temp_item_secondary_description) TextView mSecondaryDescription;
        @BindView(R.id.temp_item_image) ImageView mImage;
        @BindView(R.id.temp_item_first_value) TextView mFirstValue;
        @BindView(R.id.temp_item_secondary_value) TextView mSecondaryValue;

        public MoreInfoTempViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            mContext = itemView.getContext();
        }

        public void bindSensorData(SensorData sensorData, float temperature, int position) {



            if (mType == LAST_7_DAYS) {
                //Date
                Date date = SensorDataParser.getPreviousDayDate(position);
                String dayOfWeek = dayFormatter.format(date);
                String dateOfMonth = dateFormatter.format(date);
                //Cap first letter
                dayOfWeek = dayOfWeek.substring(0, 1).toUpperCase() + dayOfWeek.substring(1);
                dateOfMonth = dateOfMonth.substring(0, 1).toUpperCase() + dateOfMonth.substring(1);
                //Get values
                float maxTemp;
                float minTemp;
                int invertedPosition = 6 - position;
                if (mSelectedGraph == DisplaySensorData.TEMP_GRAPH) {
                    maxTemp = sensorData.getWeeklyTemperatureMax().get(invertedPosition);
                    minTemp = sensorData.getWeeklyTemperatureMin().get(invertedPosition);
                } else if (mSelectedGraph == DisplaySensorData.HUM_GRAPH) {
                    maxTemp = sensorData.getWeeklyHumidityMax().get(invertedPosition);
                    minTemp = sensorData.getWeeklyHumidityMin().get(invertedPosition);
                } else {
                    maxTemp = sensorData.getWeeklyLuminosityMax().get(invertedPosition);
                    minTemp = sensorData.getWeeklyLuminosityMin().get(invertedPosition);
                }

                //bind data
                if (mSelectedGraph == DisplaySensorData.TEMP_GRAPH) {
                    int thermometerDrawable = DisplaySensorData.getPercentageTermometer((int) maxTemp);
                    mImage.setImageResource(thermometerDrawable);
                } else if (mSelectedGraph == DisplaySensorData.HUM_GRAPH) {
                    mImage.setImageResource(R.drawable.drop);
                } else {
                    mImage.setImageResource(R.drawable.luminosity_medium);
                }

                mMainDescription.setText(dayOfWeek);
                mSecondaryDescription.setText(dateOfMonth);
                mFirstValue.setText(String.format("%.2f\u00b0", maxTemp));
                mSecondaryValue.setText(String.format("%.2f\u00b0", minTemp));
            } else if (mType == LAST_7_HOURS) {
                //TODO Show all previous hours instead of just 7
                //Time
                Date hour = SensorDataParser.getPreviousHourDate(position);
                String time = hourFormatter.format(hour);
                String currentHour = String.format("%s:00 - %s:59", time, time);

                String previousHours = String.format("Hace %d horas", position);
                if (position == 0)
                    previousHours = "Actualmente";
                //Get values
                float temp = mHourlyTemperatures.get(position);

                //Bind data
                if (mSelectedGraph == DisplaySensorData.TEMP_GRAPH) {
                    int thermometerDrawable = DisplaySensorData.getPercentageTermometer((int) temp);
                    mImage.setImageResource(thermometerDrawable);
                } else if (mSelectedGraph == DisplaySensorData.HUM_GRAPH) {
                    mImage.setImageResource(R.drawable.drop);
                } else {
                    mImage.setImageResource(R.drawable.luminosity_medium);
                }
                mMainDescription.setText(currentHour);
                mSecondaryDescription.setText(previousHours);
                mFirstValue.setText(String.format("%.2f\u00b0", temp));
                mSecondaryValue.setText("");
            }
        }
    }
}
