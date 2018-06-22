

package org.lemon.shoutscreen;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;

import org.lemon.commons.Application;
import org.lemon.commons.CoolToast;
import org.lemon.commons.HanyuPinyinHelper;
import org.lemon.commons.MqttService;
import org.lemon.shoutscreen.util.ShoutScreenUtil;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Random;

//import android.support.v7.app

/*
 * MainActivity class that loads MainFragment
 */
public class MainActivity extends Activity {
    @ViewInject(R.id.main_gridView)
    private GridView main_gridView;
    @ViewInject(R.id.main_textView)
    private TextView main_textView;
    @ViewInject(R.id.main_page_textView)
    private TextView main_page_textView;
    private GridAdapter gridAdapter;
    private UpdateUIBroadcastReceiver broadcastReceiver;
    public static final String ACTION_SCREEN = "org.lemon.commons.MqttService";
    private List<Map<String, Object>> returnData = new ArrayList<Map<String, Object>>();
    private List<Map<String, Object>> showData = new ArrayList<Map<String, Object>>();
    private List<Map<String, Object>> nowData = new ArrayList<Map<String, Object>>();
    private List<Map<String, Object>> finelData = new ArrayList<Map<String, Object>>();
    private HanyuPinyinHelper hanyuPinyinHelper = new HanyuPinyinHelper();
    private int ct_of_page = 24;
    private int nowPage = 0;
    private int pageCount = 0;
    private boolean hasUpdate = false;
    // 定义一个变量，来标识是否退出
    private static boolean isExit = false;
//    private CountDownTimer changeUITime = new CountDownTimer(9999999 * 1000, 8 * 1000) {
//        @Override
//        public void onTick(long l) {
//
//        }
//
//        @Override
//        public void onFinish() {
////            Log.i("页码", );
//        }
//    };

    private CountDownTimer changeUI1 = new CountDownTimer(6 * 1000, 1 * 1000) {
        @Override
        public void onTick(long l) {

        }

        @Override
        public void onFinish() {
            main_textView.setText("请以下人员前来取药");
        }
    };
    private MyAsyncTask task = null;

    private boolean running = true;

    public class MyAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            while (running) {
                try {
                    // 处理耗时操作。我们这边线程休眠1秒实现秒表的效果
                    publishProgress();//类似于发消息给主线程，更新UI
                    Thread.sleep(8000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            // 为主线程 实现我们刷新UI的逻辑
            super.onProgressUpdate(values);
            finelView();
        }

    }

    private void startTask() {
        stopTask();
        running = true;
        task = (MyAsyncTask) new MyAsyncTask().execute();
    }

    private void stopTask() {
        if (task != null) {
            running = false;
            task.cancel(true);
            task = null;
        }
    }

    private void finelView() {
        nowData = new ArrayList<Map<String, Object>>();
        if (hasUpdate && nowPage == 0) {
            Log.e("修改", ">");
            showData = new ArrayList<Map<String, Object>>();
            showData.addAll(finelData);
            Log.e("update", String.valueOf(showData.size()));
            updateUI();
            hasUpdate = false;
            nowPage = 0;
            finelView();
//                startTask();
//                return;
        } else {
            if (nowPage < pageCount - 1) {
                Log.i("现在页码", String.valueOf(nowPage));
                for (int n = nowPage * ct_of_page; n < (nowPage + 1) * ct_of_page; n++) {
//                    Log.i("现在个数", String.valueOf(n));
                    nowData.add(showData.get(n));
                }
                nowPage++;
                main_page_textView.setText(nowPage + "/" + pageCount);
            } else if (nowPage == pageCount - 1) {
                Log.i("现在页码1", String.valueOf(nowPage));
                for (int n = nowPage * ct_of_page; n < showData.size(); n++) {
                    nowData.add(showData.get(n));
                }
                nowPage = 0;
                main_page_textView.setText(pageCount + "/" + pageCount);
            }
            gridAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        x.view().inject(this);
        Application.imei = getDeviceInfo();
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


    private void updateUI() {
        pageCount = showData.size() / ct_of_page;
        if (showData.size() % ct_of_page != 0) {
            pageCount++;
        }
        //按照拼音排序
        Collections.sort(showData, new Comparator<Map>() {
            @Override
            public int compare(Map map1, Map map2) {
                return String.valueOf(map1.get("py")).compareTo(String.valueOf(map2.get("py")));
            }
        });

        changeUI1.cancel();
        changeUI1.start();
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
                            Map mp = returnData.get(x);
                            mp.put("py", hanyuPinyinHelper.getFirstALLLetter(String.valueOf(mp.get("patientName"))));
                            addMap(showData, mp);
                        }
                        finelData.addAll(showData);
                        updateUI();
                        startTask();
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
            try {
                String mes = intent.getStringExtra("people");
                Map map = JSON.parseObject(mes, Map.class);
                if (null != map && null != map.get("status")) {
                    if (0 == Integer.parseInt(String.valueOf(map.get("status")))) {
//               new ChineseToSpeech().speech("请"+map.get("patientName") + "前来取药");
                        main_textView.setText("请 " + map.get("patientName") + " 前来取药");
                        Application.speakChinese("请  " + map.get("patientName") + "  前来取药");
                        map.put("py", hanyuPinyinHelper.getFirstALLLetter(String.valueOf(map.get("patientName"))));
//                        returnData.add(map);
                        addMap(finelData, map);
                        Log.e("add", String.valueOf(finelData.size()));
//                        updateUI();
                    } else {
                        removeMap(finelData, map);
                        Log.e("remove", String.valueOf(finelData.size()));
//                        removeMap(showData, map);
//                        updateUI();
                    }
                    hasUpdate = true;
                }
            } catch (Exception e) {
                new CoolToast(getBaseContext()).show("接收数据异常");
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
//        int z = 0;
//        for (int x = 0; x < returnData.size(); x++) {
//            if (map.get("patientId").equals(returnData.get(x).get("patientId"))) {
//                if (map.get("presId").equals(returnData.get(x).get("presId"))) {
//                    returnData.remove(x);
//                }
//                z++;
//            }
//        }
//        if (z == 1) {
            for (int x = 0; x < list.size(); x++) {
                if (map.get("patientId").equals(list.get(x).get("patientId"))) {
                    list.remove(x);
                    break;
                }
            }
//        }
        System.out.println(returnData.size() + ">>" + list.size());
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(broadcastReceiver);
        super.onDestroy();
        stopTask();
        changeUI1.cancel();
        changeUI1.onFinish();
    }

    @SuppressLint("MissingPermission")
    private String getDeviceInfo() {
        TelephonyManager mTm = (TelephonyManager) this.getSystemService(TELEPHONY_SERVICE);
        if (null == mTm) {
            return mTm.getDeviceId();
        }
        return new Random(100).toString();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exit();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            isExit = false;
        }
    };

    private void exit() {
        if (!isExit) {
            isExit = true;
            new CoolToast(getBaseContext()).show("再按一次退出程序");
            mHandler.sendEmptyMessageDelayed(0, 2000);
        } else {
            finish();
            System.exit(0);
        }
    }
}
