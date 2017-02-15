package com.kaim808.transitalarm.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.kaim808.transitalarm.R;

public class MenuLayoutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_layout);

    }

    public void setBusStop(View v) {
        Intent intent = new Intent(this, SetStopActivity.class);
        startActivity(intent);
    }

    public void setTimeSchedule(View v) {
        Intent intent = new Intent(this, TimeScheduleActivity.class);
        startActivity(intent);
    }

    public void finish(View v) {
        finish();
    }

}
