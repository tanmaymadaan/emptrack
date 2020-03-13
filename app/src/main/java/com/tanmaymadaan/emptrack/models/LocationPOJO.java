package com.tanmaymadaan.emptrack.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LocationPOJO {

    @SerializedName("userId")
    @Expose
    private String userID;

    @SerializedName("date")
    @Expose
    private String date;

    @SerializedName("lat")
    @Expose
    private Double lat;

    @SerializedName("lng")
    @Expose
    private Double lng;

    public LocationPOJO(String userID, String date, Double lat, Double lng, Integer timestamp) {
        this.userID = userID;
        this.date = date;
        this.lat = lat;
        this.lng = lng;
        this.timestamp = timestamp;
    }

    @SerializedName("timestamp")
    @Expose
    private Integer timestamp;

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

    public Integer getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Integer timestamp) {
        this.timestamp = timestamp;
    }
}
