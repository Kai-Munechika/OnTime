package com.kaim808.transitalarm.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.kaim808.transitalarm.R;
import com.kaim808.transitalarm.model.Route;
import com.kaim808.transitalarm.model.Stop;
import com.kaim808.transitalarm.view.mySearchableSpinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


/*
 * NOTE: CURRENTLY SUPPORTS ONLY BUSES
 * adding in support for everything else after it works, and should be easy
 */

//// TODO: 1/25/17 I need to separate the bus details from the set times


// TODO: 1/1/17: cover case when network not available, before any api calls

public class SetStopActivity extends AppCompatActivity {

        /* Things I need:
            - routeID: mRoutes[mRoutesSpinner.getSelectedItemPosition()].getId();
            - routeDirection: mDirections[mDirectionSpinner.getSelectedItemPosition()];
            - directionNum: mDirectionSpinner.getSelectedItemPosition();
            - startStopOrder: mStops[mStartingStopSpinner.getSelectedItemPosition()].getStopOrder();
            - endStopOrder: mStops[mDestinationStopSpinner.getSelectedItemPosition()].getStopOrder();
         */

    public static final String TAG = SetStopActivity.class.getSimpleName();
    public static final String PREFS_FILE = "PREFS_FILE";
    public static final String SELECTED_ROUTE_POSITION = "selectedRoutePosition";
    public static final String SELECTED_DIRECTION_POSITION = "selectedDirectionPosition";
    public static final String SELECTED_START_STOP_POSITION = "selectedStartStopPosition";
    public static final String SELECTED_DESTINATION_STOP_POSITION = "selectedDestinationStopPosition";
    public static String DAY = "DAY";
    public static String ROUTE_ID = "ROUTE_ID";
    public static String ROUTE_DIRECTION = "ROUTE_DIRECTION";
    public static String DIRECTION_NUM = "DIRECTION_NUM";
    public static String DIRECTION_ID = "DIRECTION_ID";
    public static String START_STOP_ORDER = "START_STOP_ORDER";
    public static String DESTINATION_STOP_ORDER = "DESTINATION_STOP_ORDER";



    /* these two might be used for debugging/double-checking if needed */
//    public static String START_STOP_ID = "START_STOP_ID";
//    public static String DESTINATION_STOP_ID = "DESTINATION_STOP_ID";

    private mySearchableSpinner mRoutesSpinner;
    private mySearchableSpinner mDirectionSpinner;
    private mySearchableSpinner mStartingStopSpinner;
    private mySearchableSpinner mDestinationStopSpinner;

    //startNotificationsButton is assigned an OnClick method from the xml
    private Button mStopNotificationsButton;



    // array of all routes served by MBTA (current implementation covers only buses); holds route name and id
    private Route[] mRoutes;

    // array of at most 2 elements; typically inbound/outbound or northbound/southbound
    private String[] mDirections;

    // array of at most 2 elements; final destination of route; headSign index corresponds with direction
    private String[] mHeadSigns;

    // array of all stops of selected route and direction
    private Stop[] mStops;

    // used for each api call
    public static final OkHttpClient client = new OkHttpClient();
    public static String mAPIKey = "lASN3s3xhEC_0_BGeXgURQ";

    private ImageView routePinkPencil;
    private TextView routeTextViewToBeUpdated;
    private ImageView directionPinkPencil;
    private TextView directionTextViewToBeUpdated;
    private ImageView startingStopPinkPencil;
    private TextView startingStopTextViewToBeUpdated;
    private ImageView destinationStopPinkPencil;
    private TextView destinationStopTextViewToBeUpdated;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.set_bus_stops);

        mSharedPreferences = getSharedPreferences(PREFS_FILE, MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();

//        try {
//            Bundle bundle = getIntent().getExtras();
//            Toast.makeText(this, bundle.getInt(MyService.SECONDS_AWAY, -2), Toast.LENGTH_LONG).show();
//        }
//        catch (Exception e) {
//            debugToast(""+e);
//        }

        routePinkPencil = (ImageView) findViewById(R.id.routePinkPencil);
        routeTextViewToBeUpdated = (TextView) findViewById(R.id.routeNameTextView);
        directionPinkPencil = (ImageView) findViewById(R.id.directionPinkPencil);
        directionTextViewToBeUpdated = (TextView) findViewById(R.id.directionNameTextView);
        startingStopPinkPencil = (ImageView) findViewById(R.id.startingStopPinkPencil);
        startingStopTextViewToBeUpdated  = (TextView) findViewById(R.id.startingBusStopTextView);
        destinationStopPinkPencil = (ImageView) findViewById(R.id.destinationStopPinkPencil);
        destinationStopTextViewToBeUpdated = (TextView) findViewById(R.id.destinationStopTextView);


        mRoutesSpinner = (mySearchableSpinner) findViewById(R.id.routesSpinner);
        mDirectionSpinner = (mySearchableSpinner) findViewById(R.id.directionSpinner);
        mStartingStopSpinner = (mySearchableSpinner) findViewById(R.id.startingStopSpinner);
        mDestinationStopSpinner = (mySearchableSpinner) findViewById(R.id.destinationStopSpinner);
        mStopNotificationsButton = (Button) findViewById(R.id.stopButton);

        mRoutesSpinner.setTouchViewTriggerAndGetTextViewToBeUpdated(routePinkPencil, routeTextViewToBeUpdated);
        mDirectionSpinner.setTouchViewTriggerAndGetTextViewToBeUpdated(directionPinkPencil, directionTextViewToBeUpdated);
        mStartingStopSpinner.setTouchViewTriggerAndGetTextViewToBeUpdated(startingStopPinkPencil, startingStopTextViewToBeUpdated);
        mDestinationStopSpinner.setTouchViewTriggerAndGetTextViewToBeUpdated(destinationStopPinkPencil, destinationStopTextViewToBeUpdated);


        mRoutesSpinner         .setTitle("Select a Route");
        mStartingStopSpinner   .setTitle("Select a Starting Stop");
        mDestinationStopSpinner.setTitle("Select a Destination Stop");
        mDirectionSpinner      .setTitle("Select a Direction");
        mRoutesSpinner         .setPositiveButton("Close");
        mStartingStopSpinner   .setPositiveButton("Close");
        mDestinationStopSpinner.setPositiveButton("Close");
        mDirectionSpinner      .setPositiveButton("Close");

        // populates mRoutesSpinner with list of routes
        routeCall(client);

        //populates mDirectionSpinner with direction(s) and headsign, and mStopsSpinner with stops for given route and direction
        directionAndHeadSignCall(client);

        }

    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;

    public void save(View v) {

        /* Things I need:
            - routeID: mRoutes[mRoutesSpinner.getSelectedItemPosition()].getId();
            - routeDirection: mDirections[mDirectionSpinner.getSelectedItemPosition()];
            - directionNum: mDirectionSpinner.getSelectedItemPosition();
            - startStopOrder: mStops[mStartingStopSpinner.getSelectedItemPosition()].getStopOrder();
            - endStopOrder: mStops[mDestinationStopSpinner.getSelectedItemPosition()].getStopOrder();
         */

        if (mRoutesSpinner.getCount() == 0 || mDirectionSpinner.getCount() == 0 || mStartingStopSpinner.getCount() == 0) {
            Toast.makeText(getApplicationContext(),"Please select another bus stop, or try again later", Toast.LENGTH_LONG).show();
        }
        else if (mStartingStopSpinner.getSelectedItemPosition() >= mDestinationStopSpinner.getSelectedItemPosition()) {
            Toast.makeText(getApplicationContext(), "Please select a final destination stop that is after" +
                    " your initial stop along the route", Toast.LENGTH_SHORT).show();
        }
        else {
            mEditor.putInt(SELECTED_ROUTE_POSITION, mRoutesSpinner.getSelectedItemPosition());
            mEditor.putInt(SELECTED_DIRECTION_POSITION, mDirectionSpinner.getSelectedItemPosition());
            mEditor.putInt(SELECTED_START_STOP_POSITION, mStartingStopSpinner.getSelectedItemPosition());
            mEditor.putInt(SELECTED_DESTINATION_STOP_POSITION, mDestinationStopSpinner.getSelectedItemPosition());

            mEditor.putString(ROUTE_ID, mRoutes[mRoutesSpinner.getSelectedItemPosition()].getId());
            mEditor.putString(ROUTE_DIRECTION, mDirections[mDirectionSpinner.getSelectedItemPosition()]);
            mEditor.putInt(DIRECTION_NUM, mDirectionSpinner.getSelectedItemPosition());
            mEditor.putString(START_STOP_ORDER, mStops[mStartingStopSpinner.getSelectedItemPosition()].getStopOrder());
            mEditor.putString(DESTINATION_STOP_ORDER, mStops[mDestinationStopSpinner.getSelectedItemPosition()].getStopOrder());
            mEditor.apply();
            Toast.makeText(getApplicationContext(), "Saved Successfully", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(this, TimeScheduleActivity.class);
            startActivity(intent);
        }
    }



    private void routeCall(OkHttpClient client) {
        String routesUrl = "http://realtime.mbta.com/developer/api/v2/routes?api_key=" + mAPIKey + "&format=json";
        Request request = new Request.Builder().url(routesUrl).build();
        Call call = client.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                onFailureToastMessage();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                try {
                    String jsonData = response.body().string();

                    if (response.isSuccessful()) {
                        //updates mRoutes
                        processRoutesData(jsonData);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //connect mRoutes to mRoutesSpinner
                                bindRoutes();
                                if (mSharedPreferences.contains(SELECTED_ROUTE_POSITION)) {
                                    mRoutesSpinner.setSelection(mSharedPreferences.getInt(SELECTED_ROUTE_POSITION, 0));
                                    routeTextViewToBeUpdated.setText(mRoutesSpinner.getSelectedItem().toString());
                                }
                                else {
                                    if (mRoutesSpinner.getCount() != 0) {
                                        routeTextViewToBeUpdated.setText(mRoutesSpinner.getSelectedItem().toString());
                                    }
                                    else {
                                        routeTextViewToBeUpdated.setText("None");
                                    }
                                }
                            }
                        });
                    }
                }
                catch (IOException | JSONException e) {
                    Log.v(TAG, "Error:", e);
                }
            }
        });
    }


    // getting the direction and headSign uses the same api call
    private void directionAndHeadSignCall(final OkHttpClient client) {

        mRoutesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                final String selectedRoute = mRoutes[position].getId();
                String directionAndHeadSignUrl = "http://realtime.mbta.com/developer/api/v2/schedulebyroutes?api_key=" + mAPIKey + "&routes=" + selectedRoute
                        + "&max_time=720&max_trips=1&format=json";

                Request request = new Request.Builder().url(directionAndHeadSignUrl).build();
                Call call = client.newCall(request);

                call.enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        onFailureToastMessage();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        try {
                            String jsonData = response.body().string();

//                          complete error message: { \"error\": { \"message\": \"No data for  route "+selectedRoute+"\" }}"
                            if (jsonData.substring(3,8).equals("error")) {
                                debugToast("Bus data unavailable; please try another stop"+"\n\n" + jsonData);
                                resetSpinners();
                                return;
                            }

                            if (response.isSuccessful()) {

                                //update mDirections and mHeadSigns based on jsonData
                                processDirectionAndHeadSignData(jsonData);

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                        //connect mDirections and mHeadSigns with mDirectionSpinner
                                        bindDirections();
                                        if (mSharedPreferences.contains(SELECTED_DIRECTION_POSITION)) {
                                            mDirectionSpinner.setSelection(mSharedPreferences.getInt(SELECTED_DIRECTION_POSITION, 0));
                                            directionTextViewToBeUpdated.setText(mDirectionSpinner.getSelectedItem().toString());
                                        }
                                        else {
                                            if (mDirectionSpinner.getCount() != 0) {
                                                directionTextViewToBeUpdated.setText(mDirectionSpinner.getSelectedItem().toString());
                                            } else {
                                                directionTextViewToBeUpdated.setText("None");
                                            }
                                        }

                                        //populate both stop spinners
                                        stopCall(client, selectedRoute);
                                    }
                                });
                            }
                        } catch (IOException | JSONException e) {
                            Log.e(TAG, "Error:", e);
                        }
                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }


    private void stopCall(final OkHttpClient client, final String selectedRoute) {
        mDirectionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, final int position, long id) {
                String stopsUrl = "http://realtime.mbta.com/developer/api/v2/stopsbyroute?api_key=" + mAPIKey + "&route=" + selectedRoute + "&format=json";

                Request request = new Request.Builder().url(stopsUrl).build();
                Call call = client.newCall(request);
                call.enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        onFailureToastMessage();
                    }

                    String direction = mDirections[position];

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        try {
                            String jsonData = response.body().string();
                            if (response.isSuccessful()) {

                                //updates mStops based on jsonData
                                processStopData(jsonData, direction);
                            }

                        }
                        catch (IOException | JSONException e) {
                            Log.e(TAG, "Error:", e);
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // populate stops spinners
                                bindStops();
                                if (mSharedPreferences.contains(SELECTED_START_STOP_POSITION)) {
                                    mStartingStopSpinner.setSelection(mSharedPreferences.getInt(SELECTED_START_STOP_POSITION, 0));
                                    mDestinationStopSpinner.setSelection(mSharedPreferences.getInt(SELECTED_DESTINATION_STOP_POSITION, 0));

                                    startingStopTextViewToBeUpdated.setText(mStartingStopSpinner.getSelectedItem().toString());
                                    destinationStopTextViewToBeUpdated.setText(mDestinationStopSpinner.getSelectedItem().toString());
                                }
                                else {
                                    if (mStartingStopSpinner.getCount() != 0) {
                                        startingStopTextViewToBeUpdated.setText(mStartingStopSpinner.getSelectedItem().toString());
                                        destinationStopTextViewToBeUpdated.setText(mDestinationStopSpinner.getSelectedItem().toString());
                                    } else {
                                        startingStopTextViewToBeUpdated.setText("None");
                                        destinationStopTextViewToBeUpdated.setText("None");
                                    }
                                }

                            }
                        });
                    }


                });

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }


    private void processRoutesData(String jsonData) throws JSONException {
        /* NOTE: CURRENTLY ONLY INCLUDES BUSES; @getJSONObject(3) */
        JSONArray routesData = new JSONObject(jsonData).getJSONArray("mode").getJSONObject(3).getJSONArray("route");
        mRoutes = new Route[routesData.length()];
        for (int i = 0; i < routesData.length(); i++) {
            JSONObject jsonRoute = routesData.getJSONObject(i);
            mRoutes[i] = new Route(jsonRoute.getString("route_id"), jsonRoute.getString("route_name"));
        }
    }


    private void processDirectionAndHeadSignData(String jsonData) throws JSONException {
        JSONObject data = new JSONObject(jsonData);
        final JSONArray directionArray = data.getJSONArray("mode").getJSONObject(0).getJSONArray("route").getJSONObject(0).getJSONArray("direction");

        // direction is either 0 or 1
        mDirections = new String[2];
        mHeadSigns = new String[2];

        //populate mDirections and mHeadSigns
        for (int i = 0; i < directionArray.length(); i++) {
            int directionID = Integer.parseInt(directionArray.getJSONObject(i).getString("direction_id"));
            String directionName = directionArray.getJSONObject(i).getString("direction_name");
            mDirections[directionID] = directionName;

            //the index of the headsign corresponds with the same direction
            mHeadSigns[directionID] = directionArray.getJSONObject(i).getJSONArray("trip").getJSONObject(0).getString("trip_headsign");
        }
    }

    private void processStopData(String jsonData, String direction) throws JSONException {
        JSONObject stopsData = new JSONObject(jsonData);
        final JSONArray directionArray = stopsData.getJSONArray("direction");

        //ensures we only look at the stops for the selected direction
        JSONObject jsonObject = directionArray.getJSONObject(0);
        if (!jsonObject.getString("direction_name").equals(direction)) {
            jsonObject = directionArray.getJSONObject(1);
        }

        JSONArray jsonStops = jsonObject.getJSONArray("stop");
        mStops = new Stop[jsonStops.length()];
        for (int i = 0; i < jsonStops.length(); i++) {
            JSONObject currentStopJSON = jsonStops.getJSONObject(i);

            //id, name, lat, long
            mStops[i] = new Stop(currentStopJSON.getString("stop_id"), currentStopJSON.getString("stop_name")
                    , currentStopJSON.getString("stop_order"));
        }
    }

    private void bindRoutes() {
        ArrayList<String> routeNames = new ArrayList<>();
        for (Route route : mRoutes) {
            routeNames.add(route.getName());
        }
        adaptBind(routeNames, mRoutesSpinner);
    }

    private void bindStops() {
        ArrayList<String> stopList = new ArrayList<>();
        for (Stop stop : mStops) {
            stopList.add(stop.getStopName());
        }
        adaptBind(stopList, mStartingStopSpinner, mDestinationStopSpinner);

    }

    private void bindDirections() {
        ArrayList<String> directions = new ArrayList<>();
        for (int i = 0; i < mDirections.length; i++) {
            //accounts for when there's only 1 direction
            if (mDirections[i] == null) {
                continue;
            }
            directions.add(mDirections[i] + " to " + mHeadSigns[i]);
        }
        adaptBind(directions, mDirectionSpinner);
    }

    // searchableSpinner extends spinner; so we don't need to overload this method
    private void adaptBind(ArrayList<String> data, Spinner spinner) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(SetStopActivity.this, android.R.layout.simple_spinner_item, data);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

    }
    private void adaptBind(ArrayList<String> data, Spinner spinner1, Spinner spinner2) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(SetStopActivity.this, android.R.layout.simple_spinner_item, data);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner1.setAdapter(adapter);
        spinner2.setAdapter(adapter);
    }

    public void finish(View v) {
        finish();
    }




    /* debug methods */
    private void debugToast(final String displayText) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(SetStopActivity.this, displayText, Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void onFailureToastMessage() {
        debugToast("Network failure, try again");
    }

    private void resetSpinners() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mRoutesSpinner.setSelection(0);
                mDirectionSpinner.setAdapter(null);
                mStartingStopSpinner.setAdapter(null);
                mDestinationStopSpinner.setAdapter(null);
                directionAndHeadSignCall(client);
            }
        });
    }




    // methods to be moved

















}



