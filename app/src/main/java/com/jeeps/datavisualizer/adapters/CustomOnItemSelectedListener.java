package com.jeeps.datavisualizer.adapters;

import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

/**
 * Created by jeeps on 11/16/2017.
 */

public class CustomOnItemSelectedListener implements AdapterView.OnItemSelectedListener {

    public interface SpinnerListener {
        void itemSelected(String item);
    }

    private SpinnerListener mSpinnerListener;

    public CustomOnItemSelectedListener(SpinnerListener listener) {
        mSpinnerListener = listener;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        mSpinnerListener.itemSelected(parent.getItemAtPosition(position).toString());
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
