package net;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.media.session.MediaController;
import android.media.session.MediaSessionManager;
import android.os.Build;
import android.util.Log;
import android.view.KeyEvent;

import androidx.annotation.RequiresApi;

import net.service.NotificationReceiverService;


public class RecivedClass {
    Context mContext;

    public RecivedClass(Context mContext) {
        this.mContext = mContext;
    }

    @SuppressLint("WrongConstant")
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void sendHeadsetHookLollipop() {
        try {
            for (MediaController m : ((MediaSessionManager) this.mContext.getSystemService("media_session")).getActiveSessions(new ComponentName(this.mContext, NotificationReceiverService.class))) {
                if ("com.android.server.telecom".equals(m.getPackageName())) {
                    m.dispatchMediaButtonEvent(new KeyEvent(0, 79));
                    m.dispatchMediaButtonEvent(new KeyEvent(1, 79));
                    m.dispatchMediaButtonEvent(new KeyEvent(1, 79));
                    return;
                }
            }
        } catch (SecurityException e) {
            Log.i("xxxxxxH","sendHeadsetHookLollipop");
        }
    }
}
