package org.lemon.commons;

import android.content.Context;

import com.baidu.tts.client.SpeechSynthesizer;
import com.baidu.tts.client.TtsMode;

import org.lemon.shoutscreen.util.SpeakUtil;
import org.xutils.x;

/**
 * 全局对象
 * Created by Guang on 2017/3/23.
 */
public class Application extends android.app.Application {
    private static Context context;
    //    private static String serverIP="172.16.10.44:8080";
    private static SpeakUtil speakUtil;
    public static String imei;
    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        x.Ext.init(this);
        // 设置是否输出debug
//        x.Ext.setDebug(false);
        /** 捕获全局异常   **/
//        CrashHandler catchHandler = CrashHandler.getInstance();
//        catchHandler.init(context);
    }

    public static void init() {
        speakUtil = new SpeakUtil();
    }


    public static Context getContext() {
        return context;
    }

//    public static String getServerIP() {
//        return serverIP;
//    }
//
//    public static void setServerIP(String serverIP) {
//        Application.serverIP = serverIP;
//    }

    public static void speakChinese(String text) {
        speakUtil.speak(text);
    }
//    public static DbManager getDb() {
//        return db;
//    }

}
