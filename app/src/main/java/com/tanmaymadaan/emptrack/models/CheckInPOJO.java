package com.tanmaymadaan.emptrack.models;

public class CheckInPOJO {
    private String userId;
    private String date;
    private Double lat;
    private Double lng;
    private String checkinTime;
    private String checkoutTime;
    private String company;
    private String purpose;
    private String remarks;

    public CheckInPOJO(String userId, String date, Double lat, Double lng, String checkInTime, String checkOutTime, String company, String purpose, String remarks) {
        this.userId = userId;
        this.date = date;
        this.lat = lat;
        this.lng = lng;
        this.checkinTime = checkInTime;
        this.checkoutTime = checkOutTime;
        this.company = company;
        this.purpose = purpose;
        this.remarks = remarks;
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

    public String getCheckInTime() {
        return checkinTime;
    }

    public void setCheckInTime(String checkInTime) {
        this.checkinTime = checkInTime;
    }

    public String getCheckOutTime() {
        return checkoutTime;
    }

    public void setCheckOutTime(String checkOutTime) {
        this.checkoutTime = checkOutTime;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
