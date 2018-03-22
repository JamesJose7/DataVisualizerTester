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
    private List<Float> mTemperatures;
    private SensorData mSensorData;
    private int mType;

    public MoreInfoTempListAdapter(Context context, List<Float> temperatures, SensorData sensorData, int type) {
        mContext = context;
        mTemperatures = temperatures;
        mSensorData = sensorData;
        mType = type;
    }

    @Override
    public MoreInfoTempViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.more_info_temp_list_item, parent, false);
        MoreInfoTempViewHolder viewHolder = new MoreInfoTempViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MoreInfoTempViewHolder holder, int position) {
        holder.bindSensorData(mSensorData, mTemperatures.get(position), position);
    }

    @Override
    public int getItemCount() {
        return mTemperatures.size();
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
                float maxTemp = sensorData.getWeeklyTemperatureMax().get(position);
                float minTemp = sensorData.getWeeklyTemperatureMin().get(position);
                int thermometerDrawable = DisplaySensorData.getPercentageTermometer((int) maxTemp);
                //bind data
                mMainDescription.setText(dayOfWeek);
                mSecondaryDescription.setText(dateOfMonth);
                mImage.setImageResource(thermometerDrawable);
                mFirstValue.setText(String.format("%.2f\u00b0", maxTemp));
                mSecondaryValue.setText(String.format("%.2f\u00b0", minTemp));
            } else if (mType == LAST_7_HOURS) {
                //Time
                Date hour = SensorDataParser.getPreviousHourDate(position);
                String time = hourFormatter.format(hour);
                String currentHour = String.format("%s:00 - %s:59", time, time);

                String previousHours = String.format("Hace %d horas", position);
                if (position == 0)
                    previousHours = "Actualmente";
                //Get values
                float temp = sensorData.getHourlyTemperature().get(position);
                int thermometerDrawable = DisplaySensorData.getPercentageTermometer((int) temp);
                //Bind data
                mMainDescription.setText(currentHour);
                mSecondaryDescription.setText(previousHours);
                mImage.setImageResource(thermometerDrawable);
                mFirstValue.setText(String.format("%.2f\u00b0", temp));
                mSecondaryValue.setText("");
            }
        }
    }
}
