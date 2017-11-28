package com.edu.ouc.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.edu.ouc.function.AddDataToServer;
import com.edu.ouc.function.AutoMaticLogin;
import com.edu.ouc.function.NetWorkUtils;
import com.edu.ouc.function.SelectDataFromServer;
import com.edu.ouc.model.PublicShareUserinfo;
import com.edu.ouc.model.TaskInfoModel;
import com.edu.ouc.model.TaskScheduleModel;
import com.edu.ouc.model.UserInfoModel;
import com.edu.ouc.tasktracking.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by JHC on 2017/11/28.
 * 班长查看任务详情的信息
 */

public class TaskInfoBanZhangActivity extends Activity implements View.OnClickListener{
    private TextView textView_tv_taskinfo_author,textView_tv_taskinfo_taskname,textView_tv_taskinfo_tasktype,textView_tv_taskinfo_taskinfo,
            textView_taskinfo_status,textView_taskinfo_starttime,textView_taskinfo_endtime,textView_taskinfo_remarks;
    private Button button__taskinfo_commit,button__taskinfo_paifa;
    private String taskInfo_status; //任务状态
    private TaskInfoModel taskinfoModel; //上一个界面传过来的任务信息
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_taskinfobanzhang);
        textView_tv_taskinfo_author=(TextView)findViewById(R.id.tv_taskinfobanzhang_author);
        textView_tv_taskinfo_taskname=(TextView)findViewById(R.id.tv_taskinfobanzhang_taskname);
        textView_tv_taskinfo_tasktype=(TextView)findViewById(R.id.tv_taskinfobanzhang_tasktype);
        textView_tv_taskinfo_taskinfo=(TextView)findViewById(R.id.tv_taskinfobanzhang_taskinfo);
        textView_taskinfo_status=(TextView)findViewById(R.id.tv_taskinfobanzhang_status);
        textView_taskinfo_starttime=(TextView)findViewById(R.id.tv_taskinfobanzhang_starttime);
        textView_taskinfo_endtime=(TextView)findViewById(R.id.tv_taskinfobanzhang_endtime);
        textView_taskinfo_remarks=(TextView)findViewById(R.id.tv_taskinfobanzhang_remarks);
        button__taskinfo_commit=(Button)findViewById(R.id.btn_taskinfobanzhang_commit);
        button__taskinfo_paifa=(Button)findViewById(R.id.btn_taskinfobanzhang_paifa);
        button__taskinfo_commit.setOnClickListener(this);
        button__taskinfo_paifa.setOnClickListener(this);
        //加载信息，将传递过来的条目显示在界面中
        initViewData();

    }
    Handler handler=new Handler(){
        //0：提示出错了 1：提示未打开连接
        @Override
        public void handleMessage(Message msg) {
            //2：更新状态
            switch (msg.what){
                case 0:
                    Toast.makeText(getApplicationContext(), "哎呀，出错了。。。", Toast.LENGTH_LONG).show();
                    break;
                case 1:
                    Toast.makeText(getApplicationContext(), "网络未连接", Toast.LENGTH_LONG).show();
                    break;
                case 2:
                    Toast.makeText(getApplicationContext(), "接收任务成功", Toast.LENGTH_SHORT).show();
                    break;
                case 3:
                    Toast.makeText(getApplicationContext(), "接收任务失败", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };
    //加载信息----开始-----
    public void initViewData(){
        new Thread(){
            @Override
            public void run() {
                taskinfoModel= (TaskInfoModel) getIntent().getSerializableExtra("taskinfo");
                textView_tv_taskinfo_author.setText(taskinfoModel.getTask_author());
                textView_tv_taskinfo_taskname.setText(taskinfoModel.getTask_name());
                textView_tv_taskinfo_tasktype.setText(taskinfoModel.getTask_type());
                textView_tv_taskinfo_taskinfo.setText(taskinfoModel.getTask_info());
                textView_taskinfo_status.setText(taskinfoModel.getTask_status());
                taskInfo_status=taskinfoModel.getTask_status();
                if (taskinfoModel.getTask_status().equals("待接")){ //若此时任务为待接任务，则显示接收任务按钮
                    button__taskinfo_paifa.setText("接收任务");
                    button__taskinfo_commit.setVisibility(View.GONE);//设置按钮隐藏
                }
                textView_taskinfo_starttime.setText(taskinfoModel.getTask_startdate());
                textView_taskinfo_endtime.setText(taskinfoModel.getTask_enddate());
                textView_taskinfo_remarks.setText(taskinfoModel.getRemarks());
            }
        }.start();
    }
    //加载信息----结束-----
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_taskinfobanzhang_commit:
                break;
            case R.id.btn_taskinfobanzhang_paifa:
                paifa();
                break;
        }
    }
    //派发按钮函数：任务状态为待接时，弹出派发个人界面
    public void paifa(){
        System.out.println("--------------------");
        if (taskInfo_status.equals("待接")&&button__taskinfo_paifa.getText().equals("接收任务")){
            new AlertDialog.Builder(this).setTitle("提示")//设置对话框标题
                    .setMessage("请确认是否接收任务！")//设置显示的内容
                    .setPositiveButton("确定",new DialogInterface.OnClickListener() {//添加确定按钮
                        @Override
                        public void onClick(DialogInterface dialog, int which) {//确定按钮的响应事件
                            Context context = getApplicationContext();
                            NetWorkUtils netWorkUtils=new NetWorkUtils();
                            // 获取手机所有连接管理对象（包括对wi-fi,net等连接的管理）
                            netWorkUtils.isNetworkConnected(context);
                            if(netWorkUtils.isNetworkConnected(context)==false){ //若网络未连接
                                Toast.makeText(getApplicationContext(), "网络未连接", Toast.LENGTH_LONG).show();
                            }else{ //若网络已连接，更新订单状态并添加信息
                                try {
                                        TaskScheduleModel taskScheduleModel = new TaskScheduleModel();
                                        taskScheduleModel.setTaskinfo_id(taskinfoModel.getId());//任务id
                                        taskScheduleModel.setUserinfo_id(PublicShareUserinfo.id);//用户id
                                        taskScheduleModel.setUnit(PublicShareUserinfo.unit);//用户单位
                                        taskScheduleModel.setTruename(PublicShareUserinfo.truename); //用户真实姓名
                                        taskScheduleModel.setStatus("已接收"); //任务状态
                                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                                        String currentDate = simpleDateFormat.format(new Date());
                                        taskScheduleModel.setTakedate(currentDate); //接收时间
                                        Gson gson = new Gson();
                                        String jsontaskScheduleModel = gson.toJson(taskScheduleModel);
                                    //增加一条信息，并更新taskinfo状态
                                        AddDataToServer addDataToServer = new AddDataToServer("http://10.0.2.2:8080/TaskTrackingService/insertTaskSchedule.do",jsontaskScheduleModel);
                                        if (addDataToServer.getContent().equals("error")) {
                                            handler.sendEmptyMessage(0);//发送消息到handler，提示出错了
                                        } else if (addDataToServer.getContent().equals("1")) {
                                            handler.sendEmptyMessage(2);//发送消息到handler，提示接收任务成功
                                            button__taskinfo_paifa.setText("派发任务");
                                        }else if (addDataToServer.getContent().equals("0")) {
                                            handler.sendEmptyMessage(3);//发送消息到handler，提示接收任务失败
                                        }
                                } catch (Exception e) {
                                    Toast.makeText(getApplicationContext(), "哎呀，出错了。。。", Toast.LENGTH_LONG).show();
                                    e.printStackTrace();
                                }
                            }
                        }
                    }).setNegativeButton("返回",new DialogInterface.OnClickListener() {//添加返回按钮
                @Override
                public void onClick(DialogInterface dialog, int which) {//响应事件
                    // TODO Auto-generated method stub
                }
            }).show();//在按键响应事件中显示此对话框
        }else if(button__taskinfo_paifa.getText().equals("派发任务")){
            AlertDialog.Builder builder = new AlertDialog.Builder(TaskInfoBanZhangActivity.this);
            builder.setTitle("爱好");
            final String[] hobbies = {"篮球", "足球", "网球", "斯诺克"};
            //    设置一个单项选择下拉框
            /**
             * 第一个参数指定我们要显示的一组下拉多选框的数据集合
             * 第二个参数代表哪几个选项被选择，如果是null，则表示一个都不选择，如果希望指定哪一个多选选项框被选择，
             * 需要传递一个boolean[]数组进去，其长度要和第一个参数的长度相同，例如 {true, false, false, true};
             * 第三个参数给每一个多选项绑定一个监听器
             */
            builder.setMultiChoiceItems(hobbies, null, new DialogInterface.OnMultiChoiceClickListener()
            {
                StringBuffer sb = new StringBuffer(100);
                @Override
                public void onClick(DialogInterface dialog, int which, boolean isChecked)
                {
                    if(isChecked)
                    {
                        sb.append(hobbies[which] + ", ");
                    }
                    Toast.makeText(TaskInfoBanZhangActivity.this, "爱好为：" + sb.toString(), Toast.LENGTH_SHORT).show();
                }
            });
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {

                }
            });
            builder.setNegativeButton("取消", new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {

                }
            });
            builder.show();
        }
    }

}
