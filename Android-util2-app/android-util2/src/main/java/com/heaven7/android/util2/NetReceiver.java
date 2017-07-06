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
import java.util.List;

public class NetReceiver extends BroadcastReceiver {

    private static final String TAG = NetReceiver.class.getSimpleName();
    private static final IntentFilter sIntentFilter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
    private List<NetStateChangedListener> mListeners;
    private volatile boolean mRegisted;

    /**
     * 枚举网络状态
     * NET_NO：没有网络 , NET_2G:2g网络 , NET_3G：3g网络 ,NET_4G：4g网络 ,NET_WIFI：wifi , NET_UNKNOWN：未知网络
     */
    public enum NetState {
        NET_NO {
            @Override
            public String toString() {
                return "NO";
            }
        },
        NET_2G {
            @Override
            public String toString() {
                return "2G";
            }
        },
        NET_3G {
            @Override
            public String toString() {
                return "3G";
            }
        },
        NET_4G {
            @Override
            public String toString() {
                return "4g";
            }
        },
        NET_WIFI {
            @Override
            public String toString() {
                return "wifi";
            }
        },
        NET_UNKNOWN {
            @Override
            public String toString() {
                return "unknown";
            }
        };

        public abstract String toString();
    }

    public interface NetStateChangedListener {

        void onNetStateChanged(Context context, NetState netCode);
    }

    public NetReceiver() {
        mListeners = new ArrayList<>();
    }

    public void registerReceiver(Context context) {
        try {
            context.registerReceiver(this, sIntentFilter);
            mRegisted = true;
        } catch (Exception e) {
            Logger.w(TAG,"registerReceiver", Logger.toString(e));
        }

    }

    public void unRegisterReceiver(Context context) {
        if (mRegisted) {
            try {
                context.unregisterReceiver(this);
                mRegisted = false;
            }catch (Exception e){
                Logger.w(TAG,"unRegisterReceiver", Logger.toString(e));
            }
        }
    }

    public void addNetStateChangeListener(NetStateChangedListener listener) {
        if (listener == null) {
            return;
        }
        if (!mListeners.contains(listener)) {
            mListeners.add(listener);
        }
    }

    public void removeNetStateChangeListener(NetStateChangedListener listener) {
        if (listener == null) {
            return;
        }
        mListeners.remove(listener);
    }

    public void clearListeners() {
        mListeners.clear();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        final NetState code = getCurrentNetStateCode(context);
        for (NetStateChangedListener listener : mListeners) {
            listener.onNetStateChanged(context, code);
        }
    }

    public NetState getCurrentNetStateCode(Context context) {
        NetState stateCode = NetState.NET_NO;
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni != null && ni.isConnectedOrConnecting()) {
            switch (ni.getType()) {
                //wifi
                case ConnectivityManager.TYPE_WIFI:
                    stateCode = NetState.NET_WIFI;
                    break;
                //mobile 网络
                case ConnectivityManager.TYPE_MOBILE:
                    switch (ni.getSubtype()) {
                        case TelephonyManager.NETWORK_TYPE_GPRS: //联通2g
                        case TelephonyManager.NETWORK_TYPE_CDMA: //电信2g
                        case TelephonyManager.NETWORK_TYPE_EDGE: //移动2g
                        case TelephonyManager.NETWORK_TYPE_1xRTT:
                        case TelephonyManager.NETWORK_TYPE_IDEN:
                            stateCode = NetState.NET_2G;
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
                            stateCode = NetState.NET_3G;
                            break;
                        case TelephonyManager.NETWORK_TYPE_LTE://4G
                            stateCode = NetState.NET_4G;
                            break;
                        //未知,一般不会出现
                        default:
                            stateCode = NetState.NET_UNKNOWN;
                    }
                    break;
                default:
                    stateCode = NetState.NET_UNKNOWN;
            }
        }
        return stateCode;
    }
}
