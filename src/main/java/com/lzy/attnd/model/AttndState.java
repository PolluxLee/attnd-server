package com.lzy.attnd.model;

public class AttndState {
    public AttndState(String openid, String name, String stu_id, int attnd_status, double distance) {
        this.openid = openid;
        this.name = name;
        this.stu_id = stu_id;
        this.attnd_status = attnd_status;
        this.distance = distance;
    }

    public AttndState() {
    }

    @Override
    public String toString() {
        return String.format("Openid:%s,Name:%s,Stuid:%s,Status:%d,Dist:%.3f",
                openid,name,stu_id,attnd_status,distance);
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStu_id() {
        return stu_id;
    }

    public void setStu_id(String stu_id) {
        this.stu_id = stu_id;
    }

    public int getAttnd_status() {
        return attnd_status;
    }

    public void setAttnd_status(int attnd_status) {
        this.attnd_status = attnd_status;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    private String openid;
    private String name;
    private String stu_id;
    private int attnd_status;
    private double distance;
}
