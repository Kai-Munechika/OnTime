package com.kaim808.transitalarm.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AlertHeader {

    @SerializedName("alert_id")
    @Expose
    private Integer alertId;
    @SerializedName("header_text")
    @Expose
    private String headerText;
    @SerializedName("effect_name")
    @Expose
    private String effectName;

    public Integer getAlertId() {
        return alertId;
    }

    public void setAlertId(Integer alertId) {
        this.alertId = alertId;
    }

    public String getHeaderText() {
        return headerText;
    }

    public void setHeaderText(String headerText) {
        this.headerText = headerText;
    }

    public String getEffectName() {
        return effectName;
    }

    public void setEffectName(String effectName) {
        this.effectName = effectName;
    }

}
