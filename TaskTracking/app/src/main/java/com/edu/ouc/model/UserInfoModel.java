package com.edu.ouc.model;

import java.io.Serializable;

/**
 * Created by JHC on 2017/11/22.
 * 用户信息Model
 */

public class UserInfoModel implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 5790767399049546805L;
    private int id; // id
    private String lgname; // 登录名
    private String lgpwd; // 登录密码
    private String truename; // 真实姓名
    private String role; // 角色
    private String phone; // 电话
    private String unit; // 单位
    private String apiid; //手机唯一标识
    private boolean Ischeck; //是否选中，用于班长派发任务给职员的时候
    private String email; //邮箱地址
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLgname() {
        return lgname;
    }

    public void setLgname(String lgname) {
        this.lgname = lgname;
    }

    public String getLgpwd() {
        return lgpwd;
    }

    public void setLgpwd(String lgpwd) {
        this.lgpwd = lgpwd;
    }

    public String getTruename() {
        return truename;
    }

    public void setTruename(String truename) {
        this.truename = truename;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public boolean ischeck() {
        return Ischeck;
    }

    public void setIscheck(boolean ischeck) {
        Ischeck = ischeck;
    }

    public String getApiid() {return apiid;}

    public void setApiid(String apiid) {this.apiid = apiid;}

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
