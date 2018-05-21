package org.lemon.commons;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * @Author : Lemon
 * @Desc :
 * @Date : 2018/5/20 12:31
 **/
public class AppOption {
    public static final String APP_OPTION_SERVER="server";
    public static final String APP_OPTION_USER="user";
    public static final String APP_OPTION_PASSWORD="password";
    public static final String APP_OPTION_MQTTSERVER ="mqttserver";
    public static final String APP_OPTION_MQTTUSER="mqttuser";
    public static final String APP_OPTION_MQTTPASSWORD="mqttpassword";
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public AppOption() {
        this.sharedPreferences = Application.getContext().getSharedPreferences("screen.option", Context.MODE_PRIVATE);
        this.editor = this.sharedPreferences.edit();
    }

    public String getOption(String key){
        return sharedPreferences.getString(key,"");
    }

    public void setOption(String key,String value){
        editor.putString(key, value);
        editor.apply();
    }

}
