package com.edu.ouc.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.edu.ouc.adapter.SpinnerArrayAdapter;
import com.edu.ouc.datetimepicker.DateUtils;
import com.edu.ouc.datetimepicker.JudgeDate;
import com.edu.ouc.datetimepicker.ScreenInfo;
import com.edu.ouc.datetimepicker.WheelMain;
import com.edu.ouc.dialog.UpLoadingDialog;
import com.edu.ouc.function.AddDataToServer;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by JHC on 2017/11/24.
 * 新建任务-界面
 */

public class NewTaskActivity extends Activity implements View.OnClickListener {
    private UpLoadingDialog dialog; //弹框加载中
    private EditText editText_newtask_taskName,editText_newtask_taskinfo,editText_newtask_remarks;
    private RadioButton radioButton_newtask_hezuo,radioButton_newtask_time;
    private TextView textView_newtask_endTime;
    private TextView tv_center; //由于控制时间选择控件显示位置
    private Spinner spinner_newtask_unit;
    private Button buttonnewtask_paifa;
    private boolean connectNet=false; //是否联网成功
    private Context context;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newtask);
        editText_newtask_taskName=(EditText)findViewById(R.id.edt_newTask_taskname);//任务名称
        editText_newtask_taskinfo=(EditText)findViewById(R.id.edt_newTask_taskinfo);//任务详情
        editText_newtask_remarks=(EditText)findViewById(R.id.edt_newTask_remarks);//备注信息
        radioButton_newtask_hezuo=(RadioButton)findViewById(R.id.rb_newtask_hezuo);//任务类型-合作
        radioButton_newtask_time=(RadioButton)findViewById(R.id.rb_newtask_time); //任务类型-协同
        textView_newtask_endTime=(TextView)findViewById(R.id.tv_newtask_endTime);//截止时间
        textView_newtask_endTime.setOnClickListener(this);
        tv_center=(TextView)findViewById(R.id.edt_newTask_taskinfo);//由于控制时间选择控件显示位置
        spinner_newtask_unit=(Spinner)findViewById(R.id.sn_newTask_unit);//接收部门
        buttonnewtask_paifa=(Button)findViewById(R.id.btn_newtask_paifa);
        buttonnewtask_paifa.setOnClickListener(this);
        addUnitList();//查询部门list
        context=this;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_newtask_endTime:
                showBottoPopupWindow();
                break;
            case R.id.btn_newtask_paifa:
                tasktake(); //派发任务
                break;
        }
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
        mPopupWindow.showAtLocation(tv_center, Gravity.CENTER, 0, 0);
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
                textView_newtask_endTime.setText(DateUtils.formateStringH(beginTime,DateUtils.yyyyMMddHHmm));
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
    //填充部门列表---开始----
    ArrayAdapter<String> adapter_unit;
    List<String> datas=new ArrayList<String>();
    Handler handler=new Handler(){
      //0：提示出错了 1：提示未打开连接 2:加载部门下拉框适配器 3：新建任务成功  4：新建任务失败
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0:
                    Toast.makeText(context, "哎呀，出错了。。。", Toast.LENGTH_LONG).show();
                    break;
                case 1:
                    Toast.makeText(context, "网络未连接", Toast.LENGTH_LONG).show();
                    break;
                case 2:
                    spinner_newtask_unit.setAdapter(adapter_unit);
                    break;
                case 3:
                    Toast.makeText(context, "任务发布成功", Toast.LENGTH_SHORT).show();
                    finish();
                    break;
                case 4:
                    Toast.makeText(context, "任务发布失败", Toast.LENGTH_SHORT).show();
                    break;
                case 5:
                    dialog = new UpLoadingDialog(context);
                    dialog.setCancelable(false); //设置这个对话框不能被用户按[返回键]而取消掉
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.setDialogText("派发任务中......");
                    dialog.show();
                    break;
                case 6:
                    dialog.dismiss();
                    break;
            }
        }
    };
    public void addUnitList(){
        new Thread(){
            @Override
            public void run() {
                        try {
                            NetWorkUtils netWorkUtils=new NetWorkUtils();
                            // 获取手机所有连接管理对象（包括对wi-fi,net等连接的管理）
                            netWorkUtils.isNetworkConnected(context);
                            if(netWorkUtils.isNetworkConnected(context)==false){ //若网络未连接
                                handler.sendEmptyMessage(1);//发送消息到handler，提示未连接网络
                            }else{ //若网络已连接，则判断用户名和密码是否都不为空192.164.2.102
                                SelectDataFromServer selectDataFromServer=new SelectDataFromServer("getUnitFormUserInfo.do?sql=where+role='班长'");
                                if(selectDataFromServer.getContent().equals("error")){
                                    handler.sendEmptyMessage(0);//发送消息到handler，提示出错了
                                }else if (selectDataFromServer.getContent().equals("@")) { //若数据为空
                                }else{
                                    connectNet=true;  //联网成功
                                    JSONObject jsonObject=new JSONObject(selectDataFromServer.getContent());
                                    JSONArray jsonArray=jsonObject.getJSONArray("data");
                                    //这两句代码必须的，为的是初始化出来gson这个对象，才能拿来用
                                    Type type1=new TypeToken<List<String>>(){}.getType();
                                    datas=new Gson().fromJson(jsonArray.toString(),type1);
                                    datas.add("全体");
                                    //适配器
                                    adapter_unit = new SpinnerArrayAdapter(context, datas);
                                    //设置样式
                                    adapter_unit.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                    //加载适配器
                                    handler.sendEmptyMessage(2);//发送消息到handler，告知其加载适配器
                                }
                            }
                        } catch (Exception e) {
                            handler.sendEmptyMessage(0);//发送消息到handler，提示出错了
                            e.printStackTrace();
                        }
            }
        }.start();
    }
    //获取部门列表---结束----
    //派发任务-----------开始------
    public void tasktake(){
        if(isStandard()&&connectNet){ //若符合规范且网络正常，则开始插入数据库
            new Thread(){
                @Override
                public void run() {
                    try {
                        NetWorkUtils netWorkUtils = new NetWorkUtils();
                        // 获取手机所有连接管理对象（包括对wi-fi,net等连接的管理）
                        netWorkUtils.isNetworkConnected(context);
                        if (netWorkUtils.isNetworkConnected(context) == false) { //若网络未连接
                            handler.sendEmptyMessage(1);//发送消息到handler，提示未连接网络
                        } else { //若网络已连接
                            TaskInfoModel taskInfoModel = new TaskInfoModel();
                            taskInfoModel.setTask_name(editText_newtask_taskName.getText().toString().trim());//任务名称
                            taskInfoModel.setTask_info(editText_newtask_taskinfo.getText().toString().trim());//任务详情
                            taskInfoModel.setTask_author(AutoMaticLogin.getInstance().getUserInfo(context).getTruename()); //发布者
                            taskInfoModel.setTask_status("待接"); //任务状态
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                            String currentDate = simpleDateFormat.format(new Date());
                            taskInfoModel.setTask_startdate(currentDate); //任务开始时间
                            taskInfoModel.setTask_enddate(textView_newtask_endTime.getText().toString()); //任务截止时间
                            taskInfoModel.setRemarks(editText_newtask_remarks.getText().toString().trim());//备注信息
                            taskInfoModel.setTake_unit(spinner_newtask_unit.getSelectedItem().toString().trim());//派发的部门
                            taskInfoModel.setConfirm("0");//等待确认，默认为不确认。全体部门，若有一个部门确认，则确认
                            if (radioButton_newtask_hezuo.isChecked()) {
                                taskInfoModel.setTask_type("分时"); //任务类型
                            } else {
                                taskInfoModel.setTask_type("同时"); //任务类型
                            }
                            taskInfoModel.setTask_lgname(AutoMaticLogin.getInstance().getUserInfo(context).getLgname()); //任务发布者的登录名
                            Gson gson = new Gson();
                            String jsontaskInfoModel = gson.toJson(taskInfoModel);
                           handler.sendEmptyMessage(5); //显示弹框
                            AddDataToServer addDataToServer = new AddDataToServer("insertTaskInfo.do",jsontaskInfoModel);
                            if (addDataToServer.getContent().equals("error")) {
                                handler.sendEmptyMessage(6); //取消弹框
                                handler.sendEmptyMessage(0);//发送消息到handler，提示出错了
                            } else if (addDataToServer.getContent().equals("1")) {
                                handler.sendEmptyMessage(6); //取消弹框
                                handler.sendEmptyMessage(3);//发送消息到handler，提示新建任务成功
                            }else if (addDataToServer.getContent().equals("0")) {
                                handler.sendEmptyMessage(6); //取消弹框
                                handler.sendEmptyMessage(4);//发送消息到handler，提示新建任务失败
                            }
                        }
                    } catch (Exception e) {
                        handler.sendEmptyMessage(0);//发送消息到handler，提示出错了
                        e.printStackTrace();
                    }
                }
            }.start();
        }
    }
    //判断逻辑是否正确
    public boolean isStandard(){
        //获取当前时间
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm");
        long minuteInterrval=0;//间隔分钟
        if (editText_newtask_taskName.getText().toString().trim().equals("")) {
            Toast.makeText(this, "请将任务填写完整", Toast.LENGTH_SHORT).show();
            return false;
        }else if (editText_newtask_taskName.getText().toString().trim().length()<1||editText_newtask_taskName.getText().toString().trim().length()>20) {
            Toast.makeText(this, "任务名称应在1-20个字符之间", Toast.LENGTH_SHORT).show();
            return false;
        }else if (editText_newtask_taskinfo.getText().toString().trim().length()>200) {
            Toast.makeText(this, "任务详情应在0-200个字符之间", Toast.LENGTH_SHORT).show();
            return false;
        }else if (editText_newtask_remarks.getText().toString().trim().length()>200) {
            Toast.makeText(this, "备注信息应在200个字符之内", Toast.LENGTH_SHORT).show();
            return false;
        }else if (textView_newtask_endTime.getText().toString().trim().equals("请选择时间")){
            Toast.makeText(this, "请选择截止时间", Toast.LENGTH_SHORT).show();
            return false;
        } else{
            try {
                String currentDate=simpleDateFormat.format(new Date());
                Date nowDate=simpleDateFormat.parse(currentDate);
                Date endDate=simpleDateFormat.parse(textView_newtask_endTime.getText().toString());
                minuteInterrval=(endDate.getTime()-nowDate.getTime())/(1000*60);//间隔分钟，若小于0，则说明结束日期早于当前日期
            } catch (ParseException e) {
                Toast.makeText(this, "时间转换失败，请告知管理员", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
            try {
                if (minuteInterrval<=60){
                    Toast.makeText(this, "任务时长最少为1小时", Toast.LENGTH_SHORT).show();
                    return false;
                }else if(radioButton_newtask_hezuo.isChecked()&&spinner_newtask_unit.getSelectedItem().toString().trim().equals("全体")){
                    Toast.makeText(this, "类型为分时的任务需指定单个部门", Toast.LENGTH_SHORT).show();
                    return false;
                } else if(radioButton_newtask_time.isChecked()&&!spinner_newtask_unit.getSelectedItem().toString().trim().equals("全体")){
                    Toast.makeText(this, "类型为同时的任务需选择全体部门", Toast.LENGTH_SHORT).show();
                    return false;
                }
            } catch (Exception e) { //若出现异常，肯定是因为部门没有加载进来
                Toast.makeText(this, "哎呀，出错了。。。", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }

        }
        return true;
    }
    //派发任务-----结束
}
