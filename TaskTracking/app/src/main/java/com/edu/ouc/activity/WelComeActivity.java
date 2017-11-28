package com.edu.ouc.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;

import java.util.Timer;
import java.util.TimerTask;

import com.edu.ouc.function.AutoMaticLogin;
import com.edu.ouc.function.SysApplication;
import com.edu.ouc.model.PublicShareUserinfo;
import com.edu.ouc.tasktracking.R;

/**
 * Created by JHC on 2017/11/23.
 * 欢迎界面
 */

public class WelComeActivity extends Activity {
    private static final int GO_HOME = 0;//去主页
    private static final int GO_LOGIN = 1;//去登录页
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        SysApplication.getInstance().addActivity(this);
        getHomeActivity(this);
    }
    //设置停留两秒之后再跳转
    private void getHomeActivity(final Context context) {
        Timer timer=new Timer();
        TimerTask task=new TimerTask(){
            public void run(){
                if (AutoMaticLogin.getInstance().hasUserInfo(context))//自动登录判断，SharePrefences中有数据，则跳转到主页，没数据则跳转到登录页
                {
                    PublicShareUserinfo.id=AutoMaticLogin.getInstance().getUserInfo(context).getId();
                    PublicShareUserinfo.lgname=AutoMaticLogin.getInstance().getUserInfo(context).getLgname();
                    PublicShareUserinfo.lgpwd=AutoMaticLogin.getInstance().getUserInfo(context).getLgpwd();
                    PublicShareUserinfo.truename=AutoMaticLogin.getInstance().getUserInfo(context).getTruename();
                    PublicShareUserinfo.role=AutoMaticLogin.getInstance().getUserInfo(context).getRole();
                    PublicShareUserinfo.unit=AutoMaticLogin.getInstance().getUserInfo(context).getUnit();
                    mHandler.sendEmptyMessageDelayed(GO_HOME, 2000);
                } else {
                    mHandler.sendEmptyMessageAtTime(GO_LOGIN, 2000);
                }
            }
        };
        timer.schedule(task, 2000);
    }
    /**
     * 跳转判断
     */
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case GO_HOME://去主页
                    Intent intent = new Intent(WelComeActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                    break;
                case GO_LOGIN://去登录页
                    Intent intent2 = new Intent(WelComeActivity.this, LoginActivity.class);
                    startActivity(intent2);
                    finish();
                    break;
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
