package com.edu.ouc.function;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.edu.ouc.model.UserInfoModel;

/**
 * Created by JHC on 2017/11/23.
 * 自动登录
 */

public class AutoMaticLogin {
    private static AutoMaticLogin instance;

    private AutoMaticLogin() {
    }
    public static AutoMaticLogin getInstance() {
        if (instance == null) {
            instance = new AutoMaticLogin();
        }
        return instance;
    }
    /**
     * 保存自动登录的用户信息
     */
    public void saveUserInfo(Context context, int userid,String username, String password,String truename,String role,String unit,String email,String phone) {
        SharedPreferences sp = context.getSharedPreferences("userInfo", Context.MODE_PRIVATE);//Context.MODE_PRIVATE表示SharePrefences的数据只有自己应用程序能访问。
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("USER_ID", userid);
        editor.putString("USER_NAME", username);
        editor.putString("PASSWORD", password);
        editor.putString("TRUENAME", truename);
        editor.putString("ROLE", role);
        editor.putString("UNIT", unit);
        editor.putString("PHONE", phone);
        editor.putString("EMAIL", email);
        editor.commit();
    }


    /**
     * 获取用户信息model
     *
     * @param context
     * @param
     * @param
     */
    public UserInfoModel getUserInfo(Context context) {
        SharedPreferences sp = context.getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        UserInfoModel userInfo = new UserInfoModel();
        userInfo.setId(sp.getInt("USER_ID", 0));
        userInfo.setLgname(sp.getString("USER_NAME", ""));
        userInfo.setLgpwd(sp.getString("PASSWORD", ""));
        userInfo.setTruename(sp.getString("TRUENAME", ""));
        userInfo.setRole(sp.getString("ROLE", ""));
        userInfo.setUnit(sp.getString("UNIT", ""));
        userInfo.setPhone(sp.getString("PHONE", ""));
        userInfo.setEmail(sp.getString("EMAIL", ""));
        return userInfo;
    }


    /**
     * userInfo中是否有数据
     */
    public boolean hasUserInfo(Context context) {
        UserInfoModel userInfo = getUserInfo(context);
        if (userInfo != null) {
            if ((!TextUtils.isEmpty(userInfo.getLgname())) && (!TextUtils.isEmpty(userInfo.getLgpwd()))) {//有数据
                return true;
            } else {
                return false;
            }
        }
        return false;
    }
    //清除SharedPreferences
    public void clearSharedPreferences(Context context){
        SharedPreferences sp = context.getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        sp.edit().clear().commit();
    }

}
