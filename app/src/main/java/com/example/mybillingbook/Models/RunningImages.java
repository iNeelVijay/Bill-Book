package com.example.mybillingbook.Models;

public class RunningImages {

    private String imageone="";
    private String imagetwo="";
    private String imagethree="";
    private String imagefour="";

    public RunningImages() {
    }

    public RunningImages(String imageone, String imagetwo, String imagethree, String imagefour) {
        this.imageone = imageone;
        this.imagetwo = imagetwo;
        this.imagethree = imagethree;
        this.imagefour = imagefour;
    }

    public String getImageone() {
        return imageone;
    }

    public void setImageone(String imageone) {
        this.imageone = imageone;
    }

    public String getImagetwo() {
        return imagetwo;
    }

    public void setImagetwo(String imagetwo) {
        this.imagetwo = imagetwo;
    }

    public String getImagethree() {
        return imagethree;
    }

    public void setImagethree(String imagethree) {
        this.imagethree = imagethree;
    }

    public String getImagefour() {
        return imagefour;
    }

    public void setImagefour(String imagefour) {
        this.imagefour = imagefour;
    }
}
