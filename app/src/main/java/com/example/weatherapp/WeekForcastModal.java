package com.example.weatherapp;

public class WeekForcastModal {
    private String Low;
    private String High;
    private String Day;
    private String Icon;

    public WeekForcastModal(String low, String high, String day, String icon) {
        Low = low;
        High = high;
        Day = day;
        Icon = icon;
    }

    public String getLow() {
        return Low;
    }

    public void setLow(String low) {
        Low = low;
    }

    public String getHigh() {
        return High;
    }

    public void setHigh(String high) {
        High = high;
    }

    public String getDay() {
        return Day;
    }

    public void setDay(String day) {
        Day = day;
    }

    public String getIcon() {
        return Icon;
    }

    public void setIcon(String icon) {
        Icon = icon;
    }
}
