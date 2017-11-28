package com.edu.ouc.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.edu.ouc.activity.PaiFaActivity;
import com.edu.ouc.model.UserInfoModel;
import com.edu.ouc.tasktracking.R;

import java.util.List;

/**
 * Created by JHC on 2017/11/28.
 * 班长派发给员工时选择员工是的item适配器
 */

public class ItemPaiFaAdapter extends BaseAdapter {
    private List<UserInfoModel> data;
    private Context context;
    private PaiFaActivity.AllCheckListener allCheckListener;

    public ItemPaiFaAdapter(List<UserInfoModel> data, Context context, PaiFaActivity.AllCheckListener allCheckListener) {
        this.data = data;
        this.context = context;
        this.allCheckListener = allCheckListener;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int i) {
        return data.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        ViewHoder hd;
        if (view == null) {
            hd = new ViewHoder();
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            view = layoutInflater.inflate(R.layout.item_paifa, null);
            hd.text_unit = (TextView) view.findViewById(R.id.text_unit);
            hd.text_truename = (TextView) view.findViewById(R.id.text_truename);
            hd.text_userid = (TextView) view.findViewById(R.id.text_userid);
            hd.checkBox = (CheckBox) view.findViewById(R.id.ckb);
            view.setTag(hd);
        }
        UserInfoModel mModel = data.get(i);
        hd = (ViewHoder) view.getTag();
        hd.text_unit.setText(mModel.getUnit());
        hd.text_truename.setText(mModel.getTruename());
        hd.text_userid.setText(String.valueOf(mModel.getId()));
        final ViewHoder hdFinal = hd;
        hd.checkBox.setChecked(mModel.ischeck());
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckBox checkBox = hdFinal.checkBox;
                if (checkBox.isChecked()) {
                    checkBox.setChecked(false);
                    data.get(i).setIscheck(false);
                } else {
                    checkBox.setChecked(true);
                    data.get(i).setIscheck(true);
                }
                //监听每个item，若所有checkbox都为选中状态则更改main的全选checkbox状态
                for (UserInfoModel model : data) {
                    if (!model.ischeck()) {
                        allCheckListener.onCheckedChanged(false);
                        return;
                    }
                }
                allCheckListener.onCheckedChanged(true);


            }
        });


        return view;
    }

    class ViewHoder {
        TextView text_unit;//单位
        TextView text_truename; //真实姓名
        TextView text_userid; //用户id
        CheckBox checkBox;
    }
}
