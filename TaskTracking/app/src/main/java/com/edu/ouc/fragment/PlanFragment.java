package com.edu.ouc.fragment;


import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.edu.ouc.adapter.PlanPublicTaskInfoListViewAdapter;
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
 * 代办进行中
 */
public class PlanFragment extends Fragment {
    private UpLoadingDialog dialog; //弹框加载中
    private Activity activity;
    private ListView listView; //任务列表
    private List<TaskInfoModel> taskInfoModelList; //管理员-任务集合
    private PlanPublicTaskInfoListViewAdapter infoAdapter; //自定义管理员适配器对象
    public PlanFragment() {
        // Required empty public constructor
    }

    @Override
    public void onResume() {
        super.onResume();
        getDatas();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_plan, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        listView=(ListView)getView().findViewById(R.id.lv_task_plan);
        dialog = new UpLoadingDialog(getActivity());
        dialog.setCancelable(false); //设置这个对话框不能被用户按[返回键]而取消掉
        dialog.setCanceledOnTouchOutside(false);
        dialog.setDialogText("加载中......");
        dialog.show();
        getDatas();
    }
    Handler handler=new Handler(){
        //0：提示出错了 1：提示未打开连接
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0:
                    dialog.dismiss(); //取消弹框
                    Toast.makeText(getContext(), "哎呀，出错了。。。", Toast.LENGTH_LONG).show();
                    break;
                case 1:
                    dialog.dismiss(); //取消弹框
                    Toast.makeText(getContext(), "网络未连接", Toast.LENGTH_LONG).show();
                    break;
                case 2:
                    dialog.dismiss(); //取消弹框
                    infoAdapter = new PlanPublicTaskInfoListViewAdapter(getActivity(), taskInfoModelList);
                    //将适配器变量的内容加载到List里(也就是把那一堆新闻都放了进去)
                    listView.setAdapter(infoAdapter);
                    break;
            }
        }
    };
    //获取任务列表---开始-----
    public void getDatas(){
        new Thread(){
            @Override
            public void run() {
                //查询出属于本人的待办任务条目：若是管理员，则查询所有进行中的任务，若是班长，则查看属于本部门进行中的任务，若是个人，则查询本人进行中任务
                try {
                    taskInfoModelList = new ArrayList<TaskInfoModel>();
                    NetWorkUtils netWorkUtils=new NetWorkUtils();
                    // 获取手机所有连接管理对象（包括对wi-fi,net等连接的管理）
                    netWorkUtils.isNetworkConnected(getContext());
                    if(netWorkUtils.isNetworkConnected(getContext())==false){ //若网络未连接
                       handler.sendEmptyMessage(1);  //发送给handler，告知网络未连接
                    }else{ //若网络已连接
                        SelectDataFromServer selectDataFromServer=null;
                        if (AutoMaticLogin.getInstance().getUserInfo(getContext()).getRole().equals("管理员")){ //查询所有待接任务
                            selectDataFromServer=new SelectDataFromServer("getTaskInfo.do?sql=where+task_status='进行中'+and+task_lgname='"+AutoMaticLogin.getInstance().getUserInfo(getContext()).getLgname()+"'");
                        }else if (AutoMaticLogin.getInstance().getUserInfo(getContext()).getRole().equals("班长")) { //查询属于本单位进行中的任务
                            selectDataFromServer=new SelectDataFromServer("getBanZhangPlanTaskInfo.do?sql=where+ti.task_status='进行中'+and+tc.status!='已交接'+and+tc.status!='逾期'+and+tc.userinfo_id="+AutoMaticLogin.getInstance().getUserInfo(getContext()).getId());
                        }else if (AutoMaticLogin.getInstance().getUserInfo(getContext()).getRole().equals("职员")) { //查询属于本单位且未确认的订单，以及查询实时类型任务的订单
                            selectDataFromServer=new SelectDataFromServer("getYuanGongPlanTaskInfo.do?sql=where+tk.status='进行中'+and+tk.userinfo_id="+AutoMaticLogin.getInstance().getUserInfo(getContext()).getId());
                        }
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
                        handler.sendEmptyMessage(2);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }

            }}.start();
    }
}
