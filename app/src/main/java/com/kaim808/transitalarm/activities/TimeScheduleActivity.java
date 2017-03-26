package com.kaim808.transitalarm.activities;

import android.app.AlarmManager;
import android.app.DialogFragment;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.kaim808.transitalarm.R;
import com.kaim808.transitalarm.receivers_and_services.MyAlarmReceiver;
import com.kaim808.transitalarm.view.TimePickerFragment;

import java.util.Calendar;

import static com.kaim808.transitalarm.activities.QandA.setupNavigationDrawer;
import static com.kaim808.transitalarm.activities.SetStopActivity.DESTINATION_STOP_ORDER;
import static com.kaim808.transitalarm.activities.SetStopActivity.DIRECTION_ID;
import static com.kaim808.transitalarm.activities.SetStopActivity.ROUTE_ID;
import static com.kaim808.transitalarm.activities.SetStopActivity.START_STOP_ORDER;


//// TODO: 1/25/17 figure out how to add option to set None from timePickerFragment or whatever 

public class TimeScheduleActivity extends AppCompatActivity {


    private int mCurrentActivityId = 2;

    // int array used to store hour(index 0) and minute(index 1) selected by the time picker fragment
    public static int[][] mTime = new int[7][2];

    // keys to retrieve mTime values
    public static String TIME_00 = "TIME_00";

    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;

    private TextView mMondayTimeTextView;
    private TextView mTuesdayTimeTextView;
    private TextView mWednesdayTimeTextView;
    private TextView mThursdayTimeTextView;
    private TextView mFridayTimeTextView;
    private TextView mSaturdayTimeTextView;
    private TextView mSundayTimeTextView;

    private TextView[] timeTextViews;

    private ImageButton mMenuButton;
    private ListView mDrawerList;
    private DrawerLayout mDrawerLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_schedule);

        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mMenuButton = (ImageButton) findViewById(R.id.menu_button);

        setupNavigationDrawer(mMenuButton, mDrawerLayout, mDrawerList, this, mCurrentActivityId);

        mSharedPreferences = getSharedPreferences(SetStopActivity.PREFS_FILE, MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();


        mMondayTimeTextView = (TextView) findViewById(R.id.MondayTimeTextView);
        mTuesdayTimeTextView = (TextView) findViewById(R.id.TuesdayTimeTextView);
        mWednesdayTimeTextView = (TextView) findViewById(R.id.WednesdayTimeTextView);
        mThursdayTimeTextView = (TextView) findViewById(R.id.ThursdayTimeTextView);
        mFridayTimeTextView  = (TextView) findViewById(R.id.FridayTimeTextView);
        mSaturdayTimeTextView = (TextView) findViewById(R.id.SaturdayTimeTextView);
        mSundayTimeTextView = (TextView) findViewById(R.id.SundayTimeTextView);

        timeTextViews = new TextView[] {mSundayTimeTextView, mMondayTimeTextView, mTuesdayTimeTextView,
                mWednesdayTimeTextView, mThursdayTimeTextView, mFridayTimeTextView, mSaturdayTimeTextView};

        // this populates mTime
        if (mSharedPreferences.contains(TIME_00)) {
            for (int i = 0; i < 7; i++) {
                for (int j = 0; j < 2; j++) {
                    mTime[i][j] = mSharedPreferences.getInt("TIME_"+i+j, -1);
                }

                //repopulates the text views based on mTime
                if (mSharedPreferences.getInt("None_" +i, -1) == 1) {
                    timeTextViews[i].setText("None");
                }
                else {
                    String hourString = (mTime[i][0] > 12) ? "" + (mTime[i][0] - 12) : mTime[i][0] + "";
                    if (mTime[i][0] == 0) hourString = "12";
                    String minuteString = (mTime[i][1] < 10) ? "0" + mTime[i][1] : mTime[i][1] + "";

                    String period = (mTime[i][0] >= 12) ? "PM" : "AM";
                    timeTextViews[i].setText(hourString + ":" + minuteString + " " + period);
                }
            }


        }

    }

    public void saveSchedule(View v) {
        // gotta save the values for mTime and repopulate onCreate
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 2; j++) {
                mEditor.putInt("TIME_"+i+j, mTime[i][j]);
                mEditor.putInt("None_"+i, timeTextViews[i].getText().toString().equals("None") ? 1 : 0);
            }
        }
        mEditor.apply();

        startNotifications(v);

        startActivity(new Intent(this, TimerActivity.class));


    }

    public void startNotifications(View v) {

        // if no bus route/trip has been saved, redirects user to select one first
        if (!mSharedPreferences.contains(SetStopActivity.ROUTE_ID)) {
            Intent intent = new Intent(this, SetStopActivity.class);
            Toast.makeText(this, "Please save a stop", Toast.LENGTH_SHORT).show();
            startActivity(intent);
            finish();
        }

        String routeID = mSharedPreferences.getString(SetStopActivity.ROUTE_ID, null);
        String direction = mSharedPreferences.getString(SetStopActivity.ROUTE_DIRECTION, null);
        Integer directionNum = mSharedPreferences.getInt(SetStopActivity.DIRECTION_NUM, -1);

        // covers case when route goes in direction 1, and that's the only possible direction;
        // case when only direction 0 is covered since that'd be the only choice
        if (direction == null) {
//          direction = mDirections[1];
            directionNum = 1;
        }

        String startStopOrder = mSharedPreferences.getString(SetStopActivity.START_STOP_ORDER, null);
        String endStopOrder = mSharedPreferences.getString(SetStopActivity.DESTINATION_STOP_ORDER, null);


        scheduleAlarm(routeID, String.valueOf(directionNum), startStopOrder, endStopOrder);

    }

    // this doesn't belong here
    public void scheduleAlarm(String routeID, String directionID, String beginStopOrder, String destinationStopOrder) {

        Calendar sundayCalendar    = getCalendarDayObject(0, Calendar.SUNDAY);
        Calendar mondayCalendar    = getCalendarDayObject(1, Calendar.MONDAY);
        Calendar tuesdayCalendar   = getCalendarDayObject(2, Calendar.TUESDAY);
        Calendar wednesdayCalendar = getCalendarDayObject(3, Calendar.WEDNESDAY);
        Calendar thursdayCalendar  = getCalendarDayObject(4, Calendar.THURSDAY);
        Calendar fridayCalendar    = getCalendarDayObject(5, Calendar.FRIDAY);
        Calendar saturdayCalendar  = getCalendarDayObject(6, Calendar.SATURDAY);

        //AlarmManager[] alarmManagers = new AlarmManager[] {alarmMgrSunday, alarmMgrMonday, alarmMgrTuesday, alarmMgrWednesday, alarmMgrThursday, alarmMgrFriday, alarmMgrSaturday};
        Calendar[] calendars = new Calendar[]{sundayCalendar, mondayCalendar, tuesdayCalendar,
                wednesdayCalendar, thursdayCalendar, fridayCalendar, saturdayCalendar};

        long currentTimeAsMs = System.currentTimeMillis();

        for (Calendar calendar : calendars) {
            long calendarTimeAsMs = calendar.getTimeInMillis();
            if (calendarTimeAsMs < currentTimeAsMs) {
                calendar.setTimeInMillis(calendar.getTimeInMillis() + (AlarmManager.INTERVAL_DAY * 7) );
            }
        }

        /* intent that will execute the AlarmReceiver */
        // PUT ALL INFO NEEDED FOR THE predictionsByRoute/stop API CALL IN HERE AS EXTRAS
        Intent intent = new Intent(getApplicationContext(), MyAlarmReceiver.class);
        intent.putExtra(ROUTE_ID, routeID);
        intent.putExtra(DIRECTION_ID, directionID);
        intent.putExtra(START_STOP_ORDER, Integer.parseInt(beginStopOrder));
        intent.putExtra(DESTINATION_STOP_ORDER, Integer.parseInt(destinationStopOrder));


        final PendingIntent pendingIntentSunday = generateDayPendingIntent(intent, MyAlarmReceiver.REQUEST_CODE_SUNDAY);
        final PendingIntent pendingIntentMonday = generateDayPendingIntent(intent, MyAlarmReceiver.REQUEST_CODE_MONDAY);
        final PendingIntent pendingIntentTuesday = generateDayPendingIntent(intent, MyAlarmReceiver.REQUEST_CODE_TUESDAY);
        final PendingIntent pendingIntentWednesday = generateDayPendingIntent(intent, MyAlarmReceiver.REQUEST_CODE_WEDNESDAY);
        final PendingIntent pendingIntentThursday = generateDayPendingIntent(intent, MyAlarmReceiver.REQUEST_CODE_THURSDAY);
        final PendingIntent pendingIntentFriday = generateDayPendingIntent(intent, MyAlarmReceiver.REQUEST_CODE_FRIDAY);
        final PendingIntent pendingIntentSaturday = generateDayPendingIntent(intent, MyAlarmReceiver.REQUEST_CODE_SATURDAY);

        final AlarmManager alarmMgr = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);


        alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, sundayCalendar   .getTimeInMillis(), AlarmManager.INTERVAL_DAY * 7, pendingIntentSunday);
        alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, mondayCalendar   .getTimeInMillis(), AlarmManager.INTERVAL_DAY * 7, pendingIntentMonday);
        alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, tuesdayCalendar  .getTimeInMillis(), AlarmManager.INTERVAL_DAY * 7, pendingIntentTuesday);
        alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, wednesdayCalendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY * 7, pendingIntentWednesday);
        alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, thursdayCalendar .getTimeInMillis(), AlarmManager.INTERVAL_DAY * 7, pendingIntentThursday);
        alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, fridayCalendar   .getTimeInMillis(), AlarmManager.INTERVAL_DAY * 7, pendingIntentFriday);
        alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, saturdayCalendar .getTimeInMillis(), AlarmManager.INTERVAL_DAY * 7, pendingIntentSaturday);




        PendingIntent[] pendingIntents = {pendingIntentSunday, pendingIntentMonday, pendingIntentTuesday,
                pendingIntentWednesday, pendingIntentThursday, pendingIntentFriday, pendingIntentSaturday};



                                                            // change sundayCalendar to current day of week calendar
//        debugToast("Seconds between now and next alarm: " + (sundayCalendar.getTimeInMillis() - System.currentTimeMillis())/1000 );
        Toast.makeText(this, "Times Successfully Saved", Toast.LENGTH_SHORT).show();
        for (int i = 0; i < timeTextViews.length; i++) {
            if (timeTextViews[i].getText().toString().equals("None")) {
                alarmMgr.cancel(pendingIntents[i]);
            }
        }



    }

    private Calendar getCalendarDayObject(int dayOfWeekAsInt, int calendarDayOfWeek) {
        /* set calendar object to current time in ms */
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.DAY_OF_WEEK, calendarDayOfWeek);
        calendar.set(Calendar.HOUR_OF_DAY, mTime[dayOfWeekAsInt][0]);
        calendar.set(Calendar.MINUTE, mTime[dayOfWeekAsInt][1]);
        return calendar;
    }

    public void finish(View v){
        finish();

    }
    public void pickTime(View v) {
        DialogFragment newFragment = new TimePickerFragment();
        Bundle bundle = new Bundle();
        int dayOfWeekAsInt;
        String day = getResources().getResourceEntryName(v.getId());


        switch (day) {
            case "SundayTimeTextView":
                dayOfWeekAsInt = 0;
                break;
            case "MondayTimeTextView":
                dayOfWeekAsInt = 1;
                break;
            case "TuesdayTimeTextView":
                dayOfWeekAsInt = 2;
                break;
            case "WednesdayTimeTextView":
                dayOfWeekAsInt = 3;
                break;
            case "ThursdayTimeTextView":
                dayOfWeekAsInt = 4;
                break;
            case "FridayTimeTextView":
                dayOfWeekAsInt = 5;
                break;
            case "SaturdayTimeTextView":
                dayOfWeekAsInt = 6;
                break;
            default:
                Toast.makeText(this, "pickTime Error, go to TimeScheduleActivity.java: " +
                        "resourceEntryName " + day, Toast.LENGTH_SHORT).show();
                dayOfWeekAsInt = -1;
        }
        bundle.putInt(SetStopActivity.DAY, dayOfWeekAsInt);
        newFragment.setArguments(bundle);
        newFragment.show(getFragmentManager(), "TimePicker");
    }

    private PendingIntent generateDayPendingIntent(Intent intent, int RequestCode) {
        return PendingIntent.getBroadcast(this, RequestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private void debugToast(final String displayText) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(TimeScheduleActivity.this, displayText, Toast.LENGTH_SHORT).show();
            }
        });
    }

}
