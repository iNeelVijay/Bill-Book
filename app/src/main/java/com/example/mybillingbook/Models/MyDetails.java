package com.example.mybillingbook.Models;

public class MyDetails {
    String image="";
    String name="";
    String contact="";
    String email="";
    String gst="";
    String adress="";
    String city="";
    String pin="";
    String state="";

    public MyDetails() {
    }

    public MyDetails(String image, String name, String contact, String email, String gst, String adress, String city, String pin, String state) {
        this.image = image;
        this.name = name;
        this.contact = contact;
        this.email = email;
        this.gst = gst;
        this.adress = adress;
        this.city = city;
        this.pin = pin;
        this.state = state;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGst() {
        return gst;
    }

    public void setGst(String gst) {
        this.gst = gst;
    }

    public String getAdress() {
        return adress;
    }

    public void setAdress(String adress) {
        this.adress = adress;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
