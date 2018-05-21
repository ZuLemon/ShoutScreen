package org.lemon.shoutscreen;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.lemon.commons.AppOption;
import org.lemon.commons.Application;
import org.lemon.commons.CoolToast;
import org.lemon.commons.HttpUtil;
import org.lemon.commons.MqttService;
import org.lemon.shoutscreen.model.UserModel;
import org.lemon.shoutscreen.util.ShoutScreenUtil;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import pub.devrel.easypermissions.EasyPermissions;

/**
 * @Author : Lemon
 * @Desc :
 * @Date : 2018/5/20 12:31
 **/
@ContentView(R.layout.login)
public class LoginActivity extends Activity implements EasyPermissions.PermissionCallbacks{
    @ViewInject(R.id.login_userId_editText)
    private EditText login_userId_editText;
    @ViewInject(R.id.login_password_editText)
    private EditText login_password_editText;
    @ViewInject(R.id.login_submit_button)
    private Button login_submit_button;
    private AppOption appOption = new AppOption();
    private ShoutScreenUtil shoutScreenUtil;
    private UserModel userModel;
    private boolean isFi=false;
    private CountDownTimer changeUITime = new CountDownTimer(5 * 1000, 1 * 1000) {
        @Override
        public void onTick(long l) {
            isFi=false;
            login_submit_button.setText(l/1000+""+"秒后自动登录");
        }

        @Override
        public void onFinish() {
            if(!isFi) {
                LoginThread(0);
            }
        }
    };
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        initPermission();
        Application.init();
        shoutScreenUtil=new ShoutScreenUtil();
        if("".equals(appOption.getOption(AppOption.APP_OPTION_SERVER))){
            HttpUtil.setUri("192.168.34.99:8888");
            appOption.setOption(AppOption.APP_OPTION_SERVER,"192.168.34.99:8888");
        }else {
            HttpUtil.setUri(appOption.getOption(AppOption.APP_OPTION_SERVER));
        }

        if("".equals(appOption.getOption(AppOption.APP_OPTION_MQTTSERVER))){
            MqttService.setUrl("192.168.34.99:61613");
            appOption.setOption(AppOption.APP_OPTION_MQTTSERVER,"192.168.34.99:61613");
        }else {
            MqttService.setUrl(appOption.getOption(AppOption.APP_OPTION_MQTTSERVER));
        }

        if("".equals(appOption.getOption(AppOption.APP_OPTION_MQTTUSER))){
            MqttService.setUser("admin");
            appOption.setOption(AppOption.APP_OPTION_MQTTUSER,"admin");
        }else{
            MqttService.setUser(appOption.getOption(AppOption.APP_OPTION_MQTTUSER));
        }

        if("".equals(appOption.getOption(AppOption.APP_OPTION_MQTTPASSWORD))){
            MqttService.setPassword("herbal");
            appOption.setOption(AppOption.APP_OPTION_MQTTPASSWORD,"herbal");
        }else{
            MqttService.setPassword(appOption.getOption(AppOption.APP_OPTION_MQTTPASSWORD));
        }
        if(!"".equals(appOption.getOption(AppOption.APP_OPTION_USER))&&!"".equals(appOption.getOption(AppOption.APP_OPTION_PASSWORD))){
            login_userId_editText.setText(appOption.getOption(AppOption.APP_OPTION_USER));
            login_password_editText.setText(appOption.getOption(AppOption.APP_OPTION_PASSWORD));
            changeUITime.start();
        }

    }
    @Event(R.id.login_submit_button)
    private void onClick(View v){
        if(isFi) {
            final String userId = login_userId_editText.getText().toString().trim();
            final String password = login_password_editText.getText().toString().trim();
            if ("".equals(userId) || "".equals(password)) {
                new CoolToast(getBaseContext()).show("用户编号和密码不允许为空");
                return;
            }
            //管理员修改服务器地址
            if ("admin".equals(userId) && "wlbgs".equals(password)) {
                login_userId_editText.setText(appOption.getOption(AppOption.APP_OPTION_USER));
                login_password_editText.setText(appOption.getOption(AppOption.APP_OPTION_PASSWORD));
                Intent intent = new Intent(LoginActivity.this, OptionActivity.class);
                startActivity(intent);
                return;
            }
            if (!isNumeric(userId)) {
                new CoolToast(getBaseContext()).show("用户编号错误");
                return;
            }
            LoginThread(0);
        }else {
            login_submit_button.setText("登录");
            isFi=true;
            changeUITime.cancel();
        }
    }
    private void LoginThread(final int what) {
        final Message message = new Message();
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case -1:
                        new CoolToast(getBaseContext()).show(String.valueOf(msg.obj));
                        break;
                    case 0:
                        if(null!=userModel) {
                            appOption.setOption(AppOption.APP_OPTION_USER, String.valueOf(userModel.getId()));
                            appOption.setOption(AppOption.APP_OPTION_PASSWORD, String.valueOf(login_password_editText.getText()));
                            Log.e("topic","ShoutScreen_"+userModel.getDeptId());
                            MqttService.setTopic("ShoutScreen_"+userModel.getDeptId());
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
//                        nameUtil ( (List) msg.obj );
                        break;
                }
            }
        };
        try {
            new Thread() {
                @Override
                public void run() {
                    super.run();
                    try {
                        switch (what) {
                            case 0:
                                message.what = 0;
                                if(shoutScreenUtil.confirmPasswd(String.valueOf(login_userId_editText.getText()),String.valueOf(login_password_editText.getText()))) {
                                    userModel=shoutScreenUtil.getUserInfoById(String.valueOf(login_userId_editText.getText()));
                                }
                                handler.sendMessage(message);
                                break;
                        }
                    } catch (Exception e) {
                        message.what = -1;
                        message.obj = e.getMessage();
                        handler.sendMessage(message);
                    }
                }
            }.start();
        } catch (Exception e) {
            new CoolToast(getBaseContext()).show(e.getMessage());
        }
    }
    public boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if (!isNum.matches()) {
            return false;
        }
        return true;
    }


    /**
     * android 6.0 以上需要动态申请权限
     */
    private void initPermission() {
        String permissions[] = {
                Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.MODIFY_AUDIO_SETTINGS,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.WRITE_SETTINGS,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.CHANGE_WIFI_STATE,
                Manifest.permission.RECEIVE_BOOT_COMPLETED
        };

        if (EasyPermissions.hasPermissions(this, permissions)) {//检查是否获取该权限
        } else {
            //第二个参数是被拒绝后再次申请该权限的解释
            //第三个参数是请求码
            //第四个参数是要申请的权限
            EasyPermissions.requestPermissions(this, "必要的权限", 0, permissions);

        }

//        for (String perm : permissions) {
//            if(EasyPermissions.hasPermissions(this, perm)) {
//
//            }else {
//                EasyPermissions.requestPermissions(this, "我需要这个权限", 100, perm);
//            }
//        }
//            if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(this, perm)) {
//                toApplyList.add(perm);
//                //进入到这里代表没有权限.
////                Log.e("c","进入到这里代表没有权限.");
//            }
    }
//        String tmpList[] = new String[toApplyList.size()];
//        if (!toApplyList.isEmpty()) {
//            ActivityCompat.requestPermissions(this, toApplyList.toArray(tmpList), 123);
//        }

    //    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //把申请权限的回调交由EasyPermissions处理
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    //下面两个方法是实现EasyPermissions的EasyPermissions.PermissionCallbacks接口
    //分别返回授权成功和失败的权限
    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        Log.i("Main", "获取成功的权限" + perms);
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        Log.i("Main", "获取失败的权限" + perms);
    }
}
