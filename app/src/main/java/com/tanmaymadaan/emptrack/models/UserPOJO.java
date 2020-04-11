package com.tanmaymadaan.emptrack.models;

import java.io.Serializable;

public class UserPOJO implements Serializable {
    String name;
    String uid;
    String companyCode;
    String status;
    String checkInStatus;
    String currCheckIn;
    String role;

    public UserPOJO(String name, String uid, String companyCode, String status, String checkInStatus, String currCheckIn, String role, String swipeStatus) {
        this.name = name;
        this.uid = uid;
        this.companyCode = companyCode;
        this.status = status;
        this.checkInStatus = checkInStatus;
        this.currCheckIn = currCheckIn;
        this.role = role;
        this.swipeStatus = swipeStatus;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getSwipeStatus() {
        return swipeStatus;
    }

    public void setSwipeStatus(String swipeStatus) {
        this.swipeStatus = swipeStatus;
    }

    String swipeStatus;

    public String getCurrCheckIn() {
        return currCheckIn;
    }

    public void setCurrCheckIn(String currCheckIn) {
        this.currCheckIn = currCheckIn;
    }



    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCheckInStatus() {
        return checkInStatus;
    }

    public void setCheckInStatus(String checkInStatus) {
        this.checkInStatus = checkInStatus;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCompanyCode() {
        return companyCode;
    }

    public void setCompanyCode(String companyCode) {
        this.companyCode = companyCode;
    }

}
