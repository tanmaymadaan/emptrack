package com.tanmaymadaan.emptrack.models;

import java.io.Serializable;

public class UserPOJO implements Serializable {
    String name;
    String uid;
    String companyCode;
    String userCode;
    String passCode;

    public UserPOJO(String name, String uid, String companyCode, String userCode, String passCode) {
        this.name = name;
        this.uid = uid;
        this.companyCode = companyCode;
        this.userCode = userCode;
        this.passCode = passCode;
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

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public String getPassCode() {
        return passCode;
    }

    public void setPassCode(String passCode) {
        this.passCode = passCode;
    }
}
