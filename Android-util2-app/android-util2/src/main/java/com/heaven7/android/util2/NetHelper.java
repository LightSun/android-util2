package com.heaven7.android.util2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

import com.heaven7.core.util.Logger;

import java.util.ArrayList;

/**
 * the network helper : help we handle handle intent action --> 'android.net.conn.CONNECTIVITY_CHANGE'.
 * @author  heaven7
 */
public final class NetHelper extends BroadcastReceiver {

    /** indicate no network */
    public static final byte STATE_NO = 1;
    /** indicate 2g network */
    public static final byte STATE_2G = 2;
    /** indicate 3g network */
    public static final byte STATE_3G = 3;
    /** indicate 4g network */
    public static final byte STATE_4G = 4;
    /** indicate wifi network */
    public static final byte STATE_WIFI = 5;
    /** indicate unknown network */
    public static final byte STATE_UNKNOWN = 6;

    private static final String TAG = "NetHelper";
    private static final IntentFilter sIntentFilter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
    private final ArrayList<OnNetStateChangedListener> mListeners;
    private volatile boolean mRegistered;

    /**
     * get the net state string.
     * @param netState the net state.
     * @return the net string.
     */
    public static String getNetStateString(byte netState){
        switch (netState){
            case STATE_2G:
                return "2G";
            case STATE_3G:
                return "3G";
            case STATE_4G:
                return "4G";

            case STATE_NO:
                return "NO";
            case STATE_WIFI:
                return "WIFI";
            case STATE_UNKNOWN:
                return "Unknown";
        }
        return null;
    }

    /**
     * the network state change listener.
     */
    public interface OnNetStateChangedListener {

        /**
         * called on net state changed.
         * @param context the context.
         * @param netState the net state.  see {@linkplain #STATE_WIFI} and etc.
         */
        void onNetStateChanged(Context context, byte netState);
    }

    public NetHelper() {
        mListeners = new ArrayList<>();
    }

    /**
     * register the net state change receiver.
     * @param context the context.
     */
    public void register(Context context) {
        try {
            context.registerReceiver(this, sIntentFilter);
            mRegistered = true;
        } catch (Exception e) {
            Logger.w(TAG, "register", Logger.toString(e));
        }

    }
    /**
     * unregister the net state change receiver.
     * @param context the context.
     */
    public void unregister(Context context) {
        if (mRegistered) {
            try {
                context.unregisterReceiver(this);
                mRegistered = false;
            } catch (Exception e) {
                Logger.w(TAG, "unregister", Logger.toString(e));
            }
        }
    }

    /**
     * add a net state change listener.
     * @param listener the listener.
     */
    public void addOnNetStateChangedListener(OnNetStateChangedListener listener) {
        if (listener == null) {
            return;
        }
        if (!mListeners.contains(listener)) {
            mListeners.add(listener);
        }
    }
    /**
     * remove a net state change listener.
     * @param listener the listener.
     */
    public void removeOnNetStateChangedListener(OnNetStateChangedListener listener) {
        if (listener == null) {
            return;
        }
        mListeners.remove(listener);
    }

    /**
     * clear net state changed listeners.
     */
    public void clearListeners() {
        mListeners.clear();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        final byte code = getCurrentNetState(context);
        for (OnNetStateChangedListener listener : mListeners) {
            listener.onNetStateChanged(context, code);
        }
    }

    /**
     * get the net state of current
     * @param context the context.
     * @return the state code. see {@linkplain #STATE_2G} and etc.
     */
    public static byte getCurrentNetState(Context context) {
        byte code = STATE_NO;
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni != null && ni.isConnectedOrConnecting()) {
            switch (ni.getType()) {
                //wifi
                case ConnectivityManager.TYPE_WIFI:
                    code = STATE_WIFI;
                    break;
                //mobile 网络
                case ConnectivityManager.TYPE_MOBILE:
                    switch (ni.getSubtype()) {
                        case TelephonyManager.NETWORK_TYPE_GPRS: //联通2g
                        case TelephonyManager.NETWORK_TYPE_CDMA: //电信2g
                        case TelephonyManager.NETWORK_TYPE_EDGE: //移动2g
                        case TelephonyManager.NETWORK_TYPE_1xRTT:
                        case TelephonyManager.NETWORK_TYPE_IDEN:
                            code = STATE_2G;
                            break;
                        case TelephonyManager.NETWORK_TYPE_EVDO_A: //电信3g
                        case TelephonyManager.NETWORK_TYPE_UMTS:
                        case TelephonyManager.NETWORK_TYPE_EVDO_0:
                        case TelephonyManager.NETWORK_TYPE_HSDPA:
                        case TelephonyManager.NETWORK_TYPE_HSUPA:
                        case TelephonyManager.NETWORK_TYPE_HSPA:
                        case TelephonyManager.NETWORK_TYPE_EVDO_B:
                        case TelephonyManager.NETWORK_TYPE_EHRPD:
                        case TelephonyManager.NETWORK_TYPE_HSPAP:
                            code = STATE_3G;
                            break;
                        case TelephonyManager.NETWORK_TYPE_LTE://4G
                            code = STATE_4G;
                            break;

                        default:
                            code = STATE_UNKNOWN;
                    }
                    break;
                default:
                    code = STATE_UNKNOWN;
            }
        }
        return code;
    }
}
