package com.example.lovasistvn.tmap;

/**123
 * Created by Lovas Istvan on 2014.11.21..
 */
public class MapPoint {
    private String pointname;
    private String pointaddress;
    private String pointlatitude;
    private String pointlongitude;


    public MapPoint(String pointname, String pointaddress, String pointlatitude, String pointlongitude) {
        this.pointname = pointname;
        this.pointaddress = pointaddress;
        this.pointlatitude = pointlatitude;
        this.pointlongitude = pointlongitude;
    }

    public String getPointaddress() {
        return pointaddress;
    }

    public void setPointaddress(String pointaddress) {
        this.pointaddress = pointaddress;
    }

    public String getPointname() {
        return pointname;
    }

    public void setPointname(String pointname) {
        this.pointname = pointname;
    }

    public String getPointlatitude() {
        return pointlatitude;
    }

    public void setPointlatitude(String pointlatitude) {
        this.pointlatitude = pointlatitude;
    }

    public String getPointlongitude(){
        return this.pointlongitude;
    }

    public void setPointlongitude(String pointlongitude){
        this.pointlongitude = pointlongitude;
    }




}
