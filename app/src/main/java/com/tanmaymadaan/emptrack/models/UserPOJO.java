package com.tanmaymadaan.emptrack.models;

public class UserPOJO {
    String name;

    public UserPOJO(String name, String companyCode, String userCode, String passCode) {
        this.name = name;
        this.companyCode = companyCode;
        this.userCode = userCode;
        this.passCode = passCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    String companyCode;
    String userCode;
    String passCode;


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
