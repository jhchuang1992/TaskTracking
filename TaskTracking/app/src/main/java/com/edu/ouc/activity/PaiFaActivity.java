package com.edu.ouc.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;

import com.edu.ouc.adapter.ItemPaiFaAdapter;
import com.edu.ouc.listview.PaiFaListView;
import com.edu.ouc.model.UserInfoModel;
import com.edu.ouc.tasktracking.R;
import java.util.ArrayList;
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
        initData();
        initViewOper();
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_paifa_paifa:
                for(int i=0;i<userInfoModelList.size();i++){
                    System.out.println(userInfoModelList.get(i).ischeck());
                }
                break;
        }
    }
    /**
     * 数据加载
     */
    private void initData() {
        //模拟数据
        userInfoModelList = new ArrayList<>();
        UserInfoModel userInfoModel;
        for (int i = 0; i < 15; i++) {
            userInfoModel = new UserInfoModel();
            userInfoModel.setId(i);
            userInfoModel.setTruename("111");
            userInfoModel.setIscheck(false);
            userInfoModelList.add(userInfoModel);
        }
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
                    Log.e("mainCheckBox", "此时我不可以触发");
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
}
