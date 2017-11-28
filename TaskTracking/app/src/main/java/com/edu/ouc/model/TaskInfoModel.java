package com.edu.ouc.model;

import java.io.Serializable;

/**
 * Created by JHC on 2017/11/23.
 * 任务详情
 */
public class TaskInfoModel implements Serializable {
	/**
		 * 
		 */
	private static final long serialVersionUID = 5790767399049546805L;
	private int id; // id
	private String task_name; //任务名称
	private String task_info; // 任务信息
	private String task_author; // 发布者
	private String task_status; // 任务状态
	private String task_startdate; //任务开始时间
	private String task_enddate; // 任务结束时间
	private String remarks; // 备注信息
	private String take_unit; //派发给的第一个部门
	private String confirm;//'等待第一个部门确认',
	private String task_type; //任务类型
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	public String getTask_name() {
		return task_name;
	}
	public void setTask_name(String task_name) {
		this.task_name = task_name;
	}
	public String getTask_info() {
		return task_info;
	}
	public void setTask_info(String task_info) {
		this.task_info = task_info;
	}
	public String getTask_author() {
		return task_author;
	}
	public void setTask_author(String task_author) {
		this.task_author = task_author;
	}
	public String getTask_status() {
		return task_status;
	}
	public void setTask_status(String task_status) {
		this.task_status = task_status;
	}
	public String getTask_startdate() {
		return task_startdate;
	}
	public void setTask_startdate(String task_startdate) {
		this.task_startdate = task_startdate;
	}
	public String getTask_enddate() {
		return task_enddate;
	}
	public void setTask_enddate(String task_enddate) {
		this.task_enddate = task_enddate;
	}
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	
	public String getTake_unit() {
		return take_unit;
	}
	public void setTake_unit(String take_unit) {
		this.take_unit = take_unit;
	}
	public String getConfirm() {
		return confirm;
	}
	public void setConfirm(String confirm) {
		this.confirm = confirm;
	}
	
	public String getTask_type() {
		return task_type;
	}
	public void setTask_type(String task_type) {
		this.task_type = task_type;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
}
