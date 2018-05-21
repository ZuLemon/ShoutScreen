package org.lemon.commons;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.alibaba.fastjson.JSON;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;

/**
 * Created by Lemon on 2017/7/24.
 */

public class HttpUtil {

     private static String baseUrl  ;
//    static String baseUrl = "http://172.16.10.241:8888/herbal/";
    public static void setUri(String url){
        baseUrl="http://"+url+"/herbal/";
    }
    /**
     * Get请求
     * @param url
     * @param paramsMap
     * @param clzz
     * @return
     * @throws Exception
     */
    public static Object get(String url, HashMap<String, String> paramsMap,Class clzz) throws Exception {
        String result=null;
        try {
            StringBuilder tempParams = new StringBuilder();
            int pos = 0;
            for (String key : paramsMap.keySet()) {
                if (pos > 0) {
                    tempParams.append("&");
                }
                tempParams.append(String.format("%s=%s", key, URLEncoder.encode(paramsMap.get(key),"utf-8")));
                pos++;
            }
            String requestUrl = baseUrl +url +"?"+ tempParams.toString();
            System.out.println(requestUrl);
            // 新建一个URL对象
            URL urlx = new URL(requestUrl);
            // 打开一个HttpURLConnection连接
            HttpURLConnection urlConn = (HttpURLConnection) urlx.openConnection();
            // 设置连接主机超时时间
            urlConn.setConnectTimeout(5 * 1000);
            //设置从主机读取数据超时
            urlConn.setReadTimeout(5 * 1000);
            // 设置是否使用缓存  默认是true
            urlConn.setUseCaches(true);
            // 设置为Post请求
            urlConn.setRequestMethod("GET");
            //urlConn设置请求头信息
            //设置请求中的媒体类型信息。
            urlConn.setRequestProperty("Content-Type", "application/json");
            //设置客户端与服务连接类型
            urlConn.addRequestProperty("Connection", "Keep-Alive");
            // 开始连接
            urlConn.connect();
            // 判断请求是否成功
            if (urlConn.getResponseCode() == 200) {
                // 获取返回的数据
                result = streamToString(urlConn.getInputStream());
            } else {
                throw new Exception("请求失败");
            }
            // 关闭连接
            urlConn.disconnect();
        } catch (Exception e) {
            throw new Exception(e);
        }
        return JSON.parseObject ( result, clzz );
    }

    /**
     * Post 请求
     * @param url
     * @param paramsMap
     * @param clzz
     * @return
     * @throws Exception
     */
    public static Object post(String url, HashMap<String, String> paramsMap,Class clzz) throws Exception {
        String result=null;
        try {
            StringBuilder tempParams = new StringBuilder();
            int pos = 0;
            for (String key : paramsMap.keySet()) {
                if (pos > 0) {
                    tempParams.append("&");
                }
                tempParams.append(String.format("%s=%s", key, URLEncoder.encode(paramsMap.get(key),"utf-8")));
                pos++;
            }
            String params =tempParams.toString();
//            // 请求的参数转换为byte数组
            byte[] postData = params.getBytes();
            // 新建一个URL对象
            URL urlx = new URL(baseUrl+url);
            // 打开一个HttpURLConnection连接
            HttpURLConnection urlConn = (HttpURLConnection) urlx.openConnection();
            // 设置连接超时时间
            urlConn.setConnectTimeout(5 * 1000);
            //设置从主机读取数据超时
            urlConn.setReadTimeout(5 * 1000);
            // Post请求必须设置允许输出 默认false
            urlConn.setDoOutput(true);
            //设置请求允许输入 默认是true
            urlConn.setDoInput(true);
            // Post请求不能使用缓存
            urlConn.setUseCaches(false);
            // 设置为Post请求
            urlConn.setRequestMethod("POST");
            //设置本次连接是否自动处理重定向
            urlConn.setInstanceFollowRedirects(true);
            // 配置请求Content-Type
            urlConn.setRequestProperty("Content-Type", "application/json");
            // 开始连接
            urlConn.connect();
            // 发送请求参数
            OutputStream dos = urlConn.getOutputStream();
            dos.write(postData);
            dos.flush();
            dos.close();
            // 判断请求是否成功
            if (urlConn.getResponseCode() == 200) {
                // 获取返回的数据
                result = streamToString(urlConn.getInputStream());
            } else {
                throw new Exception("请求失败");
            }
            // 关闭连接
            urlConn.disconnect();
        } catch (Exception e) {
            throw new Exception(e);
        }
        return JSON.parseObject ( result, clzz );
    }

    /**
     * 查看图片
     * @param url
     * @return
     * @throws Exception
     */
    public static Bitmap getImageByUrl(String url) throws Exception {
        //创建一个url对象
        //打开URL对应的资源输入流
        InputStream is= null;
        Bitmap bitmap=null;
        try {
            URL urlx=new URL(url);
            Log.e("URL",url);
            is = urlx.openStream();
            bitmap= BitmapFactory.decodeStream(is);
        } catch (IOException e) {
            return null;
//            e.printStackTrace();
        }finally {
            try {
                is.close();
            } catch (IOException e) {
                return null;
//                e.printStackTrace();
            }
        }
        return bitmap;
    }
    /**
     * 将输入流转换成字符串
     *
     * @param is 从网络获取的输入流
     * @return
     */
    private static String streamToString(InputStream is) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = is.read(buffer)) != -1) {
                baos.write(buffer, 0, len);
            }
            baos.close();
            is.close();
            byte[] byteArray = baos.toByteArray();
            return new String(byteArray);
        } catch (Exception e) {
            return null;
        }
    }

}
