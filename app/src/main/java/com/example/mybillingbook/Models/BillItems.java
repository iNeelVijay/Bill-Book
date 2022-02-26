package com.example.mybillingbook.Models;

public class BillItems {


    private String uid="";
    private String itemid="";
    private String qty="";
    private String unitPrice="";
    private String taxPercent="";
    private String unit="";

    public BillItems() {
    }


    public BillItems(String uid, String itemid, String qty, String unitPrice, String taxPercent, String unit) {
        this.uid = uid;
        this.itemid = itemid;
        this.qty = qty;
        this.unitPrice = unitPrice;
        this.taxPercent = taxPercent;
        this.unit = unit;
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

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }

    public String getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(String unitPrice) {
        this.unitPrice = unitPrice;
    }

    public String getTaxPercent() {
        return taxPercent;
    }

    public void setTaxPercent(String taxPercent) {
        this.taxPercent = taxPercent;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}
