package com.example.pracprac;

public class solarClass {
    String lon,lat,area,maxEfficieny,panelCount,ratedVoltage;

    public solarClass(String lon, String lat, String area,String ratedVoltage, String maxEfficieny, String panelCount) {
        this.lon = lon;
        this.lat = lat;
        this.area = area;
        this.maxEfficieny = maxEfficieny;
        this.panelCount = panelCount;
        this.ratedVoltage=ratedVoltage;
    }
    public solarClass()
    {}
    public String getRatedVoltage()
    {
        return ratedVoltage;
    }
    public void setRatedVoltage(String voltage)
    {
        this.ratedVoltage=voltage;
    }


    public String getLon() {
        return lon;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }



    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getMaxEfficieny() {
        return maxEfficieny;
    }

    public void setMaxEfficieny(String maxEfficieny) {
        this.maxEfficieny = maxEfficieny;
    }

    public String getPanelCount() {
        return panelCount;
    }

    public void setPanelCount(String panelCount) {
        this.panelCount = panelCount;
    }
}
