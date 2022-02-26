package com.example.mybillingbook.Models;

public class Items {
    private String uid="";
    private String itemid="";
    private String price="";
    private String unit="";
    private String name="";
    private String tax="";

    public Items() {
    }

    public Items(String uid, String itemid, String price, String unit, String name, String tax) {
        this.uid = uid;
        this.itemid = itemid;
        this.price = price;
        this.unit = unit;
        this.name = name;
        this.tax = tax;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getItemid() {
        return itemid;
    }

    public void setItemid(String itemid) {
        this.itemid = itemid;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTax() {
        return tax;
    }

    public void setTax(String tax) {
        this.tax = tax;
    }
}
