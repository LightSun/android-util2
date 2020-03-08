package com.heaven7.android.util2;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.heaven7.core.util.Logger;
import com.heaven7.java.base.anno.Nullable;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * a class named launcher intent help we fast handle . start/launch activity/service/broadcast.
 *
 * @author heaven7
 * @since 1.0.2
 */
public class LauncherIntent extends Intent {

    private WeakContextOwner mWeakContext;
    private AndroidSmartReference<IntentActionCallback> mRefCallback;

    /**
     * create the launcher intent by context and target class .
     *
     * @param context     the context
     * @param targetClass the target class.
     */
    protected LauncherIntent(Context context, Class<?> targetClass) {
        super(context, targetClass);
        setContext(context);
    }

    /**
     * @since 1.1.1
     */
    protected LauncherIntent() {
        super();
    }

    /**
     * create the launcher intent by context and target class .
     *
     * @param context     the context
     * @param targetClass the target class.
     * @return the launcher intent.
     */
    public static LauncherIntent create(Context context, Class<?> targetClass) {
        return new LauncherIntent(context, targetClass);
    }

    /**
     * create the launcher intent by context.
     *
     * @param context the context
     * @return the launcher intent.
     * @since 1.1.1
     */
    public static LauncherIntent create(Context context) {
        return new LauncherIntent().setContext(context);
    }

    /**
     * set context. if you only need context(without explicit target class). just use this.
     *
     * @param context the context.
     * @return this.
     * @since 1.1.1
     */
    public LauncherIntent setContext(Context context) {
        this.mWeakContext = new WeakContextOwner(context);
        return this;
    }

    /**
     * set the intent action callback.
     *
     * @param callback the callback
     * @return this.
     * @since 1.1.0
     */
    public LauncherIntent setIntentActionCallback(IntentActionCallback callback) {
        this.mRefCallback = new AndroidSmartReference<>(callback);
        return this;
    }

    /**
     * start service by this Intent.
     *
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
     *
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
     *
     * @param conn  the connection
     * @param flags Operation options for the binding.  May be 0,
     *              {@link Context#BIND_AUTO_CREATE}, {@link Context#BIND_DEBUG_UNBIND},
     *              {@link Context#BIND_NOT_FOREGROUND}, {@link Context#BIND_ABOVE_CLIENT},
     *              {@link Context#BIND_ALLOW_OOM_MANAGEMENT}, or
     *              {@link Context#BIND_WAIVE_PRIORITY}.
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
     *
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
     *
     * @see Context#startActivity(Intent)
     */
    public void startActivity() {
        startActivity(null);
    }

    /**
     * start activity for result by this intent and target request code.
     *
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
     *
     * @param requestCode the request code
     * @param options     Additional options for how the Activity should be started.
     *                    See {@link android.content.Context#startActivity(Intent, Bundle)
     *                    Context.startActivity(Intent, Bundle)} for more details.
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
     *
     * @param options Additional options for how the Activity should be started.
     *                May be null if there are no options.  See {@link android.app.ActivityOptions}
     *                for how to build the Bundle supplied here; there are no supported definitions
     *                for building it manually.
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
     *
     * @see Context#sendBroadcast(Intent)
     */
    public void sendBroadcast() {
        sendBroadcast(null);
    }

    /**
     * send broadcast by this intent.
     *
     * @param receiverPermission (optional) String naming a permissions that
     *                           a receiver must hold in order to receive your broadcast.
     *                           If null, no permission is required.
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
     *
     * @see Context#sendOrderedBroadcast(Intent, String)
     */
    public void sendOrderedBroadcast() {
        sendOrderedBroadcast(null);
    }

    /**
     * send ordered broadcast by this intent.
     *
     * @param receiverPermission (optional) String naming a permissions that
     *                           a receiver must hold in order to receive your broadcast.
     *                           If null, no permission is required.
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
     *
     * @param actor the intent actor
     * @return true if act success. if context is recycled or actor verify failed.
     * this method will return false.
     * @since 1.1.0
     */
    public boolean act(IntentActionActor actor) {
        final Context context = getContext();
        if (context != null) {
            if (!actor.verify(context)) {
                Logger.w("LauncherIntent", "act", "act failed. caused by actor.verify() failed.");
                return false;
            }
            IntentActionCallback callback = getCallback();
            final boolean result;
            if (callback != null) {
                callback.beforeAction(context, this);
                result = actor.act(context, this);
                callback.afterAction(context, this);
            } else {
                result = actor.act(context, this);
            }
            return result;
        }
        return false;
    }

    private Context getContext() {
        return mWeakContext != null ? mWeakContext.getContext() : null;
    }

    private IntentActionCallback getCallback() {
        return mRefCallback != null ? mRefCallback.get() : null;
    }

    /**
     * the launcher intent builder.
     * almost all method from {@linkplain Intent}.
     *
     * @since 1.1.2
     */
    public static class Builder {

        private final LauncherIntent mIntent = new LauncherIntent();

        /**
         * set the context. this method often used for unknown target component(like service, receiver and etc.).
         *
         * @param context the context.
         * @return this.
         */
        public Builder setContext(Context context) {
            mIntent.setContext(context);
            return this;
        }

        /**
         * {@linkplain Intent#setClass(Context, Class)}
         *
         * @param packageContext the context
         * @param cls            The class name to set, equivalent to
         *                       <code>setClassName(context, cls.getName())</code>.
         * @return this
         */
        public Builder setClass(Context packageContext, Class<?> cls) {
            mIntent.setContext(packageContext);
            mIntent.setClass(packageContext, cls);
            return this;
        }

        /**
         * {@linkplain Intent#setClassName(Context, String)} (Context, Class)}
         *
         * @param packageContext the context
         * @param className      The name of a class inside of the application package
         *                       that will be used as the component for this Intent.
         * @return this
         */
        public Builder setClassName(Context packageContext, String className) {
            mIntent.setContext(packageContext);
            mIntent.setClassName(packageContext, className);
            return this;
        }

        /**
         * Convenience for calling {@link #setComponent} with an
         * explicit application package name and class name.
         *
         * @param packageName The name of the package implementing the desired
         *                    component.
         * @param className   The name of a class inside of the application package
         *                    that will be used as the component for this Intent.
         * @return this.
         * @see #setComponent
         * @see #setClass
         */
        public Builder setClassName(String packageName, String className) {
            mIntent.setClassName(packageName, className);
            return this;
        }

        /**
         * Add a new category to the intent.  Categories provide additional detail
         * about the action the intent performs.  When resolving an intent, only
         * activities that provide <em>all</em> of the requested categories will be
         * used.
         *
         * @param category The desired category.  This can be either one of the
         *                 predefined Intent categories, or a custom category in your own
         *                 namespace.
         * @return Returns the same Intent object, for chaining multiple calls
         * into a single statement.
         * @see #hasCategory
         * @see #removeCategory
         */
        public Builder addCategory(String category) {
            mIntent.addCategory(category);
            return this;
        }

        public Builder setFlags(int flags) {
            mIntent.setFlags(flags);
            return this;
        }

        public Builder addFlags(int flags) {
            mIntent.addFlags(flags);
            return this;
        }

        public Builder setPackage(String packageName) {
            mIntent.setPackage(packageName);
            return this;
        }

        public Builder setType(String type) {
            mIntent.setType(type);
            return this;
        }

        @TargetApi(16)
        public Builder setDataAndTypeAndNormalize(Uri data, String type) {
            if (Build.VERSION.SDK_INT < 16) {
                return this;
            }
            mIntent.setDataAndType(data.normalizeScheme(), normalizeMimeType(type));
            return this;
        }

        public Builder setDataAndType(Uri data, String type) {
            mIntent.setDataAndType(data, type);
            return this;
        }

        public Builder setData(Uri data) {
            mIntent.setData(data);
            return this;
        }

        public Builder putCharSequenceArrayListExtra(String name, ArrayList<CharSequence> value) {
            mIntent.putCharSequenceArrayListExtra(name, value);
            return this;
        }

        public Builder putExtra(String name, Serializable value) {
            mIntent.putExtra(name, value);
            return this;
        }

        public Builder putExtra(String name, boolean[] value) {
            mIntent.putExtra(name, value);
            return this;
        }

        public Builder putExtra(String name, byte[] value) {
            mIntent.putExtra(name, value);
            return this;
        }

        public Builder putExtra(String name, short[] value) {
            mIntent.putExtra(name, value);
            return this;
        }

        public Builder putExtra(String name, char[] value) {
            mIntent.putExtra(name, value);
            return this;
        }

        public Builder putExtra(String name, int[] value) {
            mIntent.putExtra(name, value);
            return this;
        }

        public Builder putExtra(String name, long[] value) {
            mIntent.putExtra(name, value);
            return this;
        }

        public Builder putExtra(String name, float[] value) {
            mIntent.putExtra(name, value);
            return this;
        }

        public Builder putExtra(String name, double[] value) {
            mIntent.putExtra(name, value);
            return this;
        }

        public Builder putExtra(String name, String[] value) {
            mIntent.putExtra(name, value);
            return this;
        }

        public Builder putExtra(String name, CharSequence[] value) {
            mIntent.putExtra(name, value);
            return this;
        }

        public Builder putExtra(String name, Bundle value) {
            mIntent.putExtra(name, value);
            return this;
        }

        public Builder putExtras(Intent src) {
            mIntent.putExtras(src);
            return this;
        }

        public Builder putExtras(Bundle extras) {
            mIntent.putExtras(extras);
            return this;
        }

        public LauncherIntent build() {
            return mIntent;
        }

        //========================= start new methods===============

        /**
         * Add extended data to the intent.  The name must include a package
         * prefix, for example the app com.android.contacts would use names
         * like "com.android.contacts.ShowAll".
         *
         * @param name  The name of the extra data, with package prefix.
         * @param value The String data value.
         * @return Returns the same Intent object, for chaining multiple calls
         * into a single statement.
         * @see #putExtras
         * @see #removeExtra
         * @see #getStringExtra(String)
         * @since 1.1.3
         */
        public Builder putExtra(String name, String value) {
            mIntent.putExtra(name, value);
            return this;
        }

        /**
         * Add extended data to the intent.  The name must include a package
         * prefix, for example the app com.android.contacts would use names
         * like "com.android.contacts.ShowAll".
         *
         * @param name  The name of the extra data, with package prefix.
         * @param value The integer data value.
         * @return Returns the same Intent object, for chaining multiple calls
         * into a single statement.
         * @see #putExtras
         * @see #removeExtra
         * @see #getIntExtra(String, int)
         * @since 1.1.3
         */
        public Builder putExtra(String name, int value) {
            mIntent.putExtra(name, value);
            return this;
        }

        /**
         * Add extended data to the intent.  The name must include a package
         * prefix, for example the app com.android.contacts would use names
         * like "com.android.contacts.ShowAll".
         *
         * @param name  The name of the extra data, with package prefix.
         * @param value The long data value.
         * @return Returns the same Intent object, for chaining multiple calls
         * into a single statement.
         * @see #putExtras
         * @see #removeExtra
         * @see #getLongExtra(String, long)
         * @since 1.1.3
         */
        public Builder putExtra(String name, long value) {
            mIntent.putExtra(name, value);
            return this;
        }

        /**
         * Add extended data to the intent.  The name must include a package
         * prefix, for example the app com.android.contacts would use names
         * like "com.android.contacts.ShowAll".
         *
         * @param name  The name of the extra data, with package prefix.
         * @param value The float data value.
         * @return Returns the same Intent object, for chaining multiple calls
         * into a single statement.
         * @see #putExtras
         * @see #removeExtra
         * @see #getFloatExtra(String, float)
         * @since 1.1.3
         */
        public Builder putExtra(String name, float value) {
            mIntent.putExtra(name, value);
            return this;
        }

        /**
         * Add extended data to the intent.  The name must include a package
         * prefix, for example the app com.android.contacts would use names
         * like "com.android.contacts.ShowAll".
         *
         * @param name  The name of the extra data, with package prefix.
         * @param value The double data value.
         * @return Returns the same Intent object, for chaining multiple calls
         * into a single statement.
         * @see #putExtras
         * @see #removeExtra
         * @see #getDoubleExtra(String, double)
         * @since 1.1.3
         */
        public Builder putExtra(String name, double value) {
            mIntent.putExtra(name, value);
            return this;
        }

        /**
         * Add extended data to the intent.  The name must include a package
         * prefix, for example the app com.android.contacts would use names
         * like "com.android.contacts.ShowAll".
         *
         * @param name  The name of the extra data, with package prefix.
         * @param value The char data value.
         * @return Returns the same Intent object, for chaining multiple calls
         * into a single statement.
         * @see #putExtras
         * @see #removeExtra
         * @see #getCharExtra(String, char)
         * @since 1.1.3
         */
        public Builder putExtra(String name, char value) {
            mIntent.putExtra(name, value);
            return this;
        }

        /**
         * Add extended data to the intent.  The name must include a package
         * prefix, for example the app com.android.contacts would use names
         * like "com.android.contacts.ShowAll".
         *
         * @param name  The name of the extra data, with package prefix.
         * @param value The short data value.
         * @return Returns the same Intent object, for chaining multiple calls
         * into a single statement.
         * @see #putExtras
         * @see #removeExtra
         * @see #getShortExtra(String, short)
         * @since 1.1.3
         */
        public Builder putExtra(String name, short value) {
            mIntent.putExtra(name, value);
            return this;
        }

        /**
         * Add extended data to the intent.  The name must include a package
         * prefix, for example the app com.android.contacts would use names
         * like "com.android.contacts.ShowAll".
         *
         * @param name  The name of the extra data, with package prefix.
         * @param value The byte data value.
         * @return Returns the same Intent object, for chaining multiple calls
         * into a single statement.
         * @see #putExtras
         * @see #removeExtra
         * @see #getByteExtra(String, byte)
         * @since 1.1.3
         */
        public Builder putExtra(String name, byte value) {
            mIntent.putExtra(name, value);
            return this;
        }

        /**
         * Add extended data to the intent.  The name must include a package
         * prefix, for example the app com.android.contacts would use names
         * like "com.android.contacts.ShowAll".
         *
         * @param name  The name of the extra data, with package prefix.
         * @param value The boolean data value.
         * @return Returns the same Intent object, for chaining multiple calls
         * into a single statement.
         * @see #putExtras
         * @see #removeExtra
         * @see #getBooleanExtra(String, boolean)
         * @since 1.1.3
         */
        public Builder putExtra(String name, boolean value) {
            mIntent.putExtra(name, value);
            return this;
        }

        /**
         * Add extended data to the intent.  The name must include a package
         * prefix, for example the app com.android.contacts would use names
         * like "com.android.contacts.ShowAll".
         *
         * @param name  The name of the extra data, with package prefix.
         * @param value The Parcelable data value.
         * @return Returns the same Intent object, for chaining multiple calls
         * into a single statement.
         * @see #putExtras
         * @see #removeExtra
         * @see #getParcelableExtra(String)
         * @since 1.1.3
         */
        public Builder putExtra(String name, Parcelable value) {
            mIntent.putExtra(name, value);
            return this;
        }

        /**
         * Add extended data to the intent.  The name must include a package
         * prefix, for example the app com.android.contacts would use names
         * like "com.android.contacts.ShowAll".
         *
         * @param name  The name of the extra data, with package prefix.
         * @param value The Parcelable[] data value.
         * @return Returns the same Intent object, for chaining multiple calls
         * into a single statement.
         * @see #putExtras
         * @see #removeExtra
         * @see #getParcelableArrayExtra(String)
         * @since 1.1.3
         */
        public Builder putExtra(String name, Parcelable[] value) {
            mIntent.putExtra(name, value);
            return this;
        }

        /**
         * Add extended data to the intent.  The name must include a package
         * prefix, for example the app com.android.contacts would use names
         * like "com.android.contacts.ShowAll".
         *
         * @param name  The name of the extra data, with package prefix.
         * @param value The ArrayList<Parcelable> data value.
         * @return Returns the same Intent object, for chaining multiple calls
         * into a single statement.
         * @see #putExtras
         * @see #removeExtra
         * @see #getParcelableArrayListExtra(String)
         * @since 1.1.3
         */
        public Builder putParcelableArrayListExtra(String name, ArrayList<? extends Parcelable> value) {
            mIntent.putParcelableArrayListExtra(name, value);
            return this;
        }

        /**
         * Add extended data to the intent.  The name must include a package
         * prefix, for example the app com.android.contacts would use names
         * like "com.android.contacts.ShowAll".
         *
         * @param name  The name of the extra data, with package prefix.
         * @param value The ArrayList<Integer> data value.
         * @return Returns the same Intent object, for chaining multiple calls
         * into a single statement.
         * @see #putExtras
         * @see #removeExtra
         * @see #getIntegerArrayListExtra(String)
         * @since 1.1.3
         */
        public Builder putIntegerArrayListExtra(String name, ArrayList<Integer> value) {
            mIntent.putIntegerArrayListExtra(name, value);
            return this;
        }

        /**
         * Add extended data to the intent.  The name must include a package
         * prefix, for example the app com.android.contacts would use names
         * like "com.android.contacts.ShowAll".
         *
         * @param name  The name of the extra data, with package prefix.
         * @param value The ArrayList<String> data value.
         * @return Returns the same Intent object, for chaining multiple calls
         * into a single statement.
         * @see #putExtras
         * @see #removeExtra
         * @see #getStringArrayListExtra(String)
         * @since 1.1.3
         */
        public Builder putStringArrayListExtra(String name, ArrayList<String> value) {
            mIntent.putStringArrayListExtra(name, value);
            return this;
        }

        /**
         * set intent callback
         * @param callback the intent callback
         * @return this
         * @since 1.2.0-x
         */
        public Builder setCallback(IntentActionCallback callback) {
            mIntent.setIntentActionCallback(callback);
            return this;
        }
    }


    //====================================================

    /**
     * the intent action actor
     *
     * @version 1.1.0
     */
    public static abstract class IntentActionActor {
        /**
         * verify environment .
         *
         * @param context the context
         * @return true if verify success.
         */
        public boolean verify(@NonNull Context context) {
            return true;
        }

        /**
         * do act the intent .
         *
         * @param context the context
         * @param intent  the intent.
         * @return true if act success.
         */
        public abstract boolean act(Context context, Intent intent);
    }

    /**
     * the callback of launch intent.
     *
     * @version 1.1.0
     */
    public interface IntentActionCallback {
        /**
         * called before action.
         *
         * @param context the context
         * @param intent  the intent
         */
        void beforeAction(Context context, Intent intent);

        /**
         * called after action.
         *
         * @param context the context
         * @param intent  the intent
         */
        void afterAction(Context context, Intent intent);
    }
}