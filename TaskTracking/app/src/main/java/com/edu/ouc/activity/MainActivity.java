package com.edu.ouc.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.edu.ouc.adapter.MyFragmentPagerAdapter;
import com.edu.ouc.fragment.ChatFragment;
import com.edu.ouc.fragment.MeFragment;
import com.edu.ouc.fragment.PlanFragment;
import com.edu.ouc.fragment.TaskFragment;
import com.edu.ouc.function.SysApplication;
import com.edu.ouc.tasktracking.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JHC on 2017/11/23.
 * 主界面
 */

public class MainActivity extends  FragmentActivity implements RadioGroup.OnCheckedChangeListener,ViewPager.OnPageChangeListener{

    private ViewPager viewPager;
    private RadioGroup radioGroup;
    private RadioButton rb_Chat, rb_Me, rb_Task, rb_Plan;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
       /**
         * RadioGroup部分
         */
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        rb_Chat = (RadioButton) findViewById(R.id.rb_chat);
        rb_Plan = (RadioButton) findViewById(R.id.rb_plan);
        rb_Task = (RadioButton) findViewById(R.id.rb_task);
        rb_Me = (RadioButton) findViewById(R.id.rb_me);
        radioGroup.setOnCheckedChangeListener(this);
        /**
         * ViewPager部分
         */
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        ChatFragment chatFragment = new ChatFragment();
        PlanFragment planFragment = new PlanFragment();
        TaskFragment taskFragment = new TaskFragment();
        MeFragment meFragment = new MeFragment();
        List<Fragment> alFragment = new ArrayList<Fragment>();
        alFragment.add(chatFragment);
        alFragment.add(planFragment);
        alFragment.add(taskFragment);
        alFragment.add(meFragment);
        //ViewPager设置适配器
        viewPager.setAdapter(new MyFragmentPagerAdapter(getSupportFragmentManager(), alFragment));
        //ViewPager显示第一个Fragment
        viewPager.setCurrentItem(0);
        viewPager.setOnPageChangeListener(this);
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
            exit();
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
    //RadioGroup选中状态改变监听---开始----
    @Override
    public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
        switch (checkedId) {
            case R.id.rb_chat:
                /**
                 * setCurrentItem第二个参数控制页面切换动画
                 * true:打开/false:关闭
                 */
                viewPager.setCurrentItem(0, false);
                break;
            case R.id.rb_plan:
                viewPager.setCurrentItem(1, false);
                break;
            case R.id.rb_task:
                viewPager.setCurrentItem(2, false);
                break;
            case R.id.rb_me:
                viewPager.setCurrentItem(3, false);
                break;
        }
    }
    //RadioGroup选中状态改变监听---结束----
    //viewPager滑动监听---开始----
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }
    @Override
    public void onPageSelected(int position) {
        switch (position) {
            case 0:
                radioGroup.check(R.id.rb_chat);
                break;
            case 1:
                radioGroup.check(R.id.rb_plan);
                break;
            case 2:
                radioGroup.check(R.id.rb_task);
                break;
            case 3:
                radioGroup.check(R.id.rb_me);
                break;
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
    //viewPager滑动监听---结束----

}
