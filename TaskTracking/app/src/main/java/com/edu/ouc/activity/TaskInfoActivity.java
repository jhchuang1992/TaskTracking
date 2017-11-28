package com.edu.ouc.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.edu.ouc.model.PublicShareUserinfo;
import com.edu.ouc.model.TaskInfoModel;
import com.edu.ouc.tasktracking.R;

import org.w3c.dom.Text;

/**
 * Created by JHC on 2017/11/23.
 * 管理员查看任务详情
 */

public class TaskInfoActivity extends Activity implements View.OnClickListener{
    private TextView textView_tv_taskinfo_author,textView_tv_taskinfo_taskname,textView_tv_taskinfo_tasktype,textView_tv_taskinfo_taskinfo,
            textView_taskinfo_status,textView_taskinfo_starttime,textView_taskinfo_endtime,textView_taskinfo_remarks;
   private Button button_taskinfo_endtask,button__taskinfo_commit,button__taskinfo_paifa;
    private String taskInfo_status; //任务状态
    private PublicShareUserinfo publicShareUserinfo; //公共用户信息类
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_taskinfo);
        textView_tv_taskinfo_author=(TextView)findViewById(R.id.tv_taskinfo_author);
        textView_tv_taskinfo_taskname=(TextView)findViewById(R.id.tv_taskinfo_taskname);
        textView_tv_taskinfo_tasktype=(TextView)findViewById(R.id.tv_taskinfo_tasktype);
        textView_tv_taskinfo_taskinfo=(TextView)findViewById(R.id.tv_taskinfo_taskinfo);
        textView_taskinfo_status=(TextView)findViewById(R.id.tv_taskinfo_status);
        textView_taskinfo_starttime=(TextView)findViewById(R.id.tv_taskinfo_starttime);
        textView_taskinfo_endtime=(TextView)findViewById(R.id.tv_taskinfo_endtime);
        textView_taskinfo_remarks=(TextView)findViewById(R.id.tv_taskinfo_remarks);
        button__taskinfo_commit=(Button)findViewById(R.id.btn_taskinfo_commit);
        button__taskinfo_paifa=(Button)findViewById(R.id.btn_taskinfo_paifa);
        button_taskinfo_endtask=(Button)findViewById(R.id.btn_taskinfo_endtask);
        button__taskinfo_commit.setOnClickListener(this);
        button_taskinfo_endtask.setOnClickListener(this);
        button__taskinfo_paifa.setOnClickListener(this);
        //加载信息，将传递过来的条目显示在界面中
        initViewData();

    }
    Handler handler=new Handler(){
        //0：提示出错了 1：提示未打开连接
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0:
                    Toast.makeText(getApplicationContext(), "哎呀，出错了。。。", Toast.LENGTH_LONG).show();
                    break;
                case 1:
                    Toast.makeText(getApplicationContext(), "网络未连接", Toast.LENGTH_LONG).show();
                    break;
                case 2:

                    break;
            }
        }
    };
    //加载信息----开始-----
    public void initViewData(){
        new Thread(){
            @Override
            public void run() {
                TaskInfoModel taskinfo = (TaskInfoModel) getIntent().getSerializableExtra("taskinfo");
                textView_tv_taskinfo_author.setText(taskinfo.getTask_author());
                textView_tv_taskinfo_taskname.setText(taskinfo.getTask_name());
                textView_tv_taskinfo_tasktype.setText(taskinfo.getTask_type());
                textView_tv_taskinfo_taskinfo.setText(taskinfo.getTask_info());
                textView_taskinfo_status.setText(taskinfo.getTask_status());
                taskInfo_status=taskinfo.getTask_status();
                //若是管理员，则不需要显示这三个按钮
                if (PublicShareUserinfo.role.equals("管理员")){
                    button__taskinfo_paifa.setVisibility(View.GONE); //设置按钮隐藏
                    button_taskinfo_endtask.setVisibility(View.GONE); //设置按钮隐藏
                    button__taskinfo_commit.setVisibility(View.GONE);//设置按钮隐藏
                }else if (PublicShareUserinfo.role.equals("班长")){ //若是班长，且此时任务时待接时，则设置按钮
                    if (taskinfo.getTask_status().equals("待接")){
                        button__taskinfo_paifa.setText("接收任务");
                        button_taskinfo_endtask.setVisibility(View.GONE); //设置按钮隐藏
                        button__taskinfo_commit.setVisibility(View.GONE);//设置按钮隐藏
                    }
                }else if (PublicShareUserinfo.role.equals("职员")){ //若是职员，则设置按钮
                        button__taskinfo_paifa.setText("接收任务");
                        button_taskinfo_endtask.setVisibility(View.GONE); //设置按钮隐藏
                        button__taskinfo_commit.setVisibility(View.GONE);//设置按钮隐藏
                }
                textView_taskinfo_starttime.setText(taskinfo.getTask_startdate());
                textView_taskinfo_endtime.setText(taskinfo.getTask_enddate());
                textView_taskinfo_remarks.setText(taskinfo.getRemarks());
            }
        }.start();
    }
    //加载信息----结束-----
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_taskinfo_commit:
                break;
            case R.id.btn_taskinfo_endtask:
                break;
            case R.id.btn_taskinfo_paifa:
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
                        button__taskinfo_paifa.setText("派发任务");
                        //更新任务状态，并增加班长信息
                    }
                }).setNegativeButton("返回",new DialogInterface.OnClickListener() {//添加返回按钮
            @Override
            public void onClick(DialogInterface dialog, int which) {//响应事件
                // TODO Auto-generated method stub
            }
        }).show();//在按键响应事件中显示此对话框
    }else if(button__taskinfo_paifa.getText().equals("派发任务")){

        AlertDialog.Builder builder = new AlertDialog.Builder(TaskInfoActivity.this);
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
                Toast.makeText(TaskInfoActivity.this, "爱好为：" + sb.toString(), Toast.LENGTH_SHORT).show();
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
