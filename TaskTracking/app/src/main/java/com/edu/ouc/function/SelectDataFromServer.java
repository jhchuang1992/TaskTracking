package com.edu.ouc.function;

import android.os.StrictMode;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by JHC on 2017/11/22.
 * 用于查询服务器，返回String串
 */

public class SelectDataFromServer {
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    String content=null;
    public SelectDataFromServer(String url){
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        HttpGet httpGet= null;
            httpGet = new HttpGet(url);
        HttpClient httpClient=new DefaultHttpClient();
        HttpParams params = null;
        params = httpClient.getParams();
        //若3秒未连接上，则强制断开
        HttpConnectionParams.setConnectionTimeout(params, 3000);
        try {
            HttpResponse hr=httpClient.execute(httpGet);
            InputStream inputStream=hr.getEntity().getContent();
            BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(inputStream));
            String line=null;
            while((line= bufferedReader.readLine()) !=null){
                content=line;
            }
            inputStream.close();
            bufferedReader.close();
        } catch (IOException e) {
            content="error";
            e.printStackTrace();
        }
    }
}
