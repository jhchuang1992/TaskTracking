package com.edu.ouc.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.edu.ouc.function.AutoMaticLogin;
import com.edu.ouc.function.NetWorkUtils;
import com.edu.ouc.function.SelectDataFromServer;
import com.edu.ouc.function.SysApplication;
import com.edu.ouc.model.PublicShareUserinfo;
import com.edu.ouc.model.UserInfoModel;
import com.edu.ouc.tasktracking.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by JHC on 2017/11/22.
 * 登录界面
 */

public class LoginActivity extends Activity implements View.OnClickListener {
    private Button button_login;
    private EditText editText_username;
    private EditText editText_password;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        SysApplication.getInstance().addActivity(this);
        editText_username=(EditText)findViewById(R.id.edt_username);
        editText_password=(EditText)findViewById(R.id.edt_password);
        button_login=(Button) findViewById(R.id.btn_login);
        button_login.setOnClickListener(this);
       /* ConnectServer connectServer=new ConnectServer("http://10.0.2.2:8080/TaskTrackingService/getUserInfo.do?");
        try {
            JSONObject jsonObject=new JSONObject(connectServer.getContent());
            JSONArray jsonArray=jsonObject.getJSONArray("data");
            List<UserInfoModel> userInfoModelList=new ArrayList<UserInfoModel>();
            //这两句代码必须的，为的是初始化出来gson这个对象，才能拿来用
            Type type1=new TypeToken<List<UserInfoModel>>(){}.getType();
            userInfoModelList=new Gson().fromJson(jsonArray.toString(),type1);
            System.out.println("----1212-----"+userInfoModelList.size());
        } catch (JSONException e) {
            e.printStackTrace();
        }*/

    }
    //点击事件公共函数
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_login:  //登录按钮
                LoginFunction();
                break;
        }

    }
    //登录函数
    private void LoginFunction() {
        NetWorkUtils netWorkUtils=new NetWorkUtils();
        // 获取手机所有连接管理对象（包括对wi-fi,net等连接的管理）
        Context context = this.getApplicationContext();
        netWorkUtils.isNetworkConnected(context);
        if(netWorkUtils.isNetworkConnected(context)==false){ //若网络未连接
            Toast.makeText(getApplicationContext(), "网络未连接", Toast.LENGTH_LONG).show();
        }else{ //若网络已连接，则判断用户名和密码是否都不为空
            if (isUserNameAndPwdValid()){//判断用户名和密码是否都为空
                handler.post(runnable);
            }
        }
    }
    /**
     * 判断用户名和密码是否有效
     * @return
     */
    public boolean isUserNameAndPwdValid() {
        // 用户名和密码不得为空
        if (editText_username.getText().toString().trim().equals("")) {
            Toast.makeText(this, getString(R.string.accountName_empty),
                    Toast.LENGTH_SHORT).show();
            return false;
        } else if (editText_password.getText().toString().trim().equals("")) {
            Toast.makeText(this, getString(R.string.accountpassword_empty),
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
    Handler handler=new Handler();
    Runnable runnable=new Runnable(){
        public void run(){
            try {
       SelectDataFromServer selectDataFromServer=new SelectDataFromServer("http://10.0.2.2:8080/TaskTrackingService/login.do?name="+editText_username.getText().toString().trim()+"&pwd="+editText_password.getText().toString().trim());
                System.out.println(selectDataFromServer.getContent());
                 JSONObject jsonObject=new JSONObject(selectDataFromServer.getContent());
            JSONArray jsonArray=jsonObject.getJSONArray("data");
            List<UserInfoModel> userInfoModelList=new ArrayList<UserInfoModel>();
            //这两句代码必须的，为的是初始化出来gson这个对象，才能拿来用
            Type type1=new TypeToken<List<UserInfoModel>>(){}.getType();
            userInfoModelList=new Gson().fromJson(jsonArray.toString(),type1);
                if(selectDataFromServer.getContent().equals("error")){
                Toast.makeText(getApplicationContext(), "哎呀，出错了。。。", Toast.LENGTH_SHORT).show();
            }else if (userInfoModelList.size()==0){
                Toast.makeText(getApplicationContext(), "用户名或密码不正确", Toast.LENGTH_SHORT).show();
            }else{
                    String userName = editText_username.getText().toString();
                    String userPwd = editText_password.getText().toString();
                    AutoMaticLogin.getInstance().saveUserInfo(LoginActivity.this,userInfoModelList.get(0).getId(),userName, userPwd,userInfoModelList.get(0).getTruename(),userInfoModelList.get(0).getRole(),userInfoModelList.get(0).getUnit());
                    PublicShareUserinfo.id=userInfoModelList.get(0).getId();
                    PublicShareUserinfo.lgname=userInfoModelList.get(0).getLgname();
                    PublicShareUserinfo.lgpwd=userInfoModelList.get(0).getLgpwd();
                    PublicShareUserinfo.truename=userInfoModelList.get(0).getTruename();
                    PublicShareUserinfo.role=userInfoModelList.get(0).getRole();
                    PublicShareUserinfo.unit=userInfoModelList.get(0).getUnit();
                    Toast.makeText(getApplicationContext(), "登录成功", Toast.LENGTH_SHORT).show();
                    //实现界面的跳转到主页面
                    Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                    startActivity(intent);
                    //关闭当前界面
                    finish();
                }
            /*JSONObject jsonObject=new JSONObject(connectServer.getContent());
            JSONArray jsonArray=jsonObject.getJSONArray("data");
            List<UserInfoModel> userInfoModelList=new ArrayList<UserInfoModel>();
            //这两句代码必须的，为的是初始化出来gson这个对象，才能拿来用
            Type type1=new TypeToken<List<UserInfoModel>>(){}.getType();
            userInfoModelList=new Gson().fromJson(jsonArray.toString(),type1);
            System.out.println("----1212-----"+userInfoModelList.size());*/
        } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "哎呀，出错了。。。", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        }
    };

    @Override
    protected void onDestroy() {
        handler.removeCallbacks(runnable);//移除Handler
        super.onDestroy();
    }
    //两次返回键退出程序----开始-----
    // 定义一个变量，来标识是否退出
    private static boolean isExit = false;
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            isExit = false;
        }
    };
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            SysApplication.getInstance().exit();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }
    //如果在发送消息间隔的2秒内，再次按了BACK键，则再次执行exit方法，
    private void exit() {
        if (!isExit) {
            isExit = true;
            Toast.makeText(getApplicationContext(), "再按一次退出程序",
                    Toast.LENGTH_SHORT).show();
            // 利用handler延迟发送更改状态信息
            mHandler.sendEmptyMessageDelayed(0, 2000);
        } else {
            finish();
            System.exit(0);
        }
    }
    //两次返回键退出程序----结束-----
}
