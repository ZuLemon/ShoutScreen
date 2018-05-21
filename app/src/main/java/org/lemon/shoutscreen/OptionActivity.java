package org.lemon.shoutscreen;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;


import org.lemon.commons.AppOption;
import org.lemon.commons.CoolToast;
import org.lemon.commons.HttpUtil;
import org.lemon.commons.MqttService;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

/**
 * Created by Administrator on 2014-11-10.
 * 设置系统参数
 */
public class OptionActivity extends Activity {
    @ViewInject(R.id.option_server_editText)
    private EditText option_server_editText;
    @ViewInject(R.id.option_mqttserver_editText)
    private EditText option_mqttserver_editText;
    @ViewInject(R.id.option_mqttuser_editText)
    private EditText option_mqttuser_editText;
    @ViewInject(R.id.option_mqttpassword_editText)
    private EditText option_mqttpassword_editText;
    private AppOption appOption = new AppOption();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.option);
        x.view().inject(this);
        option_server_editText.setText(appOption.getOption(appOption.APP_OPTION_SERVER));
        option_mqttserver_editText.setText(appOption.getOption(AppOption.APP_OPTION_MQTTSERVER));
        option_mqttuser_editText.setText(appOption.getOption(AppOption.APP_OPTION_MQTTUSER));
        option_mqttpassword_editText.setText(appOption.getOption(AppOption.APP_OPTION_MQTTPASSWORD));
    }
    @Event(value = {R.id.option_server_button}, type = View.OnClickListener.class)
    private void onClick(View v) {
        switch (v.getId()) {
            case R.id.option_server_button:
                HttpUtil.setUri(String.valueOf(option_server_editText.getText()).trim());
                appOption.setOption(AppOption.APP_OPTION_SERVER, String.valueOf(option_server_editText.getText()));
                appOption.setOption(AppOption.APP_OPTION_MQTTSERVER,String.valueOf(option_mqttserver_editText.getText()));
                appOption.setOption(AppOption.APP_OPTION_MQTTUSER,String.valueOf(option_mqttuser_editText.getText()));
                appOption.setOption(AppOption.APP_OPTION_MQTTPASSWORD,String.valueOf(option_mqttpassword_editText.getText()));
                MqttService.setUrl(String.valueOf(option_mqttserver_editText.getText()));
                MqttService.setUser(String.valueOf(option_mqttuser_editText.getText()));
                MqttService.setPassword(String.valueOf(option_mqttpassword_editText.getText()));
                new CoolToast(getBaseContext()).show("保存成功");
                break;
          
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
