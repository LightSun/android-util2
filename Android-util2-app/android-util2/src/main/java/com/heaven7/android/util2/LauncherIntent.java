package com.heaven7.android.util2;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.heaven7.core.util.Logger;

/**
 * a class named launcher intent help we fast handle . start/launch activity/service/broadcast.
 * @author heaven7
 * @since 1.0.2
 */
public class LauncherIntent extends Intent {

    private final WeakContextOwner mWeakContext;

    /**
     * create the launcher intent by context and target class .
     * @param context the context
     * @param targetClass the target class.
     */
    protected LauncherIntent(Context context, Class<?> targetClass) {
        super(context, targetClass);
        this.mWeakContext = new WeakContextOwner(context);
    }
    /**
     * create the launcher intent by context and target class .
     * @param context the context
     * @param targetClass the target class.
     * @return the launcher intent.
     */
    public static LauncherIntent create(Context context, Class<?> targetClass){
        return new LauncherIntent(context, targetClass);
    }

    /**
     * start service by this Intent.
     */
    public void startService() {
        final Context context = getContext();
        if (context != null) {
            context.startService(this);
        }
    }
    /**
     * stop service by this Intent.
     */
    public void stopService() {
        final Context context = getContext();
        if (context != null) {
            context.stopService(this);
        }
    }

    /**
     * bind service by this intent with target parameter.
     * @param conn the connection
     * @param flags Operation options for the binding.  May be 0,
     *          {@link Context#BIND_AUTO_CREATE}, {@link Context#BIND_DEBUG_UNBIND},
     *          {@link Context#BIND_NOT_FOREGROUND}, {@link Context#BIND_ABOVE_CLIENT},
     *          {@link Context#BIND_ALLOW_OOM_MANAGEMENT}, or
     *          {@link Context#BIND_WAIVE_PRIORITY}.
     */
    public void bindService(@NonNull ServiceConnection conn, int flags) {
        final Context context = getContext();
        if (context != null) {
            context.bindService(this, conn, flags);
        }
    }

    /**
     * unbind the service.
     * @param conn the connection
     */
    public void unbindService(@NonNull ServiceConnection conn) {
        final Context context = getContext();
        if (context != null) {
            context.unbindService(conn);
        }
    }

    /**
     * start activity by this intent.
     */
    public void startActivity() {
        startActivity(null);
    }
    /**
     * start activity for result by this intent and target request code.
     * @param requestCode the request code
     */
    public void startActivityForResult(int requestCode) {
        final Context context = getContext();
        if (context != null && context instanceof Activity) {
            ((Activity) context).startActivityForResult(this, requestCode);
        }
    }

    /**
     * start activity with options.
     * @param options Additional options for how the Activity should be started.
     * May be null if there are no options.  See {@link android.app.ActivityOptions}
     * for how to build the Bundle supplied here; there are no supported definitions
     * for building it manually.
     */
    public void startActivity(@Nullable Bundle options) {
        final Context context = getContext();
        if (context == null) {
            return;
        }
        if (!(context instanceof Activity)) {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        if (options == null) {
            context.startActivity(this);
        } else {
            if (Build.VERSION.SDK_INT >= 16) {
                context.startActivity(this, options);
            } else {
                Logger.w("LauncherIntent", "startActivity", "not support");
            }
        }
    }

    /**
     * send broadcast by this intent.
     */
    public void sendBroadcast() {
        sendBroadcast(null);
    }
    /**
     * send broadcast by this intent.
     * @param receiverPermission (optional) String naming a permissions that
     *               a receiver must hold in order to receive your broadcast.
     *               If null, no permission is required.
     */
    public void sendBroadcast(@Nullable String receiverPermission) {
        final Context context = getContext();
        if (context != null) {
            context.sendBroadcast(this, receiverPermission);
        }
    }

    /**
     * send ordered broadcast by this intent.
     */
    public void sendOrderedBroadcast() {
        sendOrderedBroadcast(null);
    }
    /**
     * send ordered broadcast by this intent.
     * @param receiverPermission (optional) String naming a permissions that
     *               a receiver must hold in order to receive your broadcast.
     *               If null, no permission is required.
     *
     */
    public void sendOrderedBroadcast(@Nullable String receiverPermission) {
        final Context context = getContext();
        if (context != null) {
            context.sendOrderedBroadcast(this, receiverPermission);
        }
    }

    private Context getContext() {
        return mWeakContext.getContext();
    }

}