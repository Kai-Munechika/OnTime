package com.kaim808.transitalarm.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;

import com.kaim808.transitalarm.R;
import com.kaim808.transitalarm.model.MenuItemAdapter;

import static com.kaim808.transitalarm.activities.TimerActivity.mMenuItems;
import static com.kaim808.transitalarm.activities.TimerActivity.openMenu;

public class QandA extends AppCompatActivity {

    private int mCurrentActivityId = 3;

    private ImageButton mMenuButton;
    private ListView mDrawerList;
    private DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qand);

        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mMenuButton = (ImageButton) findViewById(R.id.menu_button);

        setupNavigationDrawer(mMenuButton, mDrawerLayout, mDrawerList, this, mCurrentActivityId);

    }

    public static void setupNavigationDrawer(ImageButton sandwichButton, final DrawerLayout drawerLayout, final ListView drawerList, final Activity activity, final int currentActivityId) {
        sandwichButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openMenu(drawerLayout, drawerList);
            }
        });

        // connect Navigation drawer to string array
        drawerList.setAdapter(new MenuItemAdapter(activity, mMenuItems));

        // do something when something is chosen
        drawerList.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                drawerLayout.closeDrawer(drawerList, true);
                if (drawerList.getCheckedItemPosition() != currentActivityId) {
                    activity.startActivity(generateActivityIntent(activity, i));
                }
            }
        });

        drawerList.setItemChecked(currentActivityId, true);
    }

    public static Intent generateActivityIntent(Activity activity, int position) {
        // 0 -> set stops, 1 -> set times, 2 -> faq
        Intent intent;
        if (position == 0) {
            intent = new Intent(activity, TimerActivity.class);
        }
        else if (position == 1) {
            intent = new Intent(activity, SetStopActivity.class);
        }
        else if (position == 2) {
            intent = new Intent(activity, TimeScheduleActivity.class);
        }
        else {
            intent = new Intent(activity, QandA.class);
        }
        return intent;
    }






}
