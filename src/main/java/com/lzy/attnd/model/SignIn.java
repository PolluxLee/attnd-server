package com.lzy.attnd.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.lzy.attnd.utils.Utils;
import org.springframework.lang.Nullable;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class SignIn extends Base {
    public interface ID{}
    public interface Name{}
    public interface Openid {}
    public interface Cipher {}
    public interface Location_Struct{}
    public interface All{}

    public SignIn(@NotNull(groups = {Remark.class, BaseAll.class}) Object remark, @Min(value = 0, groups = {Status.class, BaseAll.class}) int status, @Min(value = 1, groups = {ID.class, All.class}) int id, @NotBlank(groups = {Name.class, All.class}) @Size(max = 50, groups = {Name.class, All.class}) String openid, @NotBlank(groups = {Openid.class, All.class}) @Size(max = 50, groups = {Openid.class, All.class}) String name, @NotBlank(groups = {Cipher.class, All.class}) @Size(max = 50, groups = {Cipher.class, All.class}) String cipher, @NotNull(groups = {Location_Struct.class, All.class}) @Valid Location location) {
        super(remark, status);
        this.id = id;
        this.openid = openid;
        this.name = name;
        this.cipher = cipher;
        this.location = location;
    }

    public SignIn(@Min(value = 1, groups = {ID.class, All.class}) int id, @NotBlank(groups = {Name.class, All.class}) @Size(max = 50, groups = {Name.class, All.class}) String openid, @NotBlank(groups = {Openid.class, All.class}) @Size(max = 50, groups = {Openid.class, All.class}) String name, @NotBlank(groups = {Cipher.class, All.class}) @Size(max = 50, groups = {Cipher.class, All.class}) String cipher, @NotNull(groups = {Location_Struct.class, All.class}) @Valid Location location) {
        this.id = id;
        this.openid = openid;
        this.name = name;
        this.cipher = cipher;
        this.location = location;
    }


    public SignIn() {
        this.remark = new Object();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getCipher() {
        return cipher;
    }

    public void setCipher(String cipher) {
        this.cipher = cipher;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    @Min(value = 1,groups = {ID.class,All.class})
    private int id;
    @NotBlank(groups = {Name.class,All.class})
    @Size(max = 50,groups = {Name.class,All.class})
    private String openid;
    @NotBlank(groups = {Openid.class,All.class})
    @Size(max = 50,groups = {Openid.class,All.class})
    private String name;
    @NotBlank(groups = {Cipher.class,All.class})
    @Size(max = 50,groups = {Cipher.class,All.class})
    private String cipher;
    @NotNull(groups = {Location_Struct.class,All.class})
    @Valid
    private Location location;
}
