package com.heaven7.android.util2;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Build;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by heaven7 on 2016/6/20.
 */
public class NetworkCompatUtil {

    public static boolean isWifiConnected(Context context) {
        List<NetworkInfo> list = getConnectedNetworks(context);
        if (list == null) {
            return false;
        }
        for (NetworkInfo info : list) {
            if (info.getState() == NetworkInfo.State.CONNECTED && info.getType() == ConnectivityManager.TYPE_WIFI) {
                return true;
            }
        }
        return false;
    }


    public static boolean haveNetwork(Context context) {
        List<NetworkInfo> list = getConnectedNetworks(context);
        return list != null && list.size() > 0;
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
                for (int i = 0; i < networkInfoList.length; i++) {
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
