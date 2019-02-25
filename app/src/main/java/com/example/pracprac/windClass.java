package com.example.pracprac;

public class windClass {
    String lon,lat,dia,m_maxEfficieny,g_maxEfficieny,panelCount,ratedVoltage;

    public windClass(String lon, String lat, String diameter,String ratedVoltage, String mechmaxEfficieny,String genemaxEfficieny, String panelCount) {
        this.lon = lon;
        this.lat = lat;
        this.dia = diameter;
        this.m_maxEfficieny = mechmaxEfficieny;
        this.panelCount = panelCount;
        this.ratedVoltage=ratedVoltage;
        this.g_maxEfficieny = genemaxEfficieny;
    }
    public windClass()
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
        return dia;
    }

    public void setArea(String diameter) {
        this.dia = diameter;
    }

    public String getMechMaxEfficieny() {
        return m_maxEfficieny;
    }

    public void setMechMaxEfficieny(String maxMechEfficieny) {
        this.m_maxEfficieny = maxMechEfficieny;
    }
    public String getGeneMaxEfficieny() {
        return g_maxEfficieny;
    }

    public void setGeneMaxEfficieny(String maxGeneEfficieny) {
        this.g_maxEfficieny = maxGeneEfficieny;
    }

    public String getPanelCount() {
        return panelCount;
    }

    public void setPanelCount(String panelCount) {
        this.panelCount = panelCount;
    }
}
