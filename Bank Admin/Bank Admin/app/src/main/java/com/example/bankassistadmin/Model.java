package com.example.bankassistadmin;

public class Model {
    public String name;
    public String email;
    public String mno;
    private String imageurl;
    private String dealWith;  // New field to store the selected category

    public Model() {
    }

    public Model(String name, String email, String mno, String imageurl, String dealWith) {
        this.name = name;
        this.email = email;
        this.mno = mno;
        this.imageurl = imageurl;
        this.dealWith = dealWith;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMno() {
        return mno;
    }

    public void setMno(String mno) {
        this.mno = mno;
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public String getDealWith() {
        return dealWith;
    }

    public void setDealWith(String dealWith) {
        this.dealWith = dealWith;
    }
}
