package com.example.pracprac;

public class windClass {
    String lon,lat,dia,m_maxEfficieny,g_maxEfficieny,rotorCount,ratedVoltage;

    public windClass(String lon, String lat, String diameter,String ratedVoltage, String mechmaxEfficieny,String genemaxEfficieny, String rotorCount) {
        this.lon = lon;
        this.lat = lat;
        this.dia = diameter;
        this.m_maxEfficieny = mechmaxEfficieny;
        this.rotorCount = rotorCount;
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



    public String getDia() {
        return dia;
    }

    public void setDia(String diameter) {
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

    public String getrotorCount() {
        return rotorCount;
    }

    public void setrotorCount(String rotorCount) {
        this.rotorCount = rotorCount;
    }
}
