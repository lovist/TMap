package com.example.lovasistvn.tmap;

/**
 * Created by Lovas Istvan on 2014.11.21..
 */
public class MapPoint {
    private String pointname;
    private String pointaddress;

    public MapPoint(String pointname, String pointadress) {
        this.pointname = pointname;
        this.pointaddress = pointadress;
    }

    public String getPointaddress() {
        return pointaddress;
    }

    public void setPointaddress(String pointadress) {
        this.pointaddress = pointadress;
    }

    public String getPointname() {
        return pointname;
    }

    public void setPointname(String pointname) {
        this.pointname = pointname;
    }
}
