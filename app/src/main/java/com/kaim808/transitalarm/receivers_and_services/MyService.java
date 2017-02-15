package com.kaim808.transitalarm.receivers_and_services;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.kaim808.transitalarm.activities.SetStopActivity;
import com.kaim808.transitalarm.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class MyService extends IntentService {

    public static final String TAG = MyService.class.getSimpleName();
    public static final String SECONDS_AWAY = "SECONDS_AWAY";

    public boolean Loop;

    private String directionID;
    private int startStopOrder;
    private int destinationStopOrder;
    private int secondsAway;
    private String APIKey = SetStopActivity.mAPIKey;

    public MyService() {
        super("test-service");
        setIntentRedelivery(false);
    }

    @Override
    public void onCreate() {
        Toast.makeText(this, "Notification call...", Toast.LENGTH_SHORT).show();
        Loop = true;
        super.onCreate();
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        String routeID = intent.getStringExtra(SetStopActivity.ROUTE_ID);
        directionID = intent.getStringExtra(SetStopActivity.DIRECTION_ID);
        startStopOrder = intent.getIntExtra(SetStopActivity.START_STOP_ORDER, -1);
        destinationStopOrder = (intent.getIntExtra(SetStopActivity.DESTINATION_STOP_ORDER, -1));

        String predictionsByRouteUrl = "http://realtime.mbta.com/developer/api/v2/predictionsbyroute?api_key="+APIKey+"&route="+routeID+"&format=json";
        String scheduleByRouteUrl = "http://realtime.mbta.com/developer/api/v2/schedulebyroute?api_key="+APIKey+"&route="+routeID+"&max_time=1440&max_trips=3&format=json";

        OkHttpClient client = new OkHttpClient();

        //parseDataAndMakeNotification(predictionsByRouteUrl, client);

        //relying on cancelAlarmReceiver to break us out of this while loop
        do{
            try {
                parseDataAndMakeNotification(predictionsByRouteUrl, client);
                //10s
                Thread.sleep(10000);
                // remove this in final
                // Loop = false;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } while (Loop);



    }

    private void parseDataAndMakeNotification(String predictionsByRouteUrl, OkHttpClient client) {
        String[] minutesAndTime = getMinutesTillDepartureAndTimeOfArrival(predictionsByRouteUrl, client, "pre_away", "pre_dt");
        if (minutesAndTime != null) {
//            Vibrator v = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
//            v.vibrate(500);

            makeNotification("Your bus leaves in " + Integer.parseInt(minutesAndTime[0]) + " minutes", minutesAndTime[1]);
        }
        else{
            //Toast.makeText(this, "Error, no prediction data available; providing scheduled data", Toast.LENGTH_SHORT).show();
//            Vibrator v = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
//            v.vibrate(500);

            makeNotification("Error, no prediction data available", "-1");
            Log.e("kaikai", "Error, no prediction data available");
            Loop = false;
            // TODO: 1/2/17 implement this edge case
            // minutesAndTime = getMinutesTillDepartureAndTimeOfArrival(scheduleByRouteUrl, client,...... )
        }
    }

    private String[] getMinutesTillDepartureAndTimeOfArrival(String apiUrl,
                                                             OkHttpClient client,
                                                             String JSONSecondsAwayName,
                                                             String JSONEpochTimeOfDepartureName) {
        Request request = new Request.Builder()
                .url(apiUrl)
                .build();

        try {
            Response response = client.newCall(request).execute();

            if (response.isSuccessful()) {
                String jsonDataString = response.body().string();

                JSONObject jsonData = new JSONObject(jsonDataString);
                JSONArray directionArray = jsonData.getJSONArray("direction");
                JSONObject directionObject = directionArray.getJSONObject(0);

                // written this way to cover typical case, in addition to
                // case where there is only direction "1"; in that case direction "1" would be at index 0
                if (!directionObject.getString("direction_id").equals(directionID)) {
                    directionObject = directionArray.getJSONObject(1);
                }

                JSONArray tripArray = directionObject.getJSONArray("trip");

                for (int i = 0; i < tripArray.length(); i++) {
                    JSONObject currentTrip = tripArray.getJSONObject(i);
                    JSONObject nextStop = currentTrip.getJSONArray("stop").getJSONObject(0);

                    int nextStopSequenceNumber = Integer.parseInt(nextStop.getString("stop_sequence"));

                    if (nextStopSequenceNumber < startStopOrder) {

                        // at this point, we know that the user's startStop is one of the next stops for the current bus's trip
                        JSONObject startStop = currentTrip.getJSONArray("stop").getJSONObject(startStopOrder - nextStopSequenceNumber);
                        int secondsTillArrival = Integer.parseInt(startStop.getString(JSONSecondsAwayName));
                        secondsAway = secondsTillArrival;
                        int minutesTillArrival = secondsTillArrival / 60;

                        JSONObject destinationStop = currentTrip.getJSONArray("stop").getJSONObject(destinationStopOrder - nextStopSequenceNumber);
                        long epochTime = Integer.parseInt(destinationStop.getString(JSONEpochTimeOfDepartureName)) * 1000L;

                        SimpleDateFormat sdf = new SimpleDateFormat("h:mm a", Locale.US);
                        sdf.setTimeZone(TimeZone.getDefault());
                        String timeString = sdf.format(epochTime);

                        return new String[]{String.valueOf(minutesTillArrival), timeString};
                    }
                }
            }
        }
        catch (IOException | JSONException e) {
            Log.e(TAG, "ERROR: ", e);
        }

        //if we reach here, we know that there was no valid trip
        return null;
    }

    private void makeNotification(String titleMessage, String estArrivalString) {
        Intent deleteIntent = new Intent(this, CancelMyAlarmReceiver.class);
        PendingIntent pendingIntentCancel = PendingIntent.getBroadcast(this, 0, deleteIntent, PendingIntent.FLAG_UPDATE_CURRENT);


        Intent i = new Intent(this, SetStopActivity.class);
        i.putExtra(SECONDS_AWAY, secondsAway);


        // called when user taps on notification
        // Retrieve a PendingIntent that will start a new activity

        /* context	Context: The Context in which this PendingIntent should start the activity.
            requestCode	int: Private request code for the sender
            intent	Intent: Intent of the activity to be launched.
            flags	int: May be FLAG_ONE_SHOT, ... or any of the flags as supported by Intent.fillIn()
            to control which unspecified parts of the intent that can be supplied when the actual send happens.
        */
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 7, i, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new NotificationCompat.Builder(this)
                .setContentTitle(titleMessage)
                .setSmallIcon(R.drawable.busicon)
                .setContentText("Reach your final stop @ " + estArrivalString)
                //execute pending intent when tapped
                .setContentIntent(pendingIntent)
                .addAction(android.R.drawable.ic_menu_close_clear_cancel, "Stop receiving notifications", pendingIntentCancel)
                .build();

        // close notification when tapped
        notification.flags = Notification.FLAG_AUTO_CANCEL;

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify("myNotification", 7, notification);

    }

    @Override
    public void onDestroy() {
        Loop = false;
        super.onDestroy();
    }
}
