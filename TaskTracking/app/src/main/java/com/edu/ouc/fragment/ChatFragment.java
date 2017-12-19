package com.edu.ouc.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.edu.ouc.activity.NewTaskActivity;
import com.edu.ouc.activity.ShowSummaryActivity;
import com.edu.ouc.adapter.PublicDaiJieListViewAdapter;
import com.edu.ouc.dialog.UpLoadingDialog;
import com.edu.ouc.function.AutoMaticLogin;
import com.edu.ouc.function.NetWorkUtils;
import com.edu.ouc.function.SelectDataFromServer;
import com.edu.ouc.model.TaskInfoModel;
import com.edu.ouc.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by JHC on 2017/11/23.
 * 待接任务--主框架---管理员-班长-职员-共用一个listView适配器
 */
public class ChatFragment extends Fragment implements View.OnClickListener{
    private ListView listView; //任务列表
    private List<TaskInfoModel> taskInfoModelList; //任务集合对象
    private PublicDaiJieListViewAdapter adapter; //自定义适配器对象
    private TextView textView_newTask; //新建任务按钮
    private UpLoadingDialog dialog; //弹框加载中
    public ChatFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chat, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        textView_newTask=(TextView)getActivity().findViewById(R.id.tv_newtask);
        textView_newTask.setOnClickListener(this);
        listView=(ListView)getView().findViewById(R.id.lv_task_chat);
        dialog = new UpLoadingDialog(getActivity());
        dialog.setCancelable(false); //设置这个对话框不能被用户按[返回键]而取消掉
        dialog.setCanceledOnTouchOutside(false);
        dialog.setDialogText("加载中......");
        dialog.show();
        getDatas();
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_newtask://新建任务
                Intent  intent= new Intent(getActivity(),NewTaskActivity.class);
                startActivity(intent);
                break;
        }
    }
    @Override
    public void onResume() {
        getDatas(); //更新任务列表
        super.onResume();
    }
    Handler handler=new Handler(){
        //0：提示出错了 1：提示未打开连接
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0:
                    dialog.dismiss();
                    Toast.makeText(getContext(), "哎呀，出错了。。。", Toast.LENGTH_LONG).show();
                    break;
                case 1:
                    dialog.dismiss();
                    Toast.makeText(getContext(), "网络未连接", Toast.LENGTH_LONG).show();
                    break;
                case 2:
                    dialog.dismiss(); //取消显示加载中
                    adapter = new PublicDaiJieListViewAdapter(getActivity(), taskInfoModelList);
                    //将适配器变量的内容加载到List里(也就是把那一堆新闻都放了进去)
                    listView.setAdapter(adapter);
                    break;
                case 3:
                    textView_newTask.setVisibility(View.GONE);//设置新建按钮不可见
                    break;
            }
        }
    };
    //获取任务列表---开始-----
    public void getDatas(){
        new Thread(){
            @Override
            public void run() {
                //查询出属于本人的待接任务条目：若是管理员，则查询所有的待接的任务，若是班长，则查看属于本部门和全体信息，若是个人，则查询本部门且推送给个人的任务
                try {
                    taskInfoModelList = new ArrayList<TaskInfoModel>();
                    NetWorkUtils netWorkUtils=new NetWorkUtils();
                    // 获取手机所有连接管理对象（包括对wi-fi,net等连接的管理）
                    netWorkUtils.isNetworkConnected(getContext());
                    if(netWorkUtils.isNetworkConnected(getContext())==false){ //若网络未连接
                        handler.sendEmptyMessage(1); //告知handler，网络未连接
                    }else{ //若网络已连接
                        if (AutoMaticLogin.getInstance().getUserInfo(getContext()).getRole().equals("管理员")){ //查询所有待接任务-未确认且状态为待接
                            SelectDataFromServer selectDataFromServer=new SelectDataFromServer("getTaskInfo.do?sql=where+confirm=0+and+task_status='待接'+and+task_lgname='"+AutoMaticLogin.getInstance().getUserInfo(getContext()).getLgname()+"'");
                            if(selectDataFromServer.getContent().equals("error")){
                                handler.sendEmptyMessage(0);//发送消息到handler，提示出错了
                            }else if(selectDataFromServer.getContent().equals("@")){
                                dialog.dismiss(); //取消显示加载中
                            }else{
                                JSONObject jsonObject=new JSONObject(selectDataFromServer.getContent());
                                JSONArray jsonArray=jsonObject.getJSONArray("data");
                                //这两句代码必须的，为的是初始化出来gson这个对象，才能拿来用
                                Type type1=new TypeToken<List<TaskInfoModel>>(){}.getType();
                                taskInfoModelList=new Gson().fromJson(jsonArray.toString(),type1);
                            }
                        }else if (AutoMaticLogin.getInstance().getUserInfo(getContext()).getRole().equals("班长")) { //查询属于本单位且未确认的订单，以及查询实时类型任务的订单
                            handler.sendEmptyMessage(3); //设置新建任务按钮不可见
                            SelectDataFromServer selectDataFromServer=new SelectDataFromServer("getBanZhangWaitTaskInfo.do?sql='"+AutoMaticLogin.getInstance().getUserInfo(getContext()).getUnit()+"'");
                            if(selectDataFromServer.getContent().equals("error")){
                                handler.sendEmptyMessage(0);//发送消息到handler，提示出错了
                            }else if(selectDataFromServer.getContent().equals("@")){
                                dialog.dismiss(); //取消显示加载中
                            }else{
                                JSONObject jsonObject=new JSONObject(selectDataFromServer.getContent());
                                JSONArray jsonArray=jsonObject.getJSONArray("data");
                                //这两句代码必须的，为的是初始化出来gson这个对象，才能拿来用
                                Type type1=new TypeToken<List<TaskInfoModel>>(){}.getType();
                                taskInfoModelList=new Gson().fromJson(jsonArray.toString(),type1);
                            }
                        }else if (AutoMaticLogin.getInstance().getUserInfo(getContext()).getRole().equals("职员")) { //查询属于本单位且未确认的订单，以及查询实时类型任务的订单
                            handler.sendEmptyMessage(3); //设置新建任务按钮不可见
                            SelectDataFromServer selectDataFromServer=new SelectDataFromServer("getYuanGongWaitTaskInfo.do?sql="+AutoMaticLogin.getInstance().getUserInfo(getContext()).getId());
                            if(selectDataFromServer.getContent().equals("error")){
                                handler.sendEmptyMessage(0);//发送消息到handler，提示出错了
                            }else if(selectDataFromServer.getContent().equals("@")){
                                dialog.dismiss(); //取消显示加载中
                            }else{
                                JSONObject jsonObject=new JSONObject(selectDataFromServer.getContent());
                                JSONArray jsonArray=jsonObject.getJSONArray("data");
                                //这两句代码必须的，为的是初始化出来gson这个对象，才能拿来用
                                Type type1=new TypeToken<List<TaskInfoModel>>(){}.getType();
                                taskInfoModelList=new Gson().fromJson(jsonArray.toString(),type1);
                            }
                        }
                        handler.sendEmptyMessage(2);
                    }
                }catch (Exception e){
                    handler.sendEmptyMessage(0); //告知handler，出错了
                    e.printStackTrace();
                }
            }}.start();
    }
}
