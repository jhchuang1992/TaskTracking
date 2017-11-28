package com.edu.ouc.model;

/**
 * Created by JHC on 2017/11/23.
 * 任务显示实体
 */

public class TaskShowModel {
    private String tasktitle; //标题
    private String taskauthor; //作者
    private String startDate; //发布时间
    private String taskType; //任务类型

    public String getTasktitle() {
        return tasktitle;
    }

    public void setTasktitle(String tasktitle) {
        this.tasktitle = tasktitle;
    }

    public String getTaskauthor() {
        return taskauthor;
    }

    public void setTaskauthor(String taskauthor) {
        this.taskauthor = taskauthor;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getTaskType() {
        return taskType;
    }

    public void setTaskType(String taskType) {
        this.taskType = taskType;
    }
}
