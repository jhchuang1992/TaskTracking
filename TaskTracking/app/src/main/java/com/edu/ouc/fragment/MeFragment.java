package com.edu.ouc.fragment;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.edu.ouc.activity.AdminTaskInfoActivity;
import com.edu.ouc.activity.BanZhangPublicActivity;
import com.edu.ouc.activity.LoginActivity;
import com.edu.ouc.activity.YuanGongPublicActivity;
import com.edu.ouc.function.AutoMaticLogin;
import com.edu.ouc.R;


/**
 * Created by JHC on 2017/11/23.
 */
public class MeFragment extends Fragment implements View.OnClickListener{
    private Button button_me_exit;
    private TextView textView_me_name,textView_me_unit,textView_me_role,textView_me_phone,textView_me_email;
    public MeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_me, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        textView_me_name=(TextView)getActivity().findViewById(R.id.tv_me_name);
        textView_me_unit=(TextView)getActivity().findViewById(R.id.tv_me_unit);
        textView_me_role=(TextView)getActivity().findViewById(R.id.tv_me_role);
        textView_me_phone=(TextView)getActivity().findViewById(R.id.tv_me_phone);
        textView_me_email=(TextView)getActivity().findViewById(R.id.tv_me_email);
        button_me_exit=(Button)getActivity().findViewById(R.id.btn_me_exit);
        button_me_exit.setOnClickListener(this);
        initViewData();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_me_exit:
                AutoMaticLogin.getInstance().clearSharedPreferences(getContext());
                System.exit(0);
                break;
        }
    }
    public void initViewData(){
        textView_me_name.setText(AutoMaticLogin.getInstance().getUserInfo(getActivity()).getTruename());
        textView_me_unit.setText(AutoMaticLogin.getInstance().getUserInfo(getActivity()).getUnit());
        textView_me_role.setText(AutoMaticLogin.getInstance().getUserInfo(getActivity()).getRole());
        textView_me_phone.setText(AutoMaticLogin.getInstance().getUserInfo(getActivity()).getPhone());
        textView_me_email.setText(AutoMaticLogin.getInstance().getUserInfo(getActivity()).getEmail());
    }
}
