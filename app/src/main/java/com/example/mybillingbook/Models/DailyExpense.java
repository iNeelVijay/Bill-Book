package com.example.mybillingbook.Models;

public class DailyExpense {

    private String date="";
    private String inc="";
    private String out="";

    public DailyExpense() {
    }

    public DailyExpense(String date, String inc, String out) {
        this.date = date;
        this.inc = inc;
        this.out = out;
    }


    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getInc() {
        return inc;
    }

    public void setInc(String inc) {
        this.inc = inc;
    }

    public String getOut() {
        return out;
    }

    public void setOut(String out) {
        this.out = out;
    }
}
