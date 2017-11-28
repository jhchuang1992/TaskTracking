package com.edu.ouc.fragment;


import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.edu.ouc.adapter.TaskListViewAdapter;
import com.edu.ouc.model.TaskInfoModel;
import com.edu.ouc.model.TaskShowModel;
import com.edu.ouc.tasktracking.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JHC on 2017/11/23.
 */
public class TaskFragment extends Fragment {
    private Activity activity;
    private ListView listView;
    private List<TaskInfoModel> taskInfoModelList; //任务集合对象
    private TaskListViewAdapter adapter; //自定义适配器对象
    public TaskFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_task, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        listView=(ListView)getView().findViewById(R.id.lv_task_ok);
        taskInfoModelList = new ArrayList<TaskInfoModel>();
        getDatas();
        /**
         * 实例化Adapter对象(注意:必须要写在在getDatas() 方法后面,不然datas中没有数据)
         */
        adapter = new TaskListViewAdapter(getActivity(), taskInfoModelList);
        //将适配器变量的内容加载到List里(也就是把那一堆新闻都放了进去)
        listView.setAdapter(adapter);
    }
    /**
     * 通过接口获取任务列表的方法
     */
    public void getDatas(){
        for (int i=0;i<20;i++){
            TaskInfoModel taskInfoModel=new TaskInfoModel();
            taskInfoModel.setTask_name("文章标题"+i);
            taskInfoModel.setTask_author("时间："+i);
            taskInfoModelList.add(taskInfoModel);
        }
    }
}
