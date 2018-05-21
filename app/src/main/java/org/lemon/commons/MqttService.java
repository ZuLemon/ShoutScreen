package org.lemon.commons;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.lemon.shoutscreen.model.ReturnModel;
import org.lemon.shoutscreen.util.SerializableMap;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * 推送服务
 */
public class MqttService extends Service {
    public static final String ACTION_SCREEN = "org.lemon.commons.MqttService";
    private static MqttClient mqttClient;
    private static MqttConnectOptions options = new MqttConnectOptions();
    private static String url;
    private static String user = "";
    private static String password = "";
    private static String topic = "";
    private static boolean hold = false;
    private boolean connect = false;
    private static int idx = 0;
//    public static void getServer() {
//        new Thread() {
//            @Override
//            public void run() {
//                ReturnModel returnDomain = new ReturnModel();
//                HashMap parmap = new  HashMap<String, String>();
//                try {
//                    returnDomain = (ReturnModel) (HttpUtil.get("mqtt/getServer.do", parmap, ReturnModel.class));
//                    if (returnDomain.getSuccess()) {
//                        Map map = (Map) returnDomain.getObject();
//                        url = JSONArray.parseObject(map.get("broker").toString(), String[].class);
//                        user = String.valueOf(map.get("user"));
//                        password = String.valueOf(map.get("password"));
//                        connect();
//                    }
//                } catch (Exception e) {
//                    Log.e("MqttService",e.getMessage());
//                    e.printStackTrace();
//                }
//            }
//        }.start();
//    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//            getServer();
//        url ="192.168.34.99:61613";
////        url = "tcp://172.16.10.241:61613";
//        user = "admin";
//        password = "herbal";
        connect();
        new Thread() {
            @Override
            public void run() {
                while (true) {
                    if (!hold) reconnect();
                    try {
                        sleep(5 * 1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        disconnect();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private static class Callback implements MqttCallback {
        @Override
        public void connectionLost(Throwable throwable) {
            Log.e("error2", throwable.getMessage());
            reconnect();
        }

        @Override
        public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
            String mes = new String(mqttMessage.getPayload(), "utf-8");
            Log.e("mes", mes);

//            new MqttNotification().notification(mes);
            Intent intent = new Intent();//创建Intent对象
            intent.setAction(ACTION_SCREEN);
//            Bundle bundle=new Bundle();
//            SerializableMap serializableMap=new SerializableMap();
//            serializableMap.setMap(map);
//            bundle.putSerializable("people",serializableMap);
//            intent.putExtras(bundle);
            intent.putExtra("people", mes);
//            intent.putExtra("name", String.valueOf(map.get("name")));
//            intent.putExtra("status", Integer.parseInt(String.valueOf(map.get("status"))));
//            Log.e("APP",Application.getContext().getPackageName());
            Application.getContext().sendBroadcast(intent);//发送广播
//            Log.e("广播发送成功",map.toString());
        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

        }
    }


    public static synchronized void connect() {
        Log.e("###", "连接");
        try {
//            Log.e("推送服务：",url[idx]);
//            if (url.length > 1) {
            mqttClient = new MqttClient("tcp://"+url, Application.imei, new MemoryPersistence());
            options.setUserName(user);
            options.setPassword(password.toCharArray());
//                options.setConnectionTimeout(30);
//                options.setKeepAliveInterval(80);
//            options.setCleanSession(true);
            Log.e("mqttserver","tcp://"+url);
            Log.e("user",user );
            Log.e("password",password);
            mqttClient.connect(options);
            mqttClient.subscribe(topic, 2);
            Log.e("topic",topic);


            mqttClient.setCallback(new Callback());
            Log.e("connect", topic+"#success//" + url);
//            new CoolToast(Application.getContext()).show("success//" + url);
            hold = true;
//            } else {
//                Log.e("connect", "fail//" + url);
//                hold = false;
//            }
        } catch (Exception e) {
            Log.e("mqtt", e.getMessage());
//            new CoolToast(Application.getContext()).show(e.getMessage());

//            e.printStackTrace();
            hold = false;
        }
    }

    public static void reconnect() {
        Log.e("###", "重新连接");
//        new CoolToast(Application.getContext()).show("重新连接");

//        if (idx < url.length - 1) {
//            idx++;
//        } else {
//            idx = 0;
//        }
        connect();
    }

    public static void disconnect() {
        try {
            if (mqttClient == null) {
                return;
            }
            Log.e("###", "断开连接" + mqttClient.isConnected());
            if (mqttClient.isConnected()) {
                mqttClient.disconnect();
            }
        } catch (MqttException e) {
            e.printStackTrace();
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        } catch (Exception ef) {
            ef.printStackTrace();
        }
    }

    public static void setUrl(String url) {
        MqttService.url = url;
    }



    public static void setUser(String user) {
        MqttService.user = user;
    }


    public static void setPassword(String password) {
        MqttService.password = password;
    }

    public static void setTopic(String topic) {
        MqttService.topic = topic;
    }
}
