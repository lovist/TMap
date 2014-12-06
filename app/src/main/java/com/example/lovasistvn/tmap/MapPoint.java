package com.example.lovasistvn.tmap;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.Editable;

/**
 * Created by Lovas Istvan on 2014.11.21..
 */
public class MapPoint implements Parcelable {
    private int pointid;
    private String pointname;
    private String pointaddress;
    private double pointlatitude;
    private double pointlongitude;

    public MapPoint() {
    }

    public MapPoint(Parcel parcel) {
        this.pointid=parcel.readInt();
        this.pointname = parcel.readString();
        this.pointaddress = parcel.readString();
        this.pointlatitude = parcel.readDouble();
        this.pointlongitude = parcel.readDouble();
    }

    public MapPoint(int pointid, String pointname, String pointaddress, double pointlatitude, double pointlongitude) {
        this.pointid= pointid;
        this.pointname = pointname;
        this.pointaddress = pointaddress;
        this.pointlatitude = pointlatitude;
        this.pointlongitude = pointlongitude;
    }
    public int getPointid(){
        return this.pointid;
    }

    public void setPointid(int pointid){
        this.pointid=pointid;
    }

    public String getPointaddress() {
        return pointaddress;
    }

    public void setPointaddress(String pointaddress) {
        this.pointaddress = pointaddress;
    }

    public String getPointname() {
        return this.pointname;
    }

    public void setPointname(String pointname) {
        this.pointname = pointname;
    }

    public double getPointlatitude() {
        return this.pointlatitude;
    }

    public void setPointlatitude(double pointlatitude) {
        this.pointlatitude = pointlatitude;
    }

    public double getPointlongitude(){
        return this.pointlongitude;
    }

    public void setPointlongitude(double pointlongitude){
        this.pointlongitude = pointlongitude;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) { // OVERRIDE METHOD #2
        parcel.writeInt(this.pointid);
        parcel.writeString(this.pointname);
        parcel.writeString(this.pointaddress);
        parcel.writeDouble(this.pointlatitude);
        parcel.writeDouble(this.pointlongitude);
    }

    public static final Parcelable.Creator CREATOR
            = new Parcelable.Creator() {
        public MapPoint createFromParcel(Parcel in) {
            return new MapPoint(in);
        }

        public MapPoint[] newArray(int size) {
            return new MapPoint[size];
        }
    };
}
