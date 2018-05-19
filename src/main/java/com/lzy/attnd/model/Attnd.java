package com.lzy.attnd.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.lzy.attnd.utils.Utils;
import org.hibernate.validator.constraints.Range;
import org.springframework.cglib.core.Converter;
import org.springframework.lang.Nullable;

import javax.validation.Valid;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.sql.SQLData;

@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class Attnd extends Base {

    public interface ID{}
    public interface Name{}
    public interface StartTime{}
    public interface Last{}
    public interface Location_Struct{}
    public interface AddrName{}
    public interface GroupNameNotNull {}
    public interface TeacherName{}
    public interface TeacherID{}
    public interface Cipher{}
    public interface All{}


    public Attnd(@NotNull(groups = {Remark.class, BaseAll.class}) Object remark, @Min(value = 0, groups = {Status.class, BaseAll.class}) int status, @Min(value = 0, groups = {ID.class, All.class}) int attnd_id, @NotBlank @Size(max = 50, groups = {Name.class, All.class}) String attnd_name, @Min(value = 0, groups = {StartTime.class, All.class}) long start_time, @Range(min = 0, max = 1440, groups = {Last.class, All.class}) int last, @NotNull(groups = {Location_Struct.class, All.class}) @Valid Location location, @NotBlank(groups = {AddrName.class, All.class}) String addr_name, @NotNull(groups = {GroupNameNotNull.class, All.class}) String group_name, @NotBlank(groups = {TeacherName.class, All.class}) @Size(max = 50, groups = {TeacherName.class, All.class}) String teacher_name, @Min(value = 1, groups = {TeacherID.class, All.class}) int teacher_id, @NotBlank(groups = {Cipher.class, All.class}) @Size(max = 50, groups = {Cipher.class, All.class}) String cipher) {
        super(remark, status);
        this.attnd_id = attnd_id;
        this.attnd_name = attnd_name;
        this.start_time = start_time;
        this.last = last;
        this.location = location;
        this.addr_name = addr_name;
        this.group_name = group_name;
        this.teacher_name = teacher_name;
        this.teacher_id = teacher_id;
        this.cipher = cipher;
    }

    public Attnd(@Min(value = 0, groups = {ID.class, All.class}) int attnd_id, @NotBlank @Size(max = 50, groups = {Name.class, All.class}) String attnd_name, @Min(value = 0, groups = {StartTime.class, All.class}) long start_time, @Range(min = 0, max = 1440, groups = {Last.class, All.class}) int last, @NotNull(groups = {Location_Struct.class, All.class}) @Valid Location location, @NotBlank(groups = {AddrName.class, All.class}) String addr_name, @NotNull(groups = {GroupNameNotNull.class, All.class}) String group_name, @NotBlank(groups = {TeacherName.class, All.class}) @Size(max = 50, groups = {TeacherName.class, All.class}) String teacher_name, @Min(value = 1, groups = {TeacherID.class, All.class}) int teacher_id, @NotBlank(groups = {Cipher.class, All.class}) @Size(max = 50, groups = {Cipher.class, All.class}) String cipher) {
        this.attnd_id = attnd_id;
        this.attnd_name = attnd_name;
        this.start_time = start_time;
        this.last = last;
        this.location = location;
        this.addr_name = addr_name;
        this.group_name = group_name;
        this.teacher_name = teacher_name;
        this.teacher_id = teacher_id;
        this.cipher = cipher;
    }

    @Min(value = 0,groups = {ID.class,All.class})
    private int attnd_id;

    @NotBlank(groups = {Name.class,All.class})
    @Size(max = 50,groups = {Name.class,All.class})
    private String attnd_name;

    @Min(value = 0,groups = {StartTime.class,All.class})
    private long start_time;

    //attendance last minutes num 0~1440(1 day)
    @Range(min = 0,max = 1440,groups = {Last.class,All.class})
    private int last;

    @NotNull(groups = {Location_Struct.class,All.class})
    @Valid
    private Location location;

    @NotBlank(groups = {AddrName.class,All.class})
    @Size(max = 50,groups = {AddrName.class,All.class})
    private String addr_name;

    @NotNull(groups = {GroupNameNotNull.class,All.class})
    private String group_name;

    @NotBlank(groups = {TeacherName.class,All.class})
    @Size(max = 50,groups = {TeacherName.class,All.class})
    private String teacher_name;

    @Min(value = 1, groups = {TeacherID.class,All.class})
    private int teacher_id;

    @NotBlank(groups = {Cipher.class,All.class})
    @Size(max = 50,groups = {Cipher.class,All.class})
    private String cipher;

    public Attnd() {
        this.location = new Location();
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

    @JsonIgnore
    @Nullable
    public String getLocationJson() {
        return Utils.ObjectToJson(location);
    }


    public void setLocation(Location location) {
        this.location = location;
    }

    public String getAddr_name() {
        return addr_name;
    }

    public void setAddr_name(String addr_name) {
        this.addr_name = addr_name;
    }

    public String getGroup_name() {
        return group_name;
    }

    public void setGroup_name(String group_name) {
        this.group_name = group_name;
    }

    public String getTeacher_name() {
        return teacher_name;
    }

    public void setTeacher_name(String teacher_name) {
        this.teacher_name = teacher_name;
    }

    public int getTeacher_id() {
        return teacher_id;
    }

    public void setTeacher_id(int teacher_id) {
        this.teacher_id = teacher_id;
    }

    public String getCipher() {
        return cipher;
    }

    public void setCipher(String cipher) {
        this.cipher = cipher;
    }


}
