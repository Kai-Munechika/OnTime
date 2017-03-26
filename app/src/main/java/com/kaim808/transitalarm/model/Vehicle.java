package com.kaim808.transitalarm.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Vehicle {

    @SerializedName("vehicle_id")
    @Expose
    private String vehicleId;
    @SerializedName("vehicle_lat")
    @Expose
    private String vehicleLat;
    @SerializedName("vehicle_lon")
    @Expose
    private String vehicleLon;
    @SerializedName("vehicle_bearing")
    @Expose
    private String vehicleBearing;
    @SerializedName("vehicle_speed")
    @Expose
    private String vehicleSpeed;
    @SerializedName("vehicle_timestamp")
    @Expose
    private String vehicleTimestamp;
    @SerializedName("vehicle_label")
    @Expose
    private String vehicleLabel;

    public String getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
    }

    public String getVehicleLat() {
        return vehicleLat;
    }

    public void setVehicleLat(String vehicleLat) {
        this.vehicleLat = vehicleLat;
    }

    public String getVehicleLon() {
        return vehicleLon;
    }

    public void setVehicleLon(String vehicleLon) {
        this.vehicleLon = vehicleLon;
    }

    public String getVehicleBearing() {
        return vehicleBearing;
    }

    public void setVehicleBearing(String vehicleBearing) {
        this.vehicleBearing = vehicleBearing;
    }

    public String getVehicleSpeed() {
        return vehicleSpeed;
    }

    public void setVehicleSpeed(String vehicleSpeed) {
        this.vehicleSpeed = vehicleSpeed;
    }

    public String getVehicleTimestamp() {
        return vehicleTimestamp;
    }

    public void setVehicleTimestamp(String vehicleTimestamp) {
        this.vehicleTimestamp = vehicleTimestamp;
    }

    public String getVehicleLabel() {
        return vehicleLabel;
    }

    public void setVehicleLabel(String vehicleLabel) {
        this.vehicleLabel = vehicleLabel;
    }

}
