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

//import com.google.gson.annotations.Expose;
//import com.google.gson.annotations.SerializedName;
//
//public class Stop {
//
//    @SerializedName("stop_sequence")
//    @Expose
//    private String stopSequence;
//    @SerializedName("stop_id")
//    @Expose
//    private String stopId;
//    @SerializedName("stop_name")
//    @Expose
//    private String stopName;
//    @SerializedName("sch_arr_dt")
//    @Expose
//    private String schArrDt;
//    @SerializedName("sch_dep_dt")
//    @Expose
//    private String schDepDt;
//    @SerializedName("pre_dt")
//    @Expose
//    private String preDt;
//    @SerializedName("pre_away")
//    @Expose
//    private String preAway;
//
//    public String getStopSequence() {
//        return stopSequence;
//    }
//
//    public void setStopSequence(String stopSequence) {
//        this.stopSequence = stopSequence;
//    }
//
//    public String getStopId() {
//        return stopId;
//    }
//
//    public void setStopId(String stopId) {
//        this.stopId = stopId;
//    }
//
//    public String getStopName() {
//        return stopName;
//    }
//
//    public void setStopName(String stopName) {
//        this.stopName = stopName;
//    }
//
//    public String getSchArrDt() {
//        return schArrDt;
//    }
//
//    public void setSchArrDt(String schArrDt) {
//        this.schArrDt = schArrDt;
//    }
//
//    public String getSchDepDt() {
//        return schDepDt;
//    }
//
//    public void setSchDepDt(String schDepDt) {
//        this.schDepDt = schDepDt;
//    }
//
//    public String getPreDt() {
//        return preDt;
//    }
//
//    public void setPreDt(String preDt) {
//        this.preDt = preDt;
//    }
//
//    public String getPreAway() {
//        return preAway;
//    }
//
//    public void setPreAway(String preAway) {
//        this.preAway = preAway;
//    }

//}
