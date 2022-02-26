package com.example.mybillingbook.Models;

public class Parties {
    private String uid="";
    private String name="";
    private String contact="";
    private String gst="";
    private String email="";
    private String adress="";
    private String search="";

    public Parties() {
    }

    public Parties(String uid, String name, String contact, String gst, String email, String adress, String search) {
        this.uid = uid;
        this.name = name;
        this.contact = contact;
        this.gst = gst;
        this.email = email;
        this.adress = adress;
        this.search = search;
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

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getGst() {
        return gst;
    }

    public void setGst(String gst) {
        this.gst = gst;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAdress() {
        return adress;
    }

    public void setAdress(String adress) {
        this.adress = adress;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }
}
