package com.edu.ouc.model;

/**
 * Created by JHC on 2017/11/28.
 * 班长类表
 */

public class TaskScheduleModel {
    /**
     *
     */
    private static final long serialVersionUID = 5790767399049546805L;
    private int id; // id
    private int taskinfo_id; // 任务信息id
    private int userinfo_id; // 用户信息id
    private String unit; // 单位
    private String truename; // 真实姓名
    private String status; // 任务状态
    private String takedate; // 接收时间
    private String commitdate; // 提交时间
    private String description_title; // 任务描述主题
    private String description_info; // 任务描述信息
    private String confirm; // '等待人员确认，用，分隔,5：0，10：0，2：0',
    private String take_person;//'派发给的人员id,用，分隔',
    private String take_unit; //派发给的第一个部门
    private String confirm_unit; //等待部门确认
    private String take_person_date;//派发给个人的时间
    private String take_unit_date;//派发给单位的时间
    private String task_endDate;//派发给个人的截止时间
    private String person_remarks; //派发给个人的备注
    private String unit_remarks; //派发给单位的备注

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTaskinfo_id() {
        return taskinfo_id;
    }

    public void setTaskinfo_id(int taskinfo_id) {
        this.taskinfo_id = taskinfo_id;
    }

    public int getUserinfo_id() {
        return userinfo_id;
    }

    public void setUserinfo_id(int userinfo_id) {
        this.userinfo_id = userinfo_id;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getTruename() {
        return truename;
    }

    public void setTruename(String truename) {
        this.truename = truename;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTakedate() {
        return takedate;
    }

    public void setTakedate(String takedate) {
        this.takedate = takedate;
    }

    public String getCommitdate() {
        return commitdate;
    }

    public void setCommitdate(String commitdate) {
        this.commitdate = commitdate;
    }

    public String getDescription_title() {
        return description_title;
    }

    public void setDescription_title(String description_title) {
        this.description_title = description_title;
    }

    public String getDescription_info() {
        return description_info;
    }

    public void setDescription_info(String description_info) {
        this.description_info = description_info;
    }

    public String getConfirm() {
        return confirm;
    }

    public void setConfirm(String confirm) {
        this.confirm = confirm;
    }

    public String getTake_person() {
        return take_person;
    }

    public void setTake_person(String take_person) {
        this.take_person = take_person;
    }

    public String getTake_unit() {
        return take_unit;
    }

    public void setTake_unit(String take_unit) {
        this.take_unit = take_unit;
    }

    public String getConfirm_unit() {
        return confirm_unit;
    }

    public void setConfirm_unit(String confirm_unit) {
        this.confirm_unit = confirm_unit;
    }

    public String getTake_person_date() {
        return take_person_date;
    }

    public void setTake_person_date(String take_person_date) {
        this.take_person_date = take_person_date;
    }

    public String getTake_unit_date() {
        return take_unit_date;
    }

    public void setTake_unit_date(String take_unit_date) {
        this.take_unit_date = take_unit_date;
    }

    public String getTask_endDate() {
        return task_endDate;
    }

    public void setTask_endDate(String task_endDate) {
        this.task_endDate = task_endDate;
    }

    public String getPerson_remarks() {
        return person_remarks;
    }

    public void setPerson_remarks(String person_remarks) {
        this.person_remarks = person_remarks;
    }

    public String getUnit_remarks() {
        return unit_remarks;
    }

    public void setUnit_remarks(String unit_remarks) {
        this.unit_remarks = unit_remarks;
    }
}
