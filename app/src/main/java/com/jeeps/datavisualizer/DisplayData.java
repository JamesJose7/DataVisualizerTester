package com.jeeps.datavisualizer;

import android.graphics.Color;
import android.graphics.PointF;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hookedonplay.decoviewlib.DecoView;
import com.hookedonplay.decoviewlib.charts.SeriesItem;
import com.hookedonplay.decoviewlib.charts.SeriesLabel;
import com.hookedonplay.decoviewlib.events.DecoEvent;
import com.jeeps.datavisualizer.model.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class DisplayData extends AppCompatActivity {

    @BindView(R.id.data_1) TextView mDataView1;
    @BindView(R.id.data_2) TextView mDataView2;
    @BindView(R.id.data_3) TextView mDataView3;
    @BindView(R.id.data_4) TextView mDataView4;
    @BindView(R.id.data_5) TextView mDataView5;
    @BindView(R.id.data_6) TextView mDataView6;

    private List<User> mUsers;
    private DecoView mArcView;
    private ProgressBar mProgressBar;

    private int mDataTotal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_data);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ButterKnife.bind(this);

        mArcView = (DecoView)findViewById(R.id.dynamicArcView);
        mProgressBar = (ProgressBar) findViewById(R.id.data_progress_bar);

        displayDecoView();
        try {
            requestService();
        } catch (Exception e) {
            e.printStackTrace();
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void displayDecoView() {
        /*arcView.addEvent(new DecoEvent.Builder(DecoEvent.EventType.EVENT_SHOW, true)
                .setDelay(1000)
                .setDuration(2000)
                .build());*/
        // Create background track
        mArcView.addSeries(getBackgroundTrack(0, 0, 64f));
        mArcView.addSeries(getBackgroundTrack(64f, 64f, 64f));
        mArcView.addSeries(getBackgroundTrack(128f, 128f, 64f));
        mArcView.addSeries(getBackgroundTrack(192f, 192f, 64f));
        mArcView.addSeries(getBackgroundTrack(256f, 256f, 64f));
        mArcView.addSeries(getBackgroundTrack(320f, 320f, 64f));


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


    private final OkHttpClient client = new OkHttpClient();

    public void requestService() throws Exception {
        Request request = new Request.Builder()
                .url("http://jamescloud.hopto.org:8080/logs")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try (ResponseBody responseBody = response.body()) {
                    if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                    Headers responseHeaders = response.headers();
                    for (int i = 0, size = responseHeaders.size(); i < size; i++) {
                        System.out.println(responseHeaders.name(i) + ": " + responseHeaders.value(i));
                    }
                    extractData(responseBody.string());
                    System.out.println();
                }
            }
        });
    }

    private void extractData(String string) {
        try {
            JSONArray data = new JSONArray(string);
            List<String> list = new ArrayList<>();

            //Set data total
            mDataTotal = data.length();

            for (int i = 0; i < data.length(); i++) {
                JSONObject jsonObject = data.getJSONObject(i);

                String agent = jsonObject.getString("agent");
                //mUsers.add(new User(agent));
                list.add(agent);
            }

            //Map
            Map<String, Integer> agentsRepetitions = new HashMap<>();
            for (String ag : list) {
                if (!agentsRepetitions.containsKey(ag)) {
                    agentsRepetitions.put(ag, 1);
                } else {
                    //Get previous count
                    int currentCount = agentsRepetitions.get(ag);
                    //Remove current key
                    agentsRepetitions.remove(ag);
                    //Increment and put the key again
                    currentCount++;
                    agentsRepetitions.put(ag, currentCount);
                }
            }

            final int arrayMaxs[] = new int[5];
            final String arrayMaxsKeys[] = new String[5];
            final StringBuilder s = new StringBuilder();
            for (Map.Entry<String, Integer> entry : agentsRepetitions.entrySet()) {
                int reps = entry.getValue();

                if (reps > arrayMaxs[0]) {
                    arrayMaxs[0] = reps;
                    arrayMaxsKeys[0] = entry.getKey();
                } else if (reps > arrayMaxs[1]) {
                    arrayMaxs[1] = reps;
                    arrayMaxsKeys[1] = entry.getKey();
                } else if (reps > arrayMaxs[2]) {
                    arrayMaxs[2] = reps;
                    arrayMaxsKeys[2] = entry.getKey();
                } else if (reps > arrayMaxs[3]) {
                    arrayMaxs[3] = reps;
                    arrayMaxsKeys[3] = entry.getKey();
                } else if (reps > arrayMaxs[4]) {
                    arrayMaxs[4] = reps;
                    arrayMaxsKeys[4] = entry.getKey();
                }
            }

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mProgressBar.setVisibility(View.INVISIBLE);
                    showData(arrayMaxs, arrayMaxsKeys);
                }
            });


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void showData(final int[] arrayMaxs, String[] arrayMaxsKeys) {
        //Calculate percentages
        int data1 = calculatePercentage(arrayMaxs[0]);
        int data2 = calculatePercentage(arrayMaxs[1]);
        int data3 = calculatePercentage(arrayMaxs[2]);
        int data4 = calculatePercentage(arrayMaxs[3]);
        int data5 = calculatePercentage(arrayMaxs[4]);
        //Remaining
        int data6 = 100 - (data1 + data2 + data3 + data4 + data5);

        //Create data series track
        int s1 = mArcView.addSeries(createDataSeries(0, 0, 64f, 0, "#56d1c0"));
        int s2 = mArcView.addSeries(createDataSeries(64f, 64f, 64f, 0, "#60eba8"));
        int s3 = mArcView.addSeries(createDataSeries(128f, 128f, 64f, 0, "#78eb60"));
        int s4 = mArcView.addSeries(createDataSeries(192f, 192f, 64f, 0, "#cef866"));
        int s5 = mArcView.addSeries(createDataSeries(256f, 256f, 64f, 0, "#f9f260"));
        int s6 = mArcView.addSeries(createDataSeries(320f, 320f, 64f, 0, "#f9a660"));

        //Animate
        mArcView.addEvent(new DecoEvent.Builder(data6).setIndex(s1).setDelay(1000).build());
        mArcView.addEvent(new DecoEvent.Builder(data1).setIndex(s2).setDelay(1500).build());
        mArcView.addEvent(new DecoEvent.Builder(data2).setIndex(s3).setDelay(2000).build());
        mArcView.addEvent(new DecoEvent.Builder(data3).setIndex(s4).setDelay(2500).build());
        mArcView.addEvent(new DecoEvent.Builder(data4).setIndex(s5).setDelay(3000).build());
        mArcView.addEvent(new DecoEvent.Builder(data5).setIndex(s6).setDelay(3500).build());

        //SetText
        mDataView1.setText(String.format("%d - %s", data6, "Other browsers"));
        mDataView2.setText(String.format("%d - %s", data1, arrayMaxsKeys[0]));
        mDataView3.setText(String.format("%d - %s", data2, arrayMaxsKeys[1]));
        mDataView4.setText(String.format("%d - %s", data3, arrayMaxsKeys[2]));
        mDataView5.setText(String.format("%d - %s", data4, arrayMaxsKeys[3]));
        mDataView6.setText(String.format("%d - %s", data5, arrayMaxsKeys[4]));
    }

    private int calculatePercentage(int arrayMax) {
        return (arrayMax * 100) / mDataTotal;
    }
}
