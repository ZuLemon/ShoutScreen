package org.lemon.shoutscreen.util;

import android.media.AudioManager;
import android.util.Log;

import com.baidu.tts.auth.AuthInfo;
import com.baidu.tts.client.SpeechSynthesizer;
import com.baidu.tts.client.SpeechSynthesizerListener;
import com.baidu.tts.client.TtsMode;

import org.lemon.commons.Application;
import org.lemon.commons.CoolToast;
import org.lemon.shoutscreen.control.InitConfig;
import org.lemon.shoutscreen.control.MySyntherizer;
import org.lemon.shoutscreen.control.NonBlockSyntherizer;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Lemon on 2017/10/1.
 */

public class SpeakUtil {
    private static final String TEXT = "欢迎使用百度语音合成";
    /**
     * 发布时请替换成自己申请的appId appKey 和 secretKey。注意如果需要离线合成功能,请在您申请的应用中填写包名。
     * 本demo的包名是com.baidu.tts.sample，定义在build.gradle中。
     */
    private String appId = "10207612";

    private String appKey = "glegB0CDNrAk6vfG8zhapTxj";

    private String secretKey = "pOGRw4scZkjeh8NTOso5lOZaZALilmH7";

//    private String appId = "10207612";
//
//    private String appKey = "glegB0CDNrAk6vfG8zhapTxj";
//
//    private String secretKey = "pOGRw4scZkjeh8NTOso5lOZaZALilmH7";
    // ================选择TtsMode.ONLINE  不需要设置以下参数; 选择TtsMode.MIX 需要设置下面2个离线资源文件的路径
//    private static String TEMP_DIR = "/sdcard/baiduTTS"; //重要！请手动将assets目录下的3个dat 文件复制到该目录
//
//    private static String TEXT_FILENAME = TEMP_DIR + "/" + "bd_etts_text.dat"; // 请确保该PATH下有这个文件
//
//    private static String MODEL_FILENAME = TEMP_DIR + "/" + "bd_etts_speech_male.dat"; // 请确保该PATH下有这个文件 male是男声 female女声

    // ===============初始化参数设置完毕，更多合成参数请至getParams()方法中设置 =================
    // TtsMode.MIX; 离在线融合，在线优先； TtsMode.ONLINE 纯在线； 没有纯离线
    protected TtsMode ttsMode = TtsMode.MIX;

    // 离线发音选择，VOICE_FEMALE即为离线女声发音。
    // assets目录下bd_etts_speech_female.data为离线男声模型；bd_etts_speech_female.data为离线女声模型
    protected String offlineVoice = OfflineResource.VOICE_FEMALE;

    // ===============初始化参数设置完毕，更多合成参数请至getParams()方法中设置 =================

    // 主控制类，所有合成控制方法从这个类开始
    protected MySyntherizer synthesizer;
    protected SpeechSynthesizer mSpeechSynthesizer;

    public SpeakUtil(){
        initialTts();
    }

    protected void initialTts() {
        // 设置初始化参数
        SpeechSynthesizerListener listener = new MessageListener(); // 此处可以改为 含有您业务逻辑的SpeechSynthesizerListener的实现类

        Map<String, String> params = getParams();

        // appId appKey secretKey 网站上您申请的应用获取。注意使用离线合成功能的话，需要应用中填写您app的包名。包名在build.gradle中获取。
        InitConfig initConfig = new InitConfig(appId, appKey, secretKey, ttsMode, offlineVoice, params, listener);

        synthesizer = new NonBlockSyntherizer(Application.getContext(), initConfig); // 此处可以改为MySyntherizer 了解调用过程
    }
    /**
     * 合成的参数，可以初始化时填写，也可以在合成前设置。
     *
     * @return
     */
    protected Map<String, String> getParams() {
        Map<String, String> params = new HashMap<String, String>();
        // 以下参数均为选填
        params.put(SpeechSynthesizer.PARAM_SPEAKER, "0"); // 设置在线发声音人： 0 普通女声（默认） 1 普通男声 2 特别男声 3 情感男声<度逍遥> 4 情感儿童声<度丫丫>
        params.put(SpeechSynthesizer.PARAM_VOLUME, "9"); // 设置合成的音量，0-9 ，默认 5
        params.put(SpeechSynthesizer.PARAM_SPEED, "5");// 设置合成的语速，0-9 ，默认 5
        params.put(SpeechSynthesizer.PARAM_PITCH, "5");// 设置合成的语调，0-9 ，默认 5
        params.put(SpeechSynthesizer.PARAM_MIX_MODE, SpeechSynthesizer.MIX_MODE_HIGH_SPEED_SYNTHESIZE_WIFI);         // 该参数设置为TtsMode.MIX生效。即纯在线模式不生效。
        // MIX_MODE_DEFAULT 默认 ，wifi状态下使用在线，非wifi离线。在线状态下，请求超时6s自动转离线
        // MIX_MODE_HIGH_SPEED_SYNTHESIZE_WIFI wifi状态下使用在线，非wifi离线。在线状态下， 请求超时1.2s自动转离线
        // MIX_MODE_HIGH_SPEED_NETWORK ， 3G 4G wifi状态下使用在线，其它状态离线。在线状态下，请求超时1.2s自动转离线
        // MIX_MODE_HIGH_SPEED_SYNTHESIZE, 2G 3G 4G wifi状态下使用在线，其它状态离线。在线状态下，请求超时1.2s自动转离线
        return params;
    }



    /**
     * 检查 TEXT_FILENAME, MODEL_FILENAME 这2个文件是否存在，不存在请自行从assets目录里手动复制
     * @return
     */
//    private boolean checkOfflineResources() {
//        String[] filenames = {TEXT_FILENAME, MODEL_FILENAME};
//        for (String path : filenames) {
//            File f = new File(path);
//            if (!f.canRead()) {
//                Log.e("speak","[ERROR] 文件不存在或者不可读取，请从assets目录复制改文件到：" + path);
//                return false;
//            }
//        }
//        return true;
//    }

    public void speak(String text) {
        /* 以下参数每次合成时都可以修改
         *  mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_SPEAKER, "0"); // 设置在线发声音人： 0 普通女声（默认） 1 普通男声 2 特别男声 3 情感男声<度逍遥> 4 情感儿童声<度丫丫>
         *  mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_VOLUME, "5"); // 设置合成的音量，0-9 ，默认 5
         *  mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_SPEED, "5");// 设置合成的语速，0-9 ，默认 5
         *  mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_PITCH, "5");// 设置合成的语调，0-9 ，默认 5
         *
         *  mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_MIX_MODE, SpeechSynthesizer.MIX_MODE_DEFAULT);
         *  // MIX_MODE_DEFAULT 默认 ，wifi状态下使用在线，非wifi离线。在线状态下，请求超时6s自动转离线
         * // MIX_MODE_HIGH_SPEED_SYNTHESIZE_WIFI wifi状态下使用在线，非wifi离线。在线状态下， 请求超时1.2s自动转离线
         *  // MIX_MODE_HIGH_SPEED_NETWORK ， 3G 4G wifi状态下使用在线，其它状态离线。在线状态下，请求超时1.2s自动转离线
         * // MIX_MODE_HIGH_SPEED_SYNTHESIZE, 2G 3G 4G wifi状态下使用在线，其它状态离线。在线状态下，请求超时1.2s自动转离线
         */

        Log.e("speak","合成并播放 按钮已经点击");
        int result = synthesizer.speak(text);
        checkResult(result, "speak");
    }

    private void stop() {
        Log.e("speak","停止合成引擎 按钮已经点击");
        int result = synthesizer.stop();
        checkResult(result, "stop");
    }
    private void checkResult(int result, String method) {
        if (result != 0) {
            new CoolToast(Application.getContext()).show(result + " method:" + method);
            Log.e("speak","error code :" + result + " method:" + method + ", 错误码文档:http://yuyin.baidu.com/docs/tts/122 ");
        }
    }
}
