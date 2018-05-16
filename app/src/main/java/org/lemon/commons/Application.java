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
        speakUtil=new SpeakUtil();
        // 设置是否输出debug
//        x.Ext.setDebug(false);
        /** 捕获全局异常   **/
//        CrashHandler catchHandler = CrashHandler.getInstance();
//        catchHandler.init(context);
    }

//    private void setDbConfig(){
//        DbManager.DaoConfig daoConfig = new DbManager.DaoConfig()
//                .setDbName("escort.db")
//                // 不设置dbDir时, 默认存储在app的私有目录.
//                .setDbDir(new File(FileUtil.getProjectDataPath()))
//                .setDbVersion(1)
//                .setDbOpenListener(new DbManager.DbOpenListener() {
//                    @Override
//                    public void onDbOpened(DbManager db) {
//                        // 开启WAL, 对写入加速提升巨大
//                        db.getDatabase().enableWriteAheadLogging();
//                    }
//                });
////            ;.setDbUpgradeListener(new DbManager.DbUpgradeListener() {
////                @Override
////                public void onUpgrade(DbManager db, int oldVersion, int newVersion) {
////                    // TODO: ...
////                    // db.addColumn(...);
////                    // db.dropTable(...);
////                    // ...
////                    // or
////                    // db.dropDb();
////                }
////            })
//        db = x.getDb(daoConfig);
//    }

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

    public static void speakChinese(String text){
        speakUtil.speak(text);
    }
//    public static DbManager getDb() {
//        return db;
//    }

}
