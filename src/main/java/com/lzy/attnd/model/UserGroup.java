package com.lzy.attnd.model;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class UserGroup extends Base {
    public UserGroup(@NotNull(groups = {Remark.class, BaseAll.class}) Object remark, @Min(value = 0, groups = {Status.class, BaseAll.class}) int status, @NotBlank(groups = {Name.class, All.class}) String name, @Min(value = 1, groups = {Creatorid.class, All.class}) int creator_id, @NotBlank(groups = {CreatorName.class, All.class}) String creator_name) {
        super(remark, status);
        this.name = name;
        this.creator_id = creator_id;
        this.creator_name = creator_name;
    }

    public UserGroup(@NotBlank(groups = {Name.class, All.class}) String name, @Min(value = 1, groups = {Creatorid.class, All.class}) int creator_id, @NotBlank(groups = {CreatorName.class, All.class}) String creator_name) {
        this.name = name;
        this.creator_id = creator_id;
        this.creator_name = creator_name;
    }

    public UserGroup() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCreator_id() {
        return creator_id;
    }

    public void setCreator_id(int creator_id) {
        this.creator_id = creator_id;
    }

    public String getCreator_name() {
        return creator_name;
    }

    public void setCreator_name(String creator_name) {
        this.creator_name = creator_name;
    }

    @NotBlank(groups = {UserGroup.Name.class,UserGroup.All.class})
    private String name;
    @Min(value = 1,groups = {UserGroup.Creatorid.class,UserGroup.All.class})
    private int creator_id;
    @NotBlank(groups = {UserGroup.CreatorName.class,UserGroup.All.class})
    private String creator_name;


    public interface Name{}
    public interface CreatorName{}
    public interface Creatorid{}
    public interface All{}
}
