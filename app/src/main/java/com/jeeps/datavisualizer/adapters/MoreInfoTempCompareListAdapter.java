package com.jeeps.datavisualizer.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jeeps.datavisualizer.DisplaySensorData;
import com.jeeps.datavisualizer.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by jeeps on 3/22/2018.
 */

public class MoreInfoTempCompareListAdapter extends RecyclerView.Adapter<MoreInfoTempCompareListAdapter.MoreInfoTempCompareViewHolder> {

    private SimpleDateFormat dayFormatter = new SimpleDateFormat("EEEE", new Locale("es", "EC"));
    private SimpleDateFormat dateFormatter = new SimpleDateFormat("MMMM, dd", new Locale("es", "EC"));
    private SimpleDateFormat hourFormatter = new SimpleDateFormat("HH", new Locale("es", "EC"));

    private Context mContext;
    private float[] mDataX;
    private float[] mDataY;
    private Date mDateX;
    private Date mDateY;
    private int mSelectedGraph;

    public MoreInfoTempCompareListAdapter(Context context, float[] dataX, float[] dataY, Date dateX, Date dateY, int selectedGraph) {
        mContext = context;
        mDataX = dataX;
        mDataY = dataY;
        mDateX = dateX;
        mDateY = dateY;
        mSelectedGraph = selectedGraph;
    }

    @Override
    public MoreInfoTempCompareListAdapter.MoreInfoTempCompareViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.more_info_temp_compare_list_item, parent, false);
        MoreInfoTempCompareListAdapter.MoreInfoTempCompareViewHolder viewHolder = new MoreInfoTempCompareListAdapter.MoreInfoTempCompareViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MoreInfoTempCompareListAdapter.MoreInfoTempCompareViewHolder holder, int position) {
        holder.bindSensorData(mDataX[position], mDataY[position], position);
    }

    @Override
    public int getItemCount() {
        return mDataX.length;
    }


    public class MoreInfoTempCompareViewHolder extends RecyclerView.ViewHolder {
        /*First day*/
        @BindView(R.id.day1_container) RelativeLayout mDay1Container;
        @BindView(R.id.temp_item_main_description) TextView mMainDescription;
        @BindView(R.id.temp_item_secondary_description) TextView mSecondaryDescription;
        @BindView(R.id.temp_item_image) ImageView mImage;
        @BindView(R.id.temp_item_first_value) TextView mFirstValue;

        /*Second day*/
        @BindView(R.id.day2_container) RelativeLayout mDay2Container;
        @BindView(R.id.temp_item_main_description2) TextView mMainDescription2;
        @BindView(R.id.temp_item_secondary_description2) TextView mSecondaryDescription2;
        @BindView(R.id.temp_item_image2) ImageView mImage2;
        @BindView(R.id.temp_item_first_value2) TextView mFirstValue2;

        public MoreInfoTempCompareViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            mContext = itemView.getContext();
        }

        public void bindSensorData(float dataXValue, float dataYValue, int position) {
            if ((position & 1) == 0) {
                mDay1Container.setBackgroundColor(Color.parseColor("#55c5c5c5"));
                mDay2Container.setBackgroundColor(Color.parseColor("#ffffff"));
            } else {
                mDay1Container.setBackgroundColor(Color.parseColor("#ffffff"));
                mDay2Container.setBackgroundColor(Color.parseColor("#55c5c5c5"));
            }
            
            /*First day*/
            //Date
            String dayOfWeek = dayFormatter.format(mDateX);
            String dateOfMonth = dateFormatter.format(mDateX);
            //Time
            int counter = (mDataX.length - 1) - position;
            String time = hourFormatter.format(mDateX);
            String currentHour = String.format("%s:00 - %s:59", counter, counter);
            //Cap first letter
            dayOfWeek = dayOfWeek.substring(0, 1).toUpperCase() + dayOfWeek.substring(1);
            dateOfMonth = dateOfMonth.substring(0, 1).toUpperCase() + dateOfMonth.substring(1);
            //bind data
            mMainDescription.setText(dayOfWeek + " " + dateOfMonth);
            mSecondaryDescription.setText(currentHour);
            mFirstValue.setText(String.format("%.2f\u00b0", dataXValue));

            /*Second day*/
            //Date
            String dayOfWeek2 = dayFormatter.format(mDateY);
            String dateOfMonth2 = dateFormatter.format(mDateY);
            //Time
            int counter2 = (mDataY.length - 1) - position;
            String time2 = hourFormatter.format(mDateY);
            String currentHour2 = String.format("%s:00 - %s:59", counter2, counter2);
            //Cap first letter
            dayOfWeek2 = dayOfWeek2.substring(0, 1).toUpperCase() + dayOfWeek2.substring(1);
            dateOfMonth2 = dateOfMonth2.substring(0, 1).toUpperCase() + dateOfMonth2.substring(1);

            //bind data
            mMainDescription2.setText(dayOfWeek2 + " " + dateOfMonth2);
            mSecondaryDescription2.setText(currentHour2);
            mFirstValue2.setText(String.format("%.2f\u00b0", dataYValue));

            if (mSelectedGraph == DisplaySensorData.TEMP_GRAPH) {
                //Date 1
                int thermometerDrawable = DisplaySensorData.getPercentageTermometer((int) dataXValue);
                //Date 2
                int thermometerDrawable2 = DisplaySensorData.getPercentageTermometer((int) dataYValue);
                mImage.setImageResource(thermometerDrawable);
                mImage2.setImageResource(thermometerDrawable2);
            } else if (mSelectedGraph == DisplaySensorData.HUM_GRAPH) {
                mImage.setImageResource(R.drawable.drop);
                mImage2.setImageResource(R.drawable.drop);
            } else {
                mImage.setImageResource(R.drawable.luminosity_medium);
                mImage2.setImageResource(R.drawable.luminosity_medium);
            }

        }
    }
}
