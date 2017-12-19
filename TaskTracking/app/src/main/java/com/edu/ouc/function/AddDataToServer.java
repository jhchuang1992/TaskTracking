package com.edu.ouc.function;

import android.os.StrictMode;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by JHC on 2017/11/25.
 * 插入数据库
 */

public class AddDataToServer {
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    String content=null;

    public AddDataToServer(String urlPath,String jsonData){
        URL url=null;
        try {
            url=new URL("http://192.168.2.102:8080/TaskTrackingService/"+urlPath);
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setConnectTimeout(3000);
            // 设置允许输出
            conn.setDoOutput(true);
            // 允许向url流中读写数据
            conn.setDoInput(true);
            conn.setRequestMethod("POST");
             // 设置User-Agent: Fiddler
            conn.setRequestProperty("ser-Agent", "Fiddler");
           // 设置contentType
            conn.setRequestProperty("Content-Type","application/json");
            OutputStream os = conn.getOutputStream();
            os.write(jsonData.getBytes());
            os.close();
            int code = conn.getResponseCode(); //获取服务器是否响应
            if (code == 200) {
               content= dealResponseResult(conn.getInputStream());
            }
            }catch (Exception e){
            content="error";
            e.printStackTrace();
        }
    }
    public static String dealResponseResult(InputStream inputStream) {
        String resultData = null;      //存储处理结果
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] data = new byte[1024];
        int len = 0;
        try {
            while((len = inputStream.read(data)) != -1) {
                byteArrayOutputStream.write(data, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        resultData = new String(byteArrayOutputStream.toByteArray());
        return resultData;
    }
}
