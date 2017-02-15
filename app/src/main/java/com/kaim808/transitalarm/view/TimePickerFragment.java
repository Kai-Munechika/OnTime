package com.kaim808.transitalarm.view;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.kaim808.transitalarm.R;
import com.kaim808.transitalarm.activities.SetStopActivity;
import com.kaim808.transitalarm.activities.TimeScheduleActivity;

import java.util.Calendar;


public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

    private int dayOfWeekAsInt;
    private TextView mCurrentTextView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        dayOfWeekAsInt = getArguments().getInt(SetStopActivity.DAY);
        switch (dayOfWeekAsInt) {
            case 0:
                mCurrentTextView = (TextView) getActivity().findViewById(R.id.SundayTimeTextView);
                break;
            case 1:
                mCurrentTextView = (TextView) getActivity().findViewById(R.id.MondayTimeTextView);
                break;
            case 2:
                mCurrentTextView = (TextView) getActivity().findViewById(R.id.TuesdayTimeTextView);
                break;
            case 3:
                mCurrentTextView = (TextView) getActivity().findViewById(R.id.WednesdayTimeTextView);
                break;
            case 4:
                mCurrentTextView = (TextView) getActivity().findViewById(R.id.ThursdayTimeTextView);
                break;
            case 5:
                mCurrentTextView = (TextView) getActivity().findViewById(R.id.FridayTimeTextView);
                break;
            case 6:
                mCurrentTextView = (TextView) getActivity().findViewById(R.id.SaturdayTimeTextView);
                break;
            default:
                Toast.makeText(getActivity(), "error: from TimePickerFragment", Toast.LENGTH_SHORT).show();
                mCurrentTextView = null;
        }

        super.onCreate(savedInstanceState);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {


        //Use the current time as the default values for the time picker
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);


        TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(),this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));

        timePickerDialog.setButton(DialogInterface.BUTTON_NEUTRAL, "NONE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mCurrentTextView.setText("None");
            }
        });

        //Create and return a new instance of TimePickerDialog
        return timePickerDialog;
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

        if (mCurrentTextView != null) {
            //use a 2d array
            TimeScheduleActivity.mTime[dayOfWeekAsInt][0] = hourOfDay;
            TimeScheduleActivity.mTime[dayOfWeekAsInt][1] = minute;

            String minuteString = (minute < 10) ? "0" + minute : minute + "";
            String hourString = (hourOfDay > 12) ? "" + (hourOfDay - 12) : hourOfDay + ""; if (hourOfDay == 0) hourString = "12";

            Log.e("kaikai", hourOfDay+"");
            String period = (hourOfDay >= 12) ? "PM" : "AM";

            mCurrentTextView.setText(hourString + ":" + minuteString + " " + period);
        }

    }
}
