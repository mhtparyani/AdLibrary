/*
 * Created by Darshan Pandya.
 * @itznotabug
 * Copyright (c) 2018.
 */

package com.e.adlibrary.modal;

public class DialogModal {
    private String appTitle;
    private String appDesc;
    private String rating;
    private String downloads;
    private String imgUrl;
    private String width;
    private String height;
    private String redirectUrl;

    public void setAppTitle(String appTitle) {
        this.appTitle = appTitle;
    }

    public void setAppDesc(String appDesc) {
        this.appDesc = appDesc;
    }

    public String getAppTitle() {
        return appTitle;
    }

    public String getAppDesc() {
        return appDesc;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getDownloads() {
        return downloads;
    }

    public void setDownloads(String downloads) {
        this.downloads = downloads;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getRedirectUrl() {
        return redirectUrl;
    }

    public void setRedirectUrl(String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }

    public float getRating() {
        float val = 0;
        if (!rating.isEmpty()) val = Float.parseFloat(rating);

        return val;
    }
}
