package com.kaim808.transitalarm.model;

/**
 * Created by KaiM on 12/3/16.
 */

public class Stop {

    private String mStopId;
    private String mStopName;




    private String mStopOrder;

    public Stop(String stopId, String stopName, String stopOrder) {
        mStopId = stopId;
        mStopName = stopName;
        mStopOrder = stopOrder;
    }

    /* getters and setters */

    public String getStopId() {
        return mStopId;
    }

    public void setStopId(String stopId) {
        mStopId = stopId;
    }

    public String getStopName() {
        return mStopName;
    }

    public void setStopName(String stopName) {
        mStopName = stopName;
    }


    public String getStopOrder() {
        return mStopOrder;
    }

    public void setStopOrder(String stopOrder) {
        mStopOrder = stopOrder;
    }
}
