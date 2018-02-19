package com.jeeps.datavisualizer.controller;

import android.os.Handler;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hookedonplay.decoviewlib.events.DecoEvent;
import com.jeeps.datavisualizer.DisplaySensorData;
import com.jeeps.datavisualizer.model.SensorData;

/**
 * Created by jeeps on 1/25/2018.
 */

public class FireBaseHelper {

    private FirebaseDatabase mDatabase;
    private DatabaseReference mDatabaseReference;

    private SensorData mSensorData;
    private FireBaseListener mListener;

    public interface FireBaseListener {
        void update(SensorData sensorData);
    }

    public FireBaseHelper(FireBaseListener listener) {
        mListener = listener;
    }

    public void openConnection() {
        //Sensor data
        mDatabase = FirebaseDatabase.getInstance();

        mDatabaseReference = mDatabase.getReference("object");
        mDatabaseReference.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                mSensorData = dataSnapshot.getValue(SensorData.class);

                mListener.update(mSensorData);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(DisplaySensorData.TAG, "Failed to read value.", error.toException());
            }
        });
    }
}
