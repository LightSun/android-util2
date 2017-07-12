package com.heaven7.android.util2;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * a compat network util.
 * Created by heaven7 on 2016/6/20.
 */
public class NetworkCompatUtil {

    /**
     * indicate is wifi connected or not.
     * @param context the context
     * @return true if wifi is connected.
     */
    public static boolean isWifiConnected(Context context) {
        return getConnectedNetworkByType(context,ConnectivityManager.TYPE_WIFI) != null;
    }

    /**
     * indicate current has connected network or not.
     * @param context the context.
     * @return true if has connected network, false otherwise.
     */
    public static boolean hasConnectedNetwork(Context context) {
        List<NetworkInfo> list = getConnectedNetworks(context);
        return list != null && list.size() > 0;
    }

    /**
     * get connected network by the target type.
     * @param context the context
     * @param networkType the network type . see {@linkplain ConnectivityManager#TYPE_WIFI} and etc.
     * @return the target network info.
     */
    public static @Nullable NetworkInfo getConnectedNetworkByType(Context context, int networkType) {
        List<NetworkInfo> list = getConnectedNetworks(context);
        if (list == null) {
            return null;
        }
        for (NetworkInfo info : list) {
            if (info.getState() == NetworkInfo.State.CONNECTED && info.getType() == networkType) {
                return info;
            }
        }
        return null;
    }

    private static List<NetworkInfo> getConnectedNetworks(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) {
            return null;
        }
        final List<NetworkInfo> list = new ArrayList<NetworkInfo>();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            NetworkInfo[] networkInfoList = cm.getAllNetworkInfo();
            if (networkInfoList != null) {
                final int length = networkInfoList.length;
                for (int i = 0; i < length; i++) {
                    if (networkInfoList[i].getState() == NetworkInfo.State.CONNECTED) {
                        list.add(networkInfoList[i]);
                    }
                }
            }
        } else {
            final Network[] networks = cm.getAllNetworks();
            if (networks != null && networks.length > 0) {
                NetworkInfo info;
                for (Network network : networks) {
                    info = cm.getNetworkInfo(network);
                    if (info != null && info.getState() == NetworkInfo.State.CONNECTED) {
                        list.add(info);
                    }
                }
            }
        }
        return list;
    }

}
