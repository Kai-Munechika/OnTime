package com.kaim808.transitalarm.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.kaim808.transitalarm.R;
import com.kaim808.transitalarm.view.MenuItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.TimeZone;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.kaim808.transitalarm.activities.SetStopActivity.PREFS_FILE;

public class TimerActivity extends AppCompatActivity {

    private int mCurrentActivityId = 0;

    public static String TAG = TimerActivity.class.getSimpleName();
    public static final ArrayList<MenuItem> mMenuItems = new ArrayList<MenuItem>(Arrays.asList(
            new MenuItem("Home", R.drawable.ic_home_white_24dp),
            new MenuItem("Set Default Bus Stops", R.drawable.star),
            new MenuItem("Set Your Bus Schedules", R.drawable.clock),
            new MenuItem("Q&A", R.drawable.question)));

    private CountDownTimer mCountDownTimer;
    private TextView countdownTextView;
    private TextView arrivesAtTextView;

    private TextView mLeavesInCaption;
    private TextView mNextCountdownTextView;
    private TextView mNextArrivesAtTextView;
    private CountDownTimer mNextCountDownTimer;

    private Button mPanicButton;
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;

    private String APIKey = SetStopActivity.mAPIKey;

    private CountDownTimer[] mCountDownTimers = new CountDownTimer[2];

    private ImageButton mMenuButton;
    private ListView mDrawerList;
    private DrawerLayout mDrawerLayout;

    OkHttpClient mClient;

    private boolean saidTryAgainLater = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);

        countdownTextView = (TextView) findViewById(R.id.countdownTextView);
        arrivesAtTextView = (TextView) findViewById(R.id.arrivesAtTextView);
        mPanicButton = (Button) findViewById(R.id.panicButton);

        mLeavesInCaption = (TextView) findViewById(R.id.leavesInCaption);
        mNextCountdownTextView = (TextView) findViewById(R.id.nextCountdownTextView);

        mNextArrivesAtTextView = (TextView) findViewById(R.id.nextArrivesAtTextView);

        mLeavesInCaption.setVisibility(View.INVISIBLE);
        mNextCountdownTextView.setVisibility(View.INVISIBLE);
        mNextArrivesAtTextView.setVisibility(View.INVISIBLE);

        mSharedPreferences = getSharedPreferences(PREFS_FILE, MODE_PRIVATE);
        //mEditor = mSharedPreferences.edit();

        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mMenuButton = (ImageButton) findViewById(R.id.menu_button);

        QandA.setupNavigationDrawer(mMenuButton, mDrawerLayout, mDrawerList, this, mCurrentActivityId);



        if (mSharedPreferences.contains(SetStopActivity.ROUTE_ID)) {

            final String routeId = mSharedPreferences.getString(SetStopActivity.ROUTE_ID, null);
            final String directionNum = mSharedPreferences.getInt(SetStopActivity.DIRECTION_NUM, -1)+"";
            final int startStopOrder = Integer.valueOf(mSharedPreferences.getString(SetStopActivity.START_STOP_ORDER, null));
            final int destinationStopOrder = Integer.valueOf(mSharedPreferences.getString(SetStopActivity.DESTINATION_STOP_ORDER, null));

            String predictionsByRouteUrl = "http://realtime.mbta.com/developer/api/v2/predictionsbyroute?api_key="+APIKey+"&route="+routeId+"&format=json";

//            OkHttpClient client = new OkHttpClient();
            mClient = new OkHttpClient();
            final Request request = new Request.Builder().url(predictionsByRouteUrl).build();
            final Call call = mClient.newCall(request);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        try{
                            updateCountdown(directionNum, startStopOrder, destinationStopOrder, request);
                            Thread.sleep(30000);
                        }
                        catch (Exception e) {
                            Log.e("kaikai", "From TimerActivity line 99: " + e);
                        }
                    }
                }
            }).start();

        }
        else {
            Intent intent = new Intent(this, SetStopActivity.class);
            startActivity(intent);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mDrawerList.setItemChecked(0, true);
    }

    private void updateCountdown(final String directionNum, final int startStopOrder, final int destinationStopOrder, Request request) {
        Call call = mClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                onFailureToastMessage();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                // index 0: seconds away from start stop.               index 1: formatted time string of arrival at destination stop.
                // index 2: seconds away from start stop for next trip. index 3: formatted time string of arrival at destination stop for next trip.
                final String[] secondsAndTimeStringArray = new String[4];
                int arrayCurrentIndex = 0;

                try {
                    if (response.isSuccessful()) {
                        String jsonDataString = response.body().string();

                        JSONObject jsonData = new JSONObject(jsonDataString);
                        JSONArray directionArray = jsonData.getJSONArray("direction");
                        JSONObject directionObject = directionArray.getJSONObject(0);

                        // written this way to cover typical case, in addition to
                        // case where there is only direction "1"; in that case direction "1" would be at index 0
                        if (!directionObject.getString("direction_id").equals(directionNum)) {
                            directionObject = directionArray.getJSONObject(1);
                        }

                        JSONArray tripArray = directionObject.getJSONArray("trip");

                        for (int i = 0; i < tripArray.length() && arrayCurrentIndex < 4; i++) {
                            JSONObject currentTrip = tripArray.getJSONObject(i);
                            JSONObject nextStop = currentTrip.getJSONArray("stop").getJSONObject(0);

                            int nextStopSequenceNumber = Integer.parseInt(nextStop.getString("stop_sequence"));

                            if (nextStopSequenceNumber < startStopOrder) {

                                // at this point, we know that the user's startStop is one of the next stops for the current bus's trip
                                JSONObject startStop = currentTrip.getJSONArray("stop").getJSONObject(startStopOrder - nextStopSequenceNumber);

                                // this is seconds till arrival at the start stop
                                int secondsTillArrival = Integer.parseInt(startStop.getString("pre_away"));

                                secondsAndTimeStringArray[arrayCurrentIndex] = ""+secondsTillArrival;
                                arrayCurrentIndex++;


                                JSONObject destinationStop = currentTrip.getJSONArray("stop").getJSONObject(destinationStopOrder - nextStopSequenceNumber);
                                long epochTime = Integer.parseInt(destinationStop.getString("pre_dt")) * 1000L;

                                SimpleDateFormat sdf = new SimpleDateFormat("h:mm a", Locale.US);
                                sdf.setTimeZone(TimeZone.getDefault());

                                // this is the estimated time of arrival
                                String timeString = sdf.format(epochTime);

                                secondsAndTimeStringArray[arrayCurrentIndex] = timeString;
                                arrayCurrentIndex++;

                            }
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                initializeCountdownTimer(Integer.valueOf(secondsAndTimeStringArray[0])*1000, 0, countdownTextView);
                                arrivesAtTextView.setText("Reaches destination @ " + secondsAndTimeStringArray[1]);

                                mPanicButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        mLeavesInCaption.setVisibility(View.VISIBLE);

                                        initializeCountdownTimer(Integer.valueOf(secondsAndTimeStringArray[2])*1000, 1, mNextCountdownTextView);
                                        mNextCountdownTextView.setVisibility(View.VISIBLE);

                                        mNextArrivesAtTextView.setText("This bus will arrive at your destination stop @ " + secondsAndTimeStringArray[3]);
                                        mNextArrivesAtTextView.setVisibility(View.VISIBLE);


                                    }
                                });

                            }
                        });
                    }
                    else {
                        if (!saidTryAgainLater) {
                            debugToast("No data currently available, please try again later");
                            saidTryAgainLater = true;
                        }
                    }
                }
                catch (IOException | JSONException e) {
                    Log.e(TAG, "ERROR: ", e);
                }
            }
        });
    }


    private void initializeCountdownTimer(long ms, int index, final TextView view) {
        if (mCountDownTimers[index]!= null) {
            mCountDownTimers[index].cancel();
        }
        mCountDownTimers[index] = new CountDownTimer(ms, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                // 6000 = /1000/60
                long minutes = millisUntilFinished/60000;
                long seconds = (millisUntilFinished/1000) % 60;

                String minutesString = minutes < 10 ? "0"+minutes : String.valueOf(minutes);
                String secondsString = seconds < 10 ? "0"+seconds : String.valueOf(seconds);

                view.setText(minutesString + ":" + secondsString);
            }

            @Override
            public void onFinish() { view.setText("00:00"); }
        }.start();
    }

    public static void openMenu(DrawerLayout drawerLayout, ListView drawerList) {
//        Intent intent = new Intent(this, MenuLayoutActivity.class);
        drawerLayout.openDrawer(drawerList, true);
        //finish();


    }
    private void debugToast(final String displayText) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(TimerActivity.this, displayText, Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void onFailureToastMessage() {
        debugToast("Network failure, try again");
    }





}
