package com.edu.ouc.activity;

import android.app.ActionBar;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.edu.ouc.adapter.ItemPaiFaAdapter;
import com.edu.ouc.datetimepicker.DateUtils;
import com.edu.ouc.datetimepicker.JudgeDate;
import com.edu.ouc.datetimepicker.ScreenInfo;
import com.edu.ouc.datetimepicker.WheelMain;
import com.edu.ouc.function.AddDataToServer;
import com.edu.ouc.function.NetWorkUtils;
import com.edu.ouc.function.SelectDataFromServer;
import com.edu.ouc.listview.PaiFaListView;
import com.edu.ouc.model.TaskInfoModel;
import com.edu.ouc.model.TaskScheduleModel;
import com.edu.ouc.model.UserInfoModel;
import com.edu.ouc.tasktracking.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by JHC on 2017/11/28.
 * 派发任务给职工界面
 */

public class PaiFaActivity extends AppCompatActivity implements View.OnClickListener{
    private PaiFaListView mListView;
    private PaiFaListView listView_mListView;
    private CheckBox mMainCkb;
    private List<UserInfoModel> userInfoModelList;
    private ItemPaiFaAdapter mMyAdapter;
    private ScrollView scrollView_paifa;
    private TextView textView_paifa_endTime; //添加截止时间
    private EditText editText_paifa_remarks;  //派发给个人的备注
    //监听来源
    public boolean mIsFromItem = false;
    private Button button_paifa_paifa; //派发任务按钮
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paifa);
        mListView=(PaiFaListView)findViewById(R.id.list_main);
        mMainCkb=(CheckBox)findViewById(R.id.ckb_main);
        scrollView_paifa=(ScrollView)findViewById(R.id.scv_paifa); //滚动条
        listView_mListView=(PaiFaListView)findViewById(R.id.list_main);
        button_paifa_paifa=(Button)findViewById(R.id.btn_paifa_paifa);
        button_paifa_paifa.setOnClickListener(this);
        textView_paifa_endTime=(TextView)findViewById(R.id.tv_paifa_endTime);
        textView_paifa_endTime.setOnClickListener(this);
        editText_paifa_remarks=(EditText)findViewById(R.id.edt_paifa_remarks);
        initData();

    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_paifa_endTime:
                showBottoPopupWindow();
                break;
            case R.id.btn_paifa_paifa:
                paifa_task();
                break;
        }
    }
    Handler handler=new Handler(){
        //0：提示出错了 1：提示未打开连接 2:加载部门下拉框适配器 3：新建任务成功  4：新建任务失败
        @Override
        public void handleMessage(Message msg) {
            //0:报错  1：网络未连接
            switch (msg.what){
                case 0:
                    Toast.makeText(getApplicationContext(), "哎呀，出错了。。。", Toast.LENGTH_LONG).show();
                    break;
                case 1:
                    Toast.makeText(getApplicationContext(), "网络未连接", Toast.LENGTH_LONG).show();
                    break;

            }
        }
    };
    /**
     * 数据加载
     */
    private void initData() {

        new Thread(){
            @Override
            public void run() {
                try {
                    userInfoModelList = new ArrayList<UserInfoModel>();
                    NetWorkUtils netWorkUtils=new NetWorkUtils();
                    // 获取手机所有连接管理对象（包括对wi-fi,net等连接的管理）
                    netWorkUtils.isNetworkConnected(getApplicationContext());
                    if(netWorkUtils.isNetworkConnected(getApplicationContext())==false){ //若网络未连接
                        handler.sendEmptyMessage(1);//发送消息到handler，提示未连接网络
                    }else{ //若网络已连接，则判断用户名和密码是否都不为空192.164.2.102
                        SelectDataFromServer selectDataFromServer=new SelectDataFromServer("http://10.0.2.2:8080/TaskTrackingService/getUserInfo.do?sql=where+unit='材料部'");
                        if(selectDataFromServer.getContent().equals("error")){
                            handler.sendEmptyMessage(0);//发送消息到handler，提示出错了
                        }else{
                            JSONObject jsonObject=new JSONObject(selectDataFromServer.getContent());
                            JSONArray jsonArray=jsonObject.getJSONArray("data");
                            //这两句代码必须的，为的是初始化出来gson这个对象，才能拿来用
                            Type type1=new TypeToken<List<UserInfoModel>>(){}.getType();
                            userInfoModelList=new Gson().fromJson(jsonArray.toString(),type1);
                            System.out.println(userInfoModelList.size());
                            initViewOper();
                        }
                    }
                } catch (Exception e) {
                    handler.sendEmptyMessage(0);//发送消息到handler，提示出错了
                    e.printStackTrace();
                }
            }
        }.start();
    }
    //数据绑定
    private void initViewOper() {
        mMyAdapter = new ItemPaiFaAdapter(userInfoModelList, this, new AllCheckListener() {
            @Override
            public void onCheckedChanged(boolean b) {
                //根据不同的情况对maincheckbox做处理
                if (!b && !mMainCkb.isChecked()) {
                    return;
                } else if (!b && mMainCkb.isChecked()) {
                    mIsFromItem = true;
                    mMainCkb.setChecked(false);
                } else if (b) {
                    mIsFromItem = true;
                    mMainCkb.setChecked(true);
                }
            }
        });
        mListView.setAdapter(mMyAdapter);
        //设置scrollview初始化后滑动到顶部，必须在gridview填充数据之后，否则无法实现预期效果
        scrollView_paifa.smoothScrollTo(0,20);
        scrollView_paifa.setFocusable(true);
        //全选的点击监听
        mMainCkb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                //当监听来源为点击item改变maincbk状态时不在监听改变，防止死循环
                if (mIsFromItem) {
                    mIsFromItem = false;
                    return;
                }
                //改变数据
                for (UserInfoModel model : userInfoModelList) {
                    model.setIscheck(b);
                }
                //刷新listview
                mMyAdapter.notifyDataSetChanged();
            }
        });
    }

    //对item导致maincheckbox改变做监听
    public interface AllCheckListener {
        void onCheckedChanged(boolean b);
    }
    //选择时间时间---开始----
    private String beginTime;
    private WheelMain wheelMainDate;
    public void showBottoPopupWindow() {
        WindowManager manager = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
        Display defaultDisplay = manager.getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        defaultDisplay.getMetrics(outMetrics);
        int width = outMetrics.widthPixels;
        View menuView = LayoutInflater.from(this).inflate(R.layout.datetime_select__window,null);
        final PopupWindow mPopupWindow = new PopupWindow(menuView, (int)(width*0.8),
                ActionBar.LayoutParams.WRAP_CONTENT);
        ScreenInfo screenInfoDate = new ScreenInfo(this);
        wheelMainDate = new WheelMain(menuView, true);
        wheelMainDate.screenheight = screenInfoDate.getHeight();
        String time = DateUtils.currentMonth().toString();
        Calendar calendar = Calendar.getInstance();
        if (JudgeDate.isDate(time, "yyyy-MM-DD")) {
            try {
                calendar.setTime(new Date(time));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hours = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        wheelMainDate.initDateTimePicker(year, month, day, hours,minute);
        mPopupWindow.setAnimationStyle(R.style.AnimationPreview);
        mPopupWindow.setTouchable(true);
        mPopupWindow.setFocusable(true);
        mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
        mPopupWindow.showAtLocation(mListView, Gravity.CENTER, 0, 0);
        mPopupWindow.setOnDismissListener(new poponDismissListener());
        backgroundAlpha(0.6f);
        TextView tv_cancle = (TextView) menuView.findViewById(R.id.tv_cancle);
        TextView tv_ensure = (TextView) menuView.findViewById(R.id.tv_ensure);
        TextView tv_pop_title = (TextView) menuView.findViewById(R.id.tv_pop_title);
        tv_pop_title.setText("选择起始时间");
        tv_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                mPopupWindow.dismiss();
                backgroundAlpha(1f);
            }
        });
        tv_ensure.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                beginTime = wheelMainDate.getTime().toString();
                textView_paifa_endTime.setText(DateUtils.formateStringH(beginTime,DateUtils.yyyyMMddHHmm));
                mPopupWindow.dismiss();
                backgroundAlpha(1f);
            }
        });
    }
    public void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = bgAlpha;
        getWindow().setAttributes(lp);
    }
    class poponDismissListener implements PopupWindow.OnDismissListener {
        @Override
        public void onDismiss() {
            backgroundAlpha(1f);
        }
    }
    //选择时间时间---结束----
    //派发任务---开始---
    public void  paifa_task(){
        try {
            NetWorkUtils netWorkUtils = new NetWorkUtils();
            // 获取手机所有连接管理对象（包括对wi-fi,net等连接的管理）
            netWorkUtils.isNetworkConnected(getApplicationContext());
            if (netWorkUtils.isNetworkConnected(getApplicationContext()) == false) { //若网络未连接
                handler.sendEmptyMessage(1);//发送消息到handler，提示未连接网络
            } else { //若网络已连接，则判断指定人员和截止时间是否都已经选择
                //获取当前时间
                SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm");
                long minuteInterrval=0;//间隔分钟
                if (userInfoModelList.size()==0) {
                    Toast.makeText(this, "您部门下还没有职员，无法派发任务", Toast.LENGTH_SHORT).show();
                }else if(!userInfoModelList.contains("true")) {
                    Toast.makeText(this, "请选择委派的员工", Toast.LENGTH_SHORT).show();
                } else if (textView_paifa_endTime.getText().toString().trim().equals("请选择时间")){
                    Toast.makeText(this, "请选择截止时间", Toast.LENGTH_SHORT).show();
                } else{
                    try {
                        String currentDate=simpleDateFormat.format(new Date());
                        Date nowDate=simpleDateFormat.parse(currentDate);
                        Date endDate=simpleDateFormat.parse(textView_paifa_endTime.getText().toString());
                        minuteInterrval=(endDate.getTime()-nowDate.getTime())/(1000*60);//间隔分钟，若小于0，则说明结束日期早于当前日期
                    } catch (ParseException e) {
                        Toast.makeText(this, "时间转换失败，请告知管理员", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                    try {
                        if (minuteInterrval<=60){
                            Toast.makeText(this, "任务时长最少为1小时", Toast.LENGTH_SHORT).show();
                        }else{  //将分配的信息更新到表中
                            TaskScheduleModel taskscheduleLast = (TaskScheduleModel) getIntent().getSerializableExtra("taskschedule");
                            String take_person=","; //格式：,5,10,2，
                            String confirm=","; //格式：,5：0,10：0,2：0，
                            for (int i=0;i<userInfoModelList.size();i++){
                                if(userInfoModelList.get(i).ischeck()){  //若此员工被选中
                                    take_person=take_person+String.valueOf(userInfoModelList.get(i).getId())+",";
                                    confirm=confirm+String.valueOf(userInfoModelList.get(i).getId())+":0,";
                                }
                            }
                            if (netWorkUtils.isNetworkConnected(getApplicationContext()) == false) { //若网络未连接
                                handler.sendEmptyMessage(1);//发送消息到handler，提示未连接网络
                            } else {
                                TaskScheduleModel taskScheduleModel = new TaskScheduleModel();
                                taskScheduleModel.setId(taskscheduleLast.getId());
                                taskScheduleModel.setStatus("已派发");
                                taskScheduleModel.setTake_person(take_person); //派发给个人的id
                                taskScheduleModel.setConfirm(confirm); //个人确认状态
                                taskScheduleModel.setTake_person_date(simpleDateFormat.format(new Date())); //派发给个人的时间
                                taskScheduleModel.setTask_endDate(textView_paifa_endTime.getText().toString());  //派发给个人的截止时间
                                taskScheduleModel.setPerson_remarks(editText_paifa_remarks.getText().toString().trim()); //派发给个人的备注
                                Gson gson = new Gson();
                                String jsontaskScheduleModel = gson.toJson(taskScheduleModel);
                                AddDataToServer addDataToServer = new AddDataToServer("http://10.0.2.2:8080/TaskTrackingService/updateTaskSchedule.do",jsontaskScheduleModel);
                                if (addDataToServer.getContent().equals("error")) {
                                    handler.sendEmptyMessage(0);//发送消息到handler，提示出错了
                                } else if (addDataToServer.getContent().equals("1")) {
                                    Toast.makeText(this, "派发任务成功", Toast.LENGTH_SHORT).show();
                                }else if (addDataToServer.getContent().equals("0")) {
                                    Toast.makeText(this, "派发任务失败", Toast.LENGTH_SHORT).show();
                                }


                            }

                            }
                    } catch (Exception e) { //若出现异常，肯定是因为部门没有加载进来
                        Toast.makeText(this, "哎呀，出错了。。。", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }

                }
            }
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "哎呀，出错了。。。", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }



    //派发任务---结束
}