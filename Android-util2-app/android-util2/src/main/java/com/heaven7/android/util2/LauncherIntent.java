package com.heaven7.android.util2;

import android.annotation.TargetApi;
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
    private AndroidSmartReference<IntentActionCallback> mRefCallback;

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
     * set the intent action callback.
     * @param callback the callback
     * @return this.
     * @since 1.1.0
     */
    public LauncherIntent setIntentActionCallback(IntentActionCallback callback){
        this.mRefCallback = new AndroidSmartReference<>(callback);
        return this;
    }

    /**
     * start service by this Intent.
     * @return true if start service success.
     * @see Context#startActivity(Intent)
     */
    public boolean startService() {
        return act(new IntentActionActor() {
            @Override
            public boolean act(Context context, Intent intent) {
                return context.startService(intent) != null;
            }
        });
    }
    /**
     * stop service by this Intent.
     * @return true if stop service success.
     * @see Context#stopService(Intent)
     */
    public boolean stopService() {
        return act(new IntentActionActor() {
            @Override
            public boolean act(Context context, Intent intent) {
                return context.stopService(intent);
            }
        });
    }

    /**
     * bind service by this intent with target parameter.
     * @param conn the connection
     * @param flags Operation options for the binding.  May be 0,
     *          {@link Context#BIND_AUTO_CREATE}, {@link Context#BIND_DEBUG_UNBIND},
     *          {@link Context#BIND_NOT_FOREGROUND}, {@link Context#BIND_ABOVE_CLIENT},
     *          {@link Context#BIND_ALLOW_OOM_MANAGEMENT}, or
     *          {@link Context#BIND_WAIVE_PRIORITY}.
     * @return true if bind service success.
     * @see Context#bindService(Intent, ServiceConnection, int)
     */
    public boolean bindService(@NonNull final ServiceConnection conn, final int flags) {
        return act(new IntentActionActor() {
            @Override
            public boolean act(Context context, Intent intent) {
                return context.bindService(intent, conn, flags);
            }
        });
    }

    /**
     * unbind the service.
     * @param conn the connection
     * @see Context#unbindService(ServiceConnection)
     */
    public void unbindService(@NonNull final ServiceConnection conn) {
        act(new IntentActionActor() {
            @Override
            public boolean act(Context context, Intent intent) {
                context.unbindService(conn);
                return true;
            }
        });
    }

    /**
     * start activity by this intent.
     * @see Context#startActivity(Intent)
     */
    public void startActivity() {
        startActivity(null);
    }
    /**
     * start activity for result by this intent and target request code.
     * @param requestCode the request code
     * @see Activity#startActivityForResult(Intent, int)
     */
    public void startActivityForResult(final int requestCode) {
        act(new IntentActionActor() {
            @Override
            public boolean verify(@NonNull Context context) {
                return context instanceof Activity;
            }
            @Override
            public boolean act(Context context, Intent intent) {
                ((Activity) context).startActivityForResult(intent, requestCode);
                return true;
            }
        });
    }

    /**
     * start activity for result by this intent and target request code.
     * @param requestCode the request code
     * @param options Additional options for how the Activity should be started.
     * See {@link android.content.Context#startActivity(Intent, Bundle)
     * Context.startActivity(Intent, Bundle)} for more details.
     * @see Activity#startActivityForResult(Intent, int, Bundle)
     * @since 1.1.1
     */
    public void startActivityForResult(final int requestCode, final @Nullable Bundle options) {
        act(new IntentActionActor() {
            @Override
            public boolean verify(@NonNull Context context) {
                return context instanceof Activity && Build.VERSION.SDK_INT >= 16;
            }
            @TargetApi(16)
            @Override
            public boolean act(Context context, Intent intent) {
                ((Activity) context).startActivityForResult(intent, requestCode, options);
                return true;
            }
        });
    }
    /**
     * start activity with options.
     * @param options Additional options for how the Activity should be started.
     * May be null if there are no options.  See {@link android.app.ActivityOptions}
     * for how to build the Bundle supplied here; there are no supported definitions
     * for building it manually.
     * @see Context#startActivity(Intent)
     * @see Context#startActivity(Intent, Bundle)
     */
    public void startActivity(@Nullable final Bundle options) {
        act(new IntentActionActor() {
            @Override
            public boolean verify(@NonNull Context context) {
                return options == null || Build.VERSION.SDK_INT >= 16;
            }

            @Override
            public boolean act(Context context, Intent intent) {
                if (!(context instanceof Activity)) {
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                }
                if (options == null) {
                    context.startActivity(intent);
                } else {
                    if (Build.VERSION.SDK_INT >= 16) {
                        context.startActivity(intent, options);
                    } else {
                        throw new UnsupportedOperationException("startActivity() by options.");
                    }
                }
                return true;
            }
        });
    }

    /**
     * send broadcast by this intent.
     * @see Context#sendBroadcast(Intent)
     */
    public void sendBroadcast() {
        sendBroadcast(null);
    }
    /**
     * send broadcast by this intent.
     * @param receiverPermission (optional) String naming a permissions that
     *               a receiver must hold in order to receive your broadcast.
     *               If null, no permission is required.
     * @see Context#sendBroadcast(Intent, String)
     */
    public void sendBroadcast(@Nullable final String receiverPermission) {
        act(new IntentActionActor() {
            @Override
            public boolean act(Context context, Intent intent) {
                context.sendBroadcast(intent, receiverPermission);
                return true;
            }
        });
    }

    /**
     * send ordered broadcast by this intent.
     * @see Context#sendOrderedBroadcast(Intent, String)
     */
    public void sendOrderedBroadcast() {
        sendOrderedBroadcast(null);
    }
    /**
     * send ordered broadcast by this intent.
     * @param receiverPermission (optional) String naming a permissions that
     *               a receiver must hold in order to receive your broadcast.
     *               If null, no permission is required.
     * @see Context#sendOrderedBroadcast(Intent, String)
     */
    public void sendOrderedBroadcast(@Nullable final String receiverPermission) {
        act(new IntentActionActor() {
            @Override
            public boolean act(Context context, Intent intent) {
                context.sendOrderedBroadcast(intent, receiverPermission);
                return true;
            }
        });
    }

    /**
     * do act this intent with callback by target actor.
     * @param actor the intent actor
     * @since 1.1.0
     * @return true if act success. if context is recycled or actor verify failed.
     *          this method will return false.
     */
    public boolean act(IntentActionActor actor){
        final Context context = getContext();
        if (context != null) {
            if(!actor.verify(context)){
                Logger.w("LauncherIntent","act","act failed. caused by actor.verify() failed.");
                return false;
            }
            IntentActionCallback callback = getCallback();
            final boolean result;
            if(callback != null){
                callback.beforeAction(context, this);
                result = actor.act(context, this);
                callback.afterAction(context, this);
            }else {
                result = actor.act(context, this);
            }
            return result;
        }
        return false;
    }

    private Context getContext() {
        return mWeakContext.getContext();
    }
    private IntentActionCallback getCallback() {
        return mRefCallback != null ? mRefCallback.get() : null;
    }

    /**
     * the intent action actor
     * @version 1.1.0
     */
    public static abstract class IntentActionActor {
        /**
         * verify environment .
         * @param context the context
         * @return true if verify success.
         */
        public boolean verify(@NonNull Context context){
            return true;
        }

        /**
         * do act the intent .
         * @param context the context
         * @param intent the intent.
         * @return true if act success.
         */
        public abstract boolean act(Context context, Intent intent);
    }

    /**
     * the callback of launch intent.
     * @version  1.1.0
     */
    public interface IntentActionCallback{
        /**
         * called before action.
         * @param context the context
         * @param intent the intent
         */
        void beforeAction(Context context, Intent intent);
        /**
         * called after action.
         * @param context the context
         * @param intent the intent
         */
        void afterAction(Context context, Intent intent);
    }
}