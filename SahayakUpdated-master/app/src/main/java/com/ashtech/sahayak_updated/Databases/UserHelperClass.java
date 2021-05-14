package com.ashtech.sahayak_updated.Databases;

public class UserHelperClass {



    String fullname,username, password, email, phoneNum,lat,log;
    Boolean isActive;
    public UserHelperClass(){}




    public UserHelperClass(String fullname, String username, String password, String email, String phoneNum, String lat, String log, Boolean isActive) {
        this.fullname = fullname;
        this.username = username;
        this.password = password;
        this.email = email;
        this.phoneNum = phoneNum;
        this.lat = lat;
        this.log = log;
        this.isActive=isActive;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLog() {
        return log;
    }

    public void setLog(String log) {
        this.log = log;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }
}
