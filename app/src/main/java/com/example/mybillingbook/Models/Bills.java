package com.example.mybillingbook.Models;

public class Bills {
    private String billid="";
    private String billto="";
    private String date="";
    private String time="";
    private String billFrom="";


    public Bills() {
    }

    public Bills(String billid, String billto, String date, String time, String billFrom) {
        this.billid = billid;
        this.billto = billto;
        this.date = date;
        this.time = time;
        this.billFrom = billFrom;
    }

    public String getBillid() {
        return billid;
    }

    public void setBillid(String billid) {
        this.billid = billid;
    }

    public String getBillto() {
        return billto;
    }

    public void setBillto(String billto) {
        this.billto = billto;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getBillFrom() {
        return billFrom;
    }

    public void setBillFrom(String billFrom) {
        this.billFrom = billFrom;
    }
}
