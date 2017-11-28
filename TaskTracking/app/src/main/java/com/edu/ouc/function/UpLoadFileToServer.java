package com.edu.ouc.function;

import android.graphics.Bitmap;

import com.edu.ouc.activity.UpLoadSummaryActivity;
import com.zhy.http.okhttp.OkHttpUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.BufferedSink;

import static android.content.ContentValues.TAG;
import static android.text.TextUtils.TruncateAt.END;

/**
 * Created by JHC on 2017/11/27.
 * 上传文件到服务器
 */

public class UpLoadFileToServer {
    // 上传的bitmap
    private Bitmap upbitmap;
    private final String BOUNDARYSTR = "--------aifudao7816510d1hq";
    private final String END = "\r\n";
    private final String LAST = "--";
    //1.定义一个OkhttpClient
    private static OkHttpClient client = new OkHttpClient();
    /**
     * 上传文件方法，参数为：文件、请求的URL地址
     */
   /* public String upoadFile(File file, String requestURL){
        String result="";
        Map<String, String> params = new HashMap<String, String>();
        params.put("username", "宋新良");//上传的用户名,没有不用传参数
        params.put("password", "123456");//上传的密码,没有不用传参数
        String url = requestURL ;
        OkHttpUtils.post()//
                .addFile("mFile", "server_afu.png", file)//可以上传多个文件
               *//* .addFile("mFile", "server_test.txt", file2)/*//*//*
                .url(url)
                .params(params)//
                .build()
                .execute(new UpLoadSummaryActivity.MyStringCallback());
        return result;
    }*/
}
