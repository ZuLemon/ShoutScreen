

package org.lemon.shoutscreen;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;
//import android.support.v7.app
import com.alibaba.fastjson.JSON;

import org.lemon.commons.Application;
import org.lemon.commons.MqttService;
import org.lemon.shoutscreen.util.ShoutScreenUtil;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import pub.devrel.easypermissions.EasyPermissions;

/*
 * MainActivity class that loads MainFragment
 */
public class MainActivity extends Activity implements EasyPermissions.PermissionCallbacks {
    @ViewInject(R.id.main_gridView)
    private GridView main_gridView;
    @ViewInject(R.id.main_textView)
    private TextView main_textView;
    private GridAdapter gridAdapter;
    private UpdateUIBroadcastReceiver broadcastReceiver;
    public static final String ACTION_SCREEN = "org.lemon.commons.MqttService";
    private List<Map<String, Object>> returnData = new ArrayList<Map<String, Object>>();
    private List<Map<String, Object>> showData = new ArrayList<Map<String, Object>>();
    private List<Map<String, Object>> nowData = new ArrayList<Map<String, Object>>();
    private int ct_of_page = 24;
    private int nowPage = 0;
    private int pageCount = 0;
    private CountDownTimer changeUITime = new CountDownTimer(9999999 * 1000, 7 * 1000) {
        @Override
        public void onTick(long l) {
            nowData = new ArrayList<Map<String, Object>>();
            if (nowPage < pageCount - 1) {
//                Log.i("现在页码", String.valueOf(nowPage));
                for (int n = nowPage * ct_of_page; n < (nowPage + 1) * ct_of_page; n++) {
//                    Log.i("现在个数", String.valueOf(n));
                    nowData.add(showData.get(n));
                }
                nowPage++;
            } else if (nowPage == pageCount - 1) {
                for (int n = nowPage * ct_of_page; n < showData.size(); n++) {
                    nowData.add(showData.get(n));
                }
                nowPage = 0;
            }
            gridAdapter.notifyDataSetChanged();
        }

        @Override
        public void onFinish() {
//            Log.i("页码", );
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        x.view().inject(this);
        initPermission();
        Application.imei=getDeviceInfo();
        main_textView.setText("请以下人员前来取药");
        Application.speakChinese("系统初始化完毕");
        // 动态注册广播
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_SCREEN);
        broadcastReceiver = new UpdateUIBroadcastReceiver();
        registerReceiver(broadcastReceiver, filter);
        gridAdapter = new GridAdapter(this);
        main_gridView.setAdapter(gridAdapter);
        selectPresThread(0);
    }

    private void nameUtil(List shoutList) {
//        for (Object o:shoutList
//             ) {
//            Map ma=(Map)o;
////            ma.get("patientName");
//        }
//        HashMap<String, Object> map;
//        for (int x = 0; x <62 ; x++) {
//            map= new HashMap<String, Object>();
//            map.put("name","朗读者"+x);
//            returnData.add(map);
//        }
    }

    private void updateUI() {
        pageCount = showData.size() / ct_of_page;
        if (showData.size() % ct_of_page != 0) {
            pageCount++;
        }
//        Log.i("页码", String.valueOf(returnData.size()));
        changeUITime.cancel();
        changeUITime.start();
    }

    private void selectPresThread(final int what) {
        final Message message = new Message();
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case -1:
                        break;
                    case 0:
                        returnData = (List) msg.obj;
                        for (int x = 0; x < returnData.size(); x++) {
                            addMap(showData, returnData.get(x));
                        }
                        updateUI();
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
                                checkService();
                                message.what = 0;
                                message.obj = new ShoutScreenUtil().findByStatus();
                                handler.sendMessage(message);
                                break;
                        }
                    } catch (Exception e) {
                        Log.e("Thread1", e.getMessage());
                        message.what = -1;
                        message.obj = e.getMessage();
                        handler.sendMessage(message);
                    }
                }
            }.start();
        } catch (Exception e) {
            Log.e("Thread", e.getMessage());
        }
    }

    private class GridAdapter extends BaseAdapter {
        private LayoutInflater inflater;

        public GridAdapter(Context context) {
            this.inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return nowData.size();
        }

        @Override
        public Object getItem(int i) {
            return i;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            final Items items;
            if (view == null) {
                view = inflater.inflate(R.layout.griditem, viewGroup, false);
                items = new Items();
                items.name_textView = (TextView) view.findViewById(R.id.name_textView);
                view.setTag(items);
            } else {
                items = (Items) view.getTag();
            }
            Map map = (Map) nowData.get(i);
            String name = String.valueOf(map.get("patientName"));
            if (name.length() > 4) {
                name = name.substring(0, 4);
            }
            items.name_textView.setText("" + name);
            return view;
        }

        private class Items {
            private TextView name_textView;
        }
    }

    public void checkService() {
        boolean isRun = false;
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if ("org.lemon.commons.MqttService".equals(service.service.getClassName())) {
                isRun = true;
            }
        }
        if (isRun) {
            Log.e(">>已经存在此服务", "#########");
            MqttService.disconnect();
            MqttService.connect();
        } else {
            Log.e(">>不存在服务新建", "#########");
            //启动推送服务
            startService(new Intent(MainActivity.this, MqttService.class));
        }
    }

    private class UpdateUIBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
//            Bundle bundle = getIntent().getExtras();
//            SerializableMap serializableMap = (SerializableMap) bundle.get("people");
//            Map map=serializableMap.getMap();
            String mes = intent.getStringExtra("people");
            Map map = JSON.parseObject(mes, Map.class);
            if (0 == Integer.parseInt(String.valueOf(map.get("status")))) {
//               new ChineseToSpeech().speech("请"+map.get("patientName") + "前来取药");
                main_textView.setText("请 " + map.get("patientName") + " 前来取药");
                Application.speakChinese("请" + map.get("patientName") + "前来取药");
                returnData.add(map);
                addMap(showData, map);
                updateUI();
            } else {
                removeMap(showData, map);
                updateUI();
            }
        }
    }

    private void addMap(List<Map<String, Object>> list, Map map) {
        boolean have = true;
        for (int x = 0; x < list.size(); x++) {
            if (map.get("patientId").equals(list.get(x).get("patientId"))) {
                have = false;
                break;
            }
        }
        if (have) {
            list.add(map);
        }
        System.out.println(returnData.size() + ">>" + list.size());

    }

    private void removeMap(List<Map<String, Object>> list, Map map) {
        int z = 0;
        for (int x = 0; x < returnData.size(); x++) {
            if (map.get("patientId").equals(returnData.get(x).get("patientId"))) {
                if (map.get("presId").equals(returnData.get(x).get("presId"))) {
                    returnData.remove(x);
                }
                z++;
            }
        }
        if (z == 1) {
            for (int x = 0; x < list.size(); x++) {
                if (map.get("patientId").equals(list.get(x).get("patientId"))) {
                    list.remove(x);
                }
            }
        }
        System.out.println(returnData.size() + ">>" + list.size());
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(broadcastReceiver);
        super.onDestroy();
        changeUITime.cancel();
        changeUITime.onFinish();
    }
    @SuppressLint("MissingPermission")
    private String getDeviceInfo() {
        TelephonyManager mTm = (TelephonyManager) this.getSystemService(TELEPHONY_SERVICE);
        if (null == mTm) {
            return mTm.getDeviceId();
        }
        return new Random(100).toString();
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
