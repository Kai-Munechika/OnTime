package com.kaim808.transitalarm.model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.kaim808.transitalarm.R;
import com.kaim808.transitalarm.view.MenuItem;

import java.util.ArrayList;

/**
 * Created by KaiM on 3/25/17.
 */

public class MenuItemAdapter extends ArrayAdapter<MenuItem> {


    public MenuItemAdapter(Context context, ArrayList<MenuItem> menuItems) {
        super(context, 0, menuItems);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        MenuItem menuItem = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.menu_item_layout, parent, false);
        }
        // Lookup view for data population
        TextView description = (TextView) convertView.findViewById(R.id.menu_item_text);
        ImageView image = (ImageView) convertView.findViewById(R.id.menu_item_image);

        // Populate the data into the template view using the data object
        description.setText(menuItem.getDescription());
        image.setImageResource(menuItem.getImageId());
        // Return the completed view to render on screen
        return convertView;
    }

}
