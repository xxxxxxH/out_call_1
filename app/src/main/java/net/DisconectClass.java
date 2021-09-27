package net;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Binder;
import android.os.IBinder;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.android.internal.telephony.ITelephony;

import java.lang.reflect.Method;

public class DisconectClass {
    String TAG = "ABCD";
    Context mContext;

    public DisconectClass(Context mContext) {
        this.mContext = mContext;
    }

    public void disconnectCall() {
        try {
        System.out.println(this.TAG + " INSIDE DISCONECT CALL  ");
        /*Class<?> telephonyClass = Class.forName("ITelephony");*/
        Class<?> telephonyClass = Class.forName("com.android.internal.telephony.ITelephony");
        Class<?> telephonyStubClass = telephonyClass.getClasses()[0];
        Class<?> serviceManagerClass = Class.forName("android.os.ServiceManager");
        Class<?> serviceManagerNativeClass = Class.forName("android.os.ServiceManagerNative");
        Method getService = serviceManagerClass.getMethod("getService", String.class);
        Method tempInterfaceMethod = serviceManagerNativeClass.getMethod("asInterface", IBinder.class);
        new Binder().attachInterface(null, "fake");
        Object tmpBinder = null;
        IBinder retbinder = (IBinder) getService.invoke(tempInterfaceMethod.invoke(null, tmpBinder), new Object[]{"phone"});
        telephonyClass.getMethod("endCall").invoke(telephonyStubClass.getMethod("asInterface", IBinder.class).invoke(null, retbinder));
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(this.TAG + "ERROR IN REJECT CALL : " + e.toString());
            Log.i("xxxxxxH","disconnectCall");
        }
    }

    public void rejectCall(Context context) {
        try {
        @SuppressLint("WrongConstant") TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService("phone");
        Method method = Class.forName(telephonyManager.getClass().getName()).getDeclaredMethod("getITelephony");
        method.setAccessible(true);
        ((ITelephony) method.invoke(telephonyManager, new Object[0])).endCall();
        } catch (Exception e) {
            Log.i("xxxxxxH","rejectCall");
        }
    }
}
