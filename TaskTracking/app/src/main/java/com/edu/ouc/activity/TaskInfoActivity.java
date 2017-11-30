package com.edu.ouc.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.edu.ouc.adapter.PublicTaskScheduleListViewAdapter;
import com.edu.ouc.function.NetWorkUtils;
import com.edu.ouc.function.SelectDataFromServer;
import com.edu.ouc.listview.PaiFaListView;
import com.edu.ouc.model.PublicShareUserinfo;
import com.edu.ouc.model.TaskInfoModel;
import com.edu.ouc.model.TaskScheduleModel;
import com.edu.ouc.tasktracking.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by JHC on 2017/11/23.
 * 管理员：代办-查看详情
 */

public class TaskInfoActivity extends Activity{
    private TextView textView_tv_taskinfo_author,textView_tv_taskinfo_taskname,textView_tv_taskinfo_tasktype,textView_tv_taskinfo_taskinfo,
            textView_taskinfo_status,textView_taskinfo_starttime,textView_taskinfo_endtime,textView_taskinfo_remarks;
   private Button button_taskinfo_endtask;
    private ScrollView scrollView_taskinfo; //滚动条
    private String taskInfo_status; //任务状态
    private PublicShareUserinfo publicShareUserinfo; //公共用户信息类
    private List<TaskScheduleModel> taskScheduleModelListView; //任务进度条目
    private PublicTaskScheduleListViewAdapter adapter; //自定义适配器对象--用于显示任务进度
    private PaiFaListView paiFaListView_lvtaskinfo; //任务进度
    private TaskInfoModel taskinfoLast; //上一个界面传递过来的参数
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_taskinfo);
        textView_tv_taskinfo_author=(TextView)findViewById(R.id.tv_taskinfo_author);
        textView_tv_taskinfo_taskname=(TextView)findViewById(R.id.tv_taskinfo_taskname);
        textView_tv_taskinfo_tasktype=(TextView)findViewById(R.id.tv_taskinfo_tasktype);
        textView_tv_taskinfo_taskinfo=(TextView)findViewById(R.id.tv_taskinfo_taskinfo);
        textView_taskinfo_status=(TextView)findViewById(R.id.tv_taskinfo_status);
        textView_taskinfo_starttime=(TextView)findViewById(R.id.tv_taskinfo_starttime);
        textView_taskinfo_endtime=(TextView)findViewById(R.id.tv_taskinfo_endtime);
        textView_taskinfo_remarks=(TextView)findViewById(R.id.tv_taskinfo_remarks);
        button_taskinfo_endtask=(Button)findViewById(R.id.btn_taskinfo_endtask);
        paiFaListView_lvtaskinfo=(PaiFaListView)findViewById(R.id.lv_taskinfo);
        scrollView_taskinfo=(ScrollView)findViewById(R.id.scv_taskinfo);
        //加载信息，将传递过来的条目显示在界面中
        initViewData();
        updateTaskScheduleList();  //更新任务进度
    }
    Handler handler=new Handler(){
        //0：提示出错了 1：提示未打开连接
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0:
                    Toast.makeText(getApplicationContext(), "哎呀，出错了。。。", Toast.LENGTH_LONG).show();
                    break;
                case 1:
                    Toast.makeText(getApplicationContext(), "网络未连接", Toast.LENGTH_LONG).show();
                    break;
                case 2:
                    adapter = new PublicTaskScheduleListViewAdapter(getApplicationContext(), taskScheduleModelListView);
                    //将适配器变量的内容加载到List里(也就是把那一堆新闻都放了进去)
                    paiFaListView_lvtaskinfo.setAdapter(adapter);
                    //设置scrollview初始化后滑动到顶部，必须在gridview填充数据之后，否则无法实现预期效果
                    scrollView_taskinfo.smoothScrollTo(0,20);
                    scrollView_taskinfo.setFocusable(true);
                    break;
                case 3:
                    Toast.makeText(getApplicationContext(), "更新任务进度失败", Toast.LENGTH_LONG).show();
                    break;
            }
        }
    };
    //加载信息----开始-----
    public void initViewData(){
        new Thread(){
            @Override
            public void run() {
                taskinfoLast= (TaskInfoModel) getIntent().getSerializableExtra("taskinfo");
                textView_tv_taskinfo_author.setText(taskinfoLast.getTask_author());
                textView_tv_taskinfo_taskname.setText(taskinfoLast.getTask_name());
                textView_tv_taskinfo_tasktype.setText(taskinfoLast.getTask_type());
                textView_tv_taskinfo_taskinfo.setText(taskinfoLast.getTask_info());
                textView_taskinfo_status.setText(taskinfoLast.getTask_status());
                taskInfo_status=taskinfoLast.getTask_status();
                textView_taskinfo_starttime.setText(taskinfoLast.getTask_startdate());
                textView_taskinfo_endtime.setText(taskinfoLast.getTask_enddate());
                textView_taskinfo_remarks.setText(taskinfoLast.getRemarks());
            }
        }.start();
    }
    //更新任务进度---开始----
    public void updateTaskScheduleList(){
        new Thread(){
            @Override
            public void run() {
                //获取任务进度
                NetWorkUtils netWorkUtils=new NetWorkUtils();
                // 获取手机所有连接管理对象（包括对wi-fi,net等连接的管理）
                netWorkUtils.isNetworkConnected(getApplicationContext());
                if(netWorkUtils.isNetworkConnected(getApplicationContext())==false){ //若网络未连接
                    handler.sendEmptyMessage(1);//发送消息到handler，提示网络未连接
                }else{ //若网络已连接，查询其任务进度
                    try {
                        //根据任务信息id查询任务进度
                        SelectDataFromServer selectDataFromServer = new SelectDataFromServer("http://10.0.2.2:8080/TaskTrackingService/getTaskSchedule.do?sql=where+taskinfo_id="+taskinfoLast.getId()+"+order+by+id");
                        if (selectDataFromServer.getContent().equals("error")) {
                            handler.sendEmptyMessage(0);//发送消息到handler，提示出错了
                        } else{
                            JSONObject jsonObject=new JSONObject(selectDataFromServer.getContent());
                            JSONArray jsonArray=jsonObject.getJSONArray("data");
                            //这两句代码必须的，为的是初始化出来gson这个对象，才能拿来用
                            Type type1=new TypeToken<List<TaskScheduleModel>>(){}.getType();
                            taskScheduleModelListView = new ArrayList<TaskScheduleModel>();
                            taskScheduleModelListView=new Gson().fromJson(jsonArray.toString(),type1);
                            handler.sendEmptyMessage(2);
                        }
                    } catch (Exception e) {
                        handler.sendEmptyMessage(3);//发送消息到handler，提示出错了
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }
    //更新任务进度---结束----
}
