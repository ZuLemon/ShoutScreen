package org.lemon.commons;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.lemon.shoutscreen.R;


/**
 * Created by Administrator on 2014-11-09.
 * 自定义Toast
 */
public class CoolToast extends Toast {
    private View view;
    /**
     * Construct an empty Toast object.  You must call {@link #setView} before you
     * can call {@link #show}.
     *
     * @param context The context to use.  Usually your {@link android.app.Application}
     *                or {@link android.app.Activity} object.
     */
    public CoolToast(Context context) {
        super(context);
        view = LayoutInflater.from(context).inflate(R.layout.toast,null);
        this.setView(view);
        this.setGravity(Gravity.CENTER, 0, 0);
    }

    public void show(String content) {
        TextView textView = (TextView) view.findViewById(R.id.toast_text);
        textView.setText(content);
        this.setDuration(Toast.LENGTH_SHORT);
        this.show();
    }
}
