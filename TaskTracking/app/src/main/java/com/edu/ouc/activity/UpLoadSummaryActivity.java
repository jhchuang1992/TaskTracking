package com.edu.ouc.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Process;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.edu.ouc.dialog.UpLoadingDialog;
import com.edu.ouc.function.UpLoadFileToServer;
import com.edu.ouc.model.PublicShareUserinfo;
import com.edu.ouc.model.TaskScheduleModel;
import com.edu.ouc.permission.PermisionUtils;
import com.edu.ouc.photopicker.ImageCaptureManager;
import com.edu.ouc.photopicker.PhotoPickerActivity;
import com.edu.ouc.photopicker.PhotoPreviewActivity;
import com.edu.ouc.photopicker.SelectModel;
import com.edu.ouc.photopicker.intent.PhotoPickerIntent;
import com.edu.ouc.photopicker.intent.PhotoPreviewIntent;
import com.edu.ouc.tasktracking.R;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Request;

/**
 * Created by JHC on 2017/11/26.
 * 上传报告
 */

public class UpLoadSummaryActivity extends AppCompatActivity implements View.OnClickListener{
    private GridView gridView_uploadsummary_img;
    private static final int REQUEST_CAMERA_CODE = 10;
    private static final int REQUEST_PREVIEW_CODE = 20;
    private ArrayList<String> imagePaths = new ArrayList<>();
    private ImageCaptureManager captureManager; // 相机拍照处理类
    private String TAG =UpLoadSummaryActivity.class.getSimpleName();
    private UpLoadImageAdapter upLoadImageAdapter;
    private Button button_uploadsummary_commit; //提交报告按钮
    private EditText editText_upload_taskname,editText_upload_taskinfo,editText_upload_remarks;
    private ProgressBar progressBar_uploadsummary_progress; //进度条
    private UpLoadingDialog dialog; //弹框
    private int Lastid=0;  //上一个窗体传递过来的id
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uploadsummary);
        Lastid = (int) getIntent().getSerializableExtra("taskid");
        //检测读写权限
        PermisionUtils.verifyStoragePermissions(this);
        gridView_uploadsummary_img=(GridView)findViewById(R.id.gv_uploadsummary_img);
        int cols = getResources().getDisplayMetrics().widthPixels / getResources().getDisplayMetrics().densityDpi;
        cols = cols < 3 ? 3 : cols;
        gridView_uploadsummary_img.setNumColumns(cols);
        // preview
        gridView_uploadsummary_img.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String imgs = (String) parent.getItemAtPosition(position);
                if ("000000".equals(imgs) ){
                    PhotoPickerIntent intent = new PhotoPickerIntent(UpLoadSummaryActivity.this);
                    intent.setSelectModel(SelectModel.MULTI);
                    intent.setShowCarema(true); // 是否显示拍照
                    intent.setMaxTotal(6); // 最多选择照片数量，默认为6
                    intent.setSelectedPaths(imagePaths); // 已选中的照片地址， 用于回显选中状态
                    startActivityForResult(intent, REQUEST_CAMERA_CODE);
                }else{
                    PhotoPreviewIntent intent = new PhotoPreviewIntent(UpLoadSummaryActivity.this);
                    intent.setCurrentItem(position);
                    intent.setPhotoPaths(imagePaths);
                    startActivityForResult(intent, REQUEST_PREVIEW_CODE);
                }
            }
        });
        imagePaths.add("000000");
        upLoadImageAdapter = new UpLoadImageAdapter(imagePaths);
        gridView_uploadsummary_img.setAdapter(upLoadImageAdapter);
        button_uploadsummary_commit=(Button)findViewById(R.id.btn_uploadsummary_commit);
        button_uploadsummary_commit.setOnClickListener(this);
        progressBar_uploadsummary_progress=(ProgressBar)findViewById(R.id.pgb_uploadsummary_progress);
        editText_upload_taskname=(EditText)findViewById(R.id.edt_upload_taskname);//报告主题
        editText_upload_taskinfo=(EditText)findViewById(R.id.edt_upload_taskinfo); //报告
        editText_upload_remarks=(EditText)findViewById(R.id.edt_upload_remarks); //报告备注
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_uploadsummary_commit:
                commit_imag();
                break;
        }
    }
    Handler handler=new Handler(){
        //0：提示出错了 1：提示未打开连接 2：弹出提交框
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
                    dialog = new UpLoadingDialog(UpLoadSummaryActivity.this);
                    dialog.setCancelable(false); //设置这个对话框不能被用户按[返回键]而取消掉
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.show();
                    break;
            }
        }
    };

    public void commit_imag(){
        new Thread(){
            @Override
            public void run() {
                super.run();

                handler.sendEmptyMessage(2);
                if (imagePaths.size()>0) { //若有数据
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("userid", String.valueOf(Lastid));//对应taskschedule和tasktake表的id
                    params.put("username", PublicShareUserinfo.lgname);//用户名
                    params.put("role", PublicShareUserinfo.role);//描述角色
                    params.put("description_title", editText_upload_taskname.getText().toString().trim());//描述主题
                    params.put("description_info", editText_upload_taskinfo.getText().toString().trim());//描述正文
                    params.put("description_remarks", editText_upload_remarks.getText().toString().trim());//描述备注
                    switch (imagePaths.size()){
                        case 2:
                            OkHttpUtils.post()//
                                    .addFile("mFile", "server_afu.png", new File(imagePaths.get(0)))//可以上传多个文件
                                    .url("http://10.0.2.2:8080/TaskTrackingService/upload.do?")
                                    .params(params)//
                                    .build()
                                    .execute(new MyStringCallback());
                            break;
                        case 3:
                            OkHttpUtils.post()//
                                    .addFile("mFile", "server_afu.png", new File(imagePaths.get(0)))//可以上传多个文件
                                    .addFile("mFile", "server_afu.png", new File(imagePaths.get(1)))//可以上传多个文件
                                    .url("http://10.0.2.2:8080/TaskTrackingService/upload.do?")
                                    .params(params)//
                                    .build()
                                    .execute(new MyStringCallback());
                            break;
                        case 4:
                            OkHttpUtils.post()//
                                    .addFile("mFile", "server_afu.png", new File(imagePaths.get(0)))//可以上传多个文件
                                    .addFile("mFile", "server_afu.png", new File(imagePaths.get(1)))//可以上传多个文件
                                    .addFile("mFile", "server_afu.png", new File(imagePaths.get(2)))//可以上传多个文件
                                    .url("http://10.0.2.2:8080/TaskTrackingService/upload.do?")
                                    .params(params)//
                                    .build()
                                    .execute(new MyStringCallback());
                            break;
                        case 5:
                            OkHttpUtils.post()//
                                    .addFile("mFile", "server_afu.png", new File(imagePaths.get(0)))//可以上传多个文件
                                    .addFile("mFile", "server_afu.png", new File(imagePaths.get(1)))//可以上传多个文件
                                    .addFile("mFile", "server_afu.png", new File(imagePaths.get(2)))//可以上传多个文件
                                    .addFile("mFile", "server_afu.png", new File(imagePaths.get(3)))//可以上传多个文件
                                    .url("http://10.0.2.2:8080/TaskTrackingService/upload.do?")
                                    .params(params)//
                                    .build()
                                    .execute(new MyStringCallback());
                            break;
                        case 6:
                            OkHttpUtils.post()//
                                    .addFile("mFile", "server_afu.png", new File(imagePaths.get(0)))//可以上传多个文件
                                    .addFile("mFile", "server_afu.png", new File(imagePaths.get(1)))//可以上传多个文件
                                    .addFile("mFile", "server_afu.png", new File(imagePaths.get(2)))//可以上传多个文件
                                    .addFile("mFile", "server_afu.png", new File(imagePaths.get(3)))//可以上传多个文件
                                    .addFile("mFile", "server_afu.png", new File(imagePaths.get(4)))//可以上传多个文件
                                    .addFile("mFile", "server_afu.png", new File(imagePaths.get(5)))//可以上传多个文件
                                    .url("http://10.0.2.2:8080/TaskTrackingService/upload.do?")
                                    .params(params)
                                    .build()
                                    .execute(new MyStringCallback());
                            break;
                    }
                }
            }
        }.start();
    }
    //okhttp-utils的回调类
    public class MyStringCallback extends StringCallback {
        @Override
        public void onBefore(Request request, int id) {
            //setTitle("loading...");
        }
        @Override
        public void onAfter(int id) {
           //setTitle("Sample-okHttp");
        }

        @Override
        public void onError(Call call, Exception e, int id) {
            e.printStackTrace();
            dialog.dismiss();//取消显示进度框
            Toast.makeText(getApplicationContext(), "哎呀，出错了。。。", Toast.LENGTH_LONG).show();
            //tv_result.setText("onError:" + e.getMessage());
        }

        @Override
        public void onResponse(String response, int id) {
            System.out.println(response);
            dialog.dismiss();//取消显示进度框
            Toast.makeText(getApplicationContext(), "提交任务成功", Toast.LENGTH_LONG).show();
            //tv_result.setText("onResponse:" + response);
            switch (id) {
                case 100://http请求的响应码
                    //Toast.makeText(UpLoadSummaryActivity.this, "http", Toast.LENGTH_SHORT).show();
                    break;
                case 101://http请求的响应码
                   //Toast.makeText(UpLoadSummaryActivity.this, "https", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
        @Override
        public void inProgress(float progress, long total, int id) {
            //Log.e(TAG, "inProgress:" + progress);
            progressBar_uploadsummary_progress.setProgress((int) (100 * progress));
        }
    }
    //添加图片-----开始----
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK) {
            switch (requestCode) {
                // 选择照片
                case REQUEST_CAMERA_CODE:
                    ArrayList<String> list = data.getStringArrayListExtra(PhotoPickerActivity.EXTRA_RESULT);
                    Log.d(TAG, "list: " + "list = [" + list.size());
                    loadAdpater(list);
                    break;
                // 预览
                case REQUEST_PREVIEW_CODE:
                    ArrayList<String> ListExtra = data.getStringArrayListExtra(PhotoPreviewActivity.EXTRA_RESULT);
                    Log.d(TAG, "ListExtra: " + "ListExtra = [" + ListExtra.size());
                    loadAdpater(ListExtra);
                    break;
            }
        }
    }
    private void loadAdpater(ArrayList<String> paths){
        if (imagePaths!=null&& imagePaths.size()>0){
            imagePaths.clear();
        }
        if (paths.contains("000000")){
            paths.remove("000000");
        }
        paths.add("000000");
        imagePaths.addAll(paths);
        upLoadImageAdapter  = new UpLoadImageAdapter(imagePaths);
        gridView_uploadsummary_img.setAdapter(upLoadImageAdapter);
        try{
            JSONArray obj = new JSONArray(imagePaths);
            Log.e("--", obj.toString());
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    //上传图片--适配器---开始----
    private class UpLoadImageAdapter extends BaseAdapter {
        private ArrayList<String> listUrls;
        private LayoutInflater inflater;
        public UpLoadImageAdapter(ArrayList<String> listUrls) {
            this.listUrls = listUrls;
            if(listUrls.size() == 7){
                listUrls.remove(listUrls.size()-1);
            }
            inflater = LayoutInflater.from(UpLoadSummaryActivity.this);
        }

        public int getCount(){
            return  listUrls.size();
        }
        @Override
        public String getItem(int position) {
            return listUrls.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = inflater.inflate(R.layout.item_image, parent,false);
                holder.image = (ImageView) convertView.findViewById(R.id.imageView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder)convertView.getTag();
            }

            final String path=listUrls.get(position);
            if (path.equals("000000")){
                holder.image.setImageResource(R.mipmap.ic_launcher);
            }else {
                Glide.with(UpLoadSummaryActivity.this)
                        .load(path)
                        .placeholder(R.mipmap.default_error)
                        .error(R.mipmap.default_error)
                        .centerCrop()
                        .crossFade()
                        .into(holder.image);
            }
            return convertView;
        }
        class ViewHolder {
            ImageView image;
        }
    }
    //上传图片--适配器---结束----
    //添加图片-----结束----
}
