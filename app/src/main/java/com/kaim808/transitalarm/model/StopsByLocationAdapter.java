package com.kaim808.transitalarm.model;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.kaim808.transitalarm.R;

/**
 * Created by KaiM on 12/9/16.
 */

public class StopsByLocationAdapter extends BaseAdapter {

    private Stop[] mStops;
    private Context mContext;

    public StopsByLocationAdapter(Stop[] stops, Context context) {
        mStops = stops;
        mContext = context;
    }


    @Override
    public int getCount() {
        return mStops.length;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.initial_stops_list_item, null);
            holder = new ViewHolder();

            holder.stopName = (TextView) convertView.findViewById(R.id.name);
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.stopName.setText(mStops[i].getStopName());
        return convertView;

    }

    private static class ViewHolder {
        TextView stopName;
    }

//    @Override
//    public stopViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.initial_stops_list_item, parent, false);
//        stopViewHolder holder = new stopViewHolder(view);
//        return holder;
//    }
//
//    @Override
//    public void onBindViewHolder(stopViewHolder holder, int position) {
//        holder.bindView(mStops[position]);
//    }
//
//    @Override
//    public int getItemCount() {
//        return mStops.length;
//    }
//
//    class stopViewHolder extends RecyclerView.ViewHolder {
//        private TextView stopName;
//
//        public stopViewHolder(View itemView) {
//            super(itemView);
//            stopName = (TextView) itemView.findViewById(R.id.name);
//        }
//
//        private void bindView(Stop stop) {
//            stopName.setText(stop.getStopName());
//        }
//
//    }

}
