package com.android45.doctorfromnature.models;

public class NotificationModel {
    private String notification;
    private String DateAndTime;

    public NotificationModel() {

    }

    public NotificationModel(String notification, String dateAndTime) {
        this.notification = notification;
        DateAndTime = dateAndTime;
    }

    public String getNotification() {
        return notification;
    }

    public void setNotification(String notification) {
        this.notification = notification;
    }

    public String getDateAndTime() {
        return DateAndTime;
    }

    public void setDateAndTime(String dateAndTime) {
        DateAndTime = dateAndTime;
    }
}
