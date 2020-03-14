package com.tanmaymadaan.emptrack.models;

public class CheckInPOJO {
    private String userId;
    private String date;
    private Double lat;
    private Double lng;
    private Integer timestamp;
    private String company;

    public CheckInPOJO(String userId, String date, Double lat, Double lng, Integer timestamp, String company) {
        this.userId = userId;
        this.date = date;
        this.lat = lat;
        this.lng = lng;
        this.timestamp = timestamp;
        this.company = company;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }
}
