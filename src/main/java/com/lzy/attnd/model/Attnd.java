package com.lzy.attnd.model;

import javax.validation.constraints.*;

public class Attnd {

    @Min(0)
    private int attnd_id;

    @NotBlank
    @Size(max = 50)
    private String attnd_name;

    @Min(0)
    private long start_time;

    @Min(0)
    @Max(1440)
    //attendance last minutes num 0~1440(1 day)
    private int last;

    @NotNull
    private Location location;

    @NotBlank
    @Size(max = 50)
    private String teacherName;


    public class Location{
        @Min(-90)
        @Max(90)
        private float latitude;

        @Min(-180)
        @Max(180)
        private float longitude;

        @Min(0)
        private float accuracy;


        public float getLatitude() {
            return latitude;
        }

        public void setLatitude(float latitude) {
            this.latitude = latitude;
        }

        public float getLongitude() {
            return longitude;
        }

        public void setLongitude(float longitude) {
            this.longitude = longitude;
        }

        public float getAccuracy() {
            return accuracy;
        }

        public void setAccuracy(float accuracy) {
            this.accuracy = accuracy;
        }

    }

    public Attnd() {
    }

    public Attnd(@Min(0) int attnd_id, @NotBlank @Size(max = 50) String attnd_name, @Min(0) long start_time, @Min(0) @Max(1440) int last, @NotNull Location location, @NotBlank @Size(max = 50) String teacherName) {
        this.attnd_id = attnd_id;
        this.attnd_name = attnd_name;
        this.start_time = start_time;
        this.last = last;
        this.location = location;
        this.teacherName = teacherName;
    }



    public int getAttnd_id() {
        return attnd_id;
    }

    public void setAttnd_id(int attnd_id) {
        this.attnd_id = attnd_id;
    }

    public String getAttnd_name() {
        return attnd_name;
    }

    public void setAttnd_name(String attnd_name) {
        this.attnd_name = attnd_name;
    }

    public long getStart_time() {
        return start_time;
    }

    public void setStart_time(long start_time) {
        this.start_time = start_time;
    }

    public int getLast() {
        return last;
    }

    public void setLast(int last) {
        this.last = last;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }
}
