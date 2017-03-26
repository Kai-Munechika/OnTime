package com.kaim808.transitalarm.model;

/**
 * Created by KaiM on 2/23/17.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Trip {

    @SerializedName("trip_id")
    @Expose
    private String tripId;
    @SerializedName("trip_name")
    @Expose
    private String tripName;
    @SerializedName("trip_headsign")
    @Expose
    private String tripHeadsign;
    @SerializedName("vehicle")
    @Expose
    private Vehicle vehicle;
    @SerializedName("stop")
    @Expose
    private List<Stop> stop = null;

    public String getTripId() {
        return tripId;
    }

    public void setTripId(String tripId) {
        this.tripId = tripId;
    }

    public String getTripName() {
        return tripName;
    }

    public void setTripName(String tripName) {
        this.tripName = tripName;
    }

    public String getTripHeadsign() {
        return tripHeadsign;
    }

    public void setTripHeadsign(String tripHeadsign) {
        this.tripHeadsign = tripHeadsign;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    public List<com.kaim808.transitalarm.model.Stop> getStop() {
        return stop;
    }

    public void setStop(List<com.kaim808.transitalarm.model.Stop> stop) {
        this.stop = stop;
    }

}
