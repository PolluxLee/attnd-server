package com.lzy.attnd.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.lzy.attnd.utils.Utils;
import org.hibernate.validator.constraints.Range;
import org.springframework.lang.Nullable;

import javax.validation.constraints.Min;

public class Location {
    public double getLatitude() {
        return latitude;
    }

    //return m
    public static double calDistanceBetweenLocation(Location loc1,Location loc2){
        double theta = loc1.longitude - loc2.longitude;
        double dist = Math.sin(deg2rad(loc1.latitude)) * Math.sin(deg2rad(loc2.latitude)) + Math.cos(deg2rad(loc1.latitude)) * Math.cos(deg2rad(loc2.latitude)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;

        return dist * 1.609344 * 1000;
    }

    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    /*::	This function converts decimal degrees to radians						 :*/
    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    private static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    /*::	This function converts radians to decimal degrees						 :*/
    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    private static double rad2deg(double rad) {
        return (rad * 180 / Math.PI);
    }

    @JsonIgnore
    @Nullable
    public String getLocationJson(){
        return Utils.ObjectToJson(this);
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(double accuracy) {
        this.accuracy = accuracy;
    }

    public Location(@Range(min = -90, max = 90, groups = {Attnd.Location_Struct.class, Attnd.All.class}) double latitude, @Range(min = -180, max = 180, groups = {Attnd.Location_Struct.class, Attnd.All.class}) double longitude, @Min(value = 0, groups = {Attnd.Location_Struct.class, Attnd.All.class}) double accuracy) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.accuracy = accuracy;
    }


    public Location(){

    }

    @Range(min = -90,max = 90,groups = {Location_Struct.class,Attnd.Location_Struct.class,Attnd.All.class})
    private double latitude;

    @Range(min = -180,max = 180,groups = {Location_Struct.class,Attnd.Location_Struct.class,Attnd.All.class})
    private double longitude;

    @Min(value = 0,groups = {Location_Struct.class,Attnd.Location_Struct.class,Attnd.All.class})
    private double accuracy;

    public interface Location_Struct{}
}
