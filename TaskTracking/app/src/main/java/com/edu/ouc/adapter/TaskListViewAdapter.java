package com.edu.ouc.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.edu.ouc.activity.NewTaskActivity;
import com.edu.ouc.activity.TaskInfoActivity;
import com.edu.ouc.activity.TaskInfoBanZhangActivity;
import com.edu.ouc.activity.TaskInfoYuanGongActivity;
import com.edu.ouc.model.PublicShareUserinfo;
import com.edu.ouc.model.TaskInfoModel;
import com.edu.ouc.tasktracking.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JHC on 2017/11/23.
 * 为已完成任务中的ListView写一个填充数据的适配器,通过这个适配器来确定ListView上每个Item显示的数据.
 */

public class TaskListViewAdapter extends BaseAdapter {
    private List<TaskInfoModel> datas = new ArrayList<TaskInfoModel>();//任务列表集合
    private Context context;
    public TaskListViewAdapter(Context context, List<TaskInfoModel> datas) {
        this.datas = datas;
        this.context = context;
    }
    @Override
    public int getCount() {
        return datas.size(); //返回列表的长度
    }

    @Override
    public TaskInfoModel getItem(int position) {
        return datas.get(position); //通过列表的位置 获得集合中的对象
    }

    @Override
    public long getItemId(int position) { // 获得集合的Item的位
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder vh;
        if(convertView==null){
            vh = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.listview_fragment_task,null);
            vh.tvType = (TextView) convertView.findViewById(R.id.tv_task_type);
            vh.tvTitle = (TextView) convertView.findViewById(R.id.tv_task_title);
            vh.tvInfo = (TextView) convertView.findViewById(R.id.tv_task_info);
            convertView.setTag(vh);
        }else {
            vh = (ViewHolder) convertView.getTag();
        }
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //点击进入详情界面，分为三种详情界面
                Intent  intent=new Intent();
                if(PublicShareUserinfo.role.equals("管理员")){
                    intent.setClass(context,TaskInfoActivity.class);
                }else if(PublicShareUserinfo.role.equals("班长")){
                    intent.setClass(context,TaskInfoBanZhangActivity.class);
                }else if(PublicShareUserinfo.role.equals("职员")){
                   intent.setClass(context,TaskInfoYuanGongActivity.class);
                }
                Bundle bundle = new Bundle();
                bundle.putSerializable("taskinfo", datas.get(position));
                intent.putExtras(bundle);
                context.startActivity(intent);
            }
        });
        /*vh.img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context,"点击图片",Toast.LENGTH_SHORT).show();
            }
        });*/
        TaskInfoModel taskInfoModel = datas.get(position);
        vh.tvType.setText("["+taskInfoModel.getTask_status()+"]"); //任务状态
        vh.tvTitle.setText(taskInfoModel.getTask_name()); //任务主题
        vh.tvInfo.setText("发布时间："+taskInfoModel.getTask_startdate()); //发布时间
        return convertView;
    }
    protected class ViewHolder {
        private TextView tvType;
        private TextView tvTitle;
        private TextView tvInfo;
    }
}
