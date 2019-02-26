package com.example.pracprac;

public class solar_daily_data_class {

    private String date;
    private String value;

    public solar_daily_data_class(){}
    public solar_daily_data_class(String date, String value) {
        this.date = date;
        this.value = value;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}