package com.heaven7.android.util2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

/**
 * the common receiver callback.
 * Created by heaven7 on 2017/7/5 0005.
 */

public class ReceiverHelper {

    private final Context mContext;
    private ReceiverCallback mCallback;
    private InternalReceiver mReceiver;

    public ReceiverHelper(Context mContext) {
        this.mContext = mContext;
    }

    public ReceiverCallback getReceiverCallback() {
        return mCallback;
    }
    public void setReceiverCallback(ReceiverCallback callback) {
        this.mCallback = callback;
    }

    public void register(){
        if(mReceiver != null) {
            mReceiver = new InternalReceiver();
            mContext.registerReceiver(mReceiver, mCallback.buildIntentFilter(mContext));
        }
    }

    public void unregister(){
        if(mReceiver != null) {
            mContext.unregisterReceiver(mReceiver);
            mReceiver = null;
        }
    }

    private class InternalReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
             if(intent == null){
                return;//ignore
             }
             if(mCallback != null){
                 mCallback.onReceive(context, intent.getAction(),  intent);
             }
        }
    }

    public static abstract class ReceiverCallback{

        public IntentFilter buildIntentFilter(Context context){
            IntentFilter filter = new IntentFilter();
            filter.addAction(Intent.ACTION_SCREEN_OFF);
            filter.addAction(Intent.ACTION_SCREEN_ON);
            return filter;
        }

        public abstract void onReceive(Context context, String action, Intent intent);
    }

    public static class SimpleReceiverCallback extends ReceiverCallback{

        @Override
        public void onReceive(Context context, String action, Intent intent) {
            switch (action){
                case Intent.ACTION_SCREEN_OFF:
                    onScreenOff(context);
                    break;
                case Intent.ACTION_SCREEN_ON:
                    onScreenOn(context);
                    break;
                default:
                    onReceiveOthers(context, action, intent);
            }
        }
        protected void onReceiveOthers(Context context, String action, Intent intent) {

        }
        protected void onScreenOn(Context context) {

        }
        protected void onScreenOff(Context context) {

        }
    }
}
