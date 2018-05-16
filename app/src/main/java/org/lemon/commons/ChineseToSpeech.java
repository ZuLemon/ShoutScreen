package org.lemon.commons;

import android.speech.tts.TextToSpeech;


import java.util.Locale;

/**
 * Created by Administrator on 2014-11-21.
 * 中文朗读
 */
public class ChineseToSpeech {
    private TextToSpeech textToSpeech;

    public ChineseToSpeech() {
        this.textToSpeech = new TextToSpeech(Application.getContext(), new TextToSpeech.OnInitListener() {
            //  @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int result = textToSpeech.setLanguage(Locale.CHINA);
                    if (result == TextToSpeech.LANG_MISSING_DATA
                            || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        new CoolToast(Application.getContext()).show("不支持朗读功能");
                    }
                }
            }
        });
    }

    public void speech(String text) {
        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }

    public void destroy() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
    }
}

