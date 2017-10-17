package com.heaven7.android.util2;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import dalvik.system.DexClassLoader;
import dalvik.system.VMStack;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class DexLoader {
    private DexClassLoader mClassLoader;

    public DexLoader(String var1, Context var2, String[] var3, String var4) {
        Object var5 = VMStack.getCallingClassLoader();
        if(var5 == null) {
            var5 = var2.getClassLoader();
        }

        for(int var6 = 0; var6 < var3.length; ++var6) {
            var5 = this.mClassLoader = new DexClassLoader(var3[var6], var4, var1, (ClassLoader)var5);
        }

    }

    public DexLoader(Context var1, String[] var2, String var3) {
        this((String)null, (Context)var1, (String[])var2, (String)var3);
    }

    public DexLoader(Context var1, String[] var2, String var3, String var4) {
        Object var5 = var1.getClassLoader();
        String var6 = var1.getApplicationInfo().nativeLibraryDir;
        if(!TextUtils.isEmpty(var4)) {
            var6 = var6 + File.pathSeparator + var4;
        }

        for(int var7 = 0; var7 < var2.length; ++var7) {
            var5 = this.mClassLoader = new DexClassLoader(var2[var7], var3, var6, (ClassLoader)var5);
        }

    }

    public DexLoader(Context var1, String[] var2, String var3, DexLoader var4) {
        DexClassLoader var5 = var4.getClassLoader();

        for(int var6 = 0; var6 < var2.length; ++var6) {
            var5 = this.mClassLoader = new DexClassLoader(var2[var6], var3, var1.getApplicationInfo().nativeLibraryDir, var5);
        }

    }

    public DexLoader(Context var1, String var2, String var3) {
        this(var1, new String[]{var2}, var3);
    }

    public DexClassLoader getClassLoader() {
        return this.mClassLoader;
    }

    public Object newInstance(String var1) {
        try {
            return this.mClassLoader.loadClass(var1).newInstance();
        } catch (Throwable var3) {
            Log.e(this.getClass().getSimpleName(), "create " + var1 + " instance failed", var3);
            return null;
        }
    }

    public Object newInstance(String var1, Class<?>[] var2, Object... var3) {
        try {
            return this.mClassLoader.loadClass(var1).getConstructor(var2).newInstance(var3);
        } catch (Throwable var5) {
            if("com.tencent.smtt.webkit.adapter.X5WebViewAdapter".equalsIgnoreCase(var1)) {
                Log.e(this.getClass().getSimpleName(), "\'newInstance " + var1 + " failed", var5);
                return var5;
            } else {
                Log.e(this.getClass().getSimpleName(), "create \'" + var1 + "\' instance failed", var5);
                return null;
            }
        }
    }

    public Class<?> loadClass(String var1) {
        try {
            return this.mClassLoader.loadClass(var1);
        } catch (Throwable var3) {
            Log.e(this.getClass().getSimpleName(), "loadClass \'" + var1 + "\' failed", var3);
            return null;
        }
    }

    public Object invokeStaticMethod(String var1, String var2, Class<?>[] var3, Object... var4) {
        try {
            Method var5 = this.mClassLoader.loadClass(var1).getMethod(var2, var3);
            var5.setAccessible(true);
            return var5.invoke((Object)null, var4);
        } catch (Throwable var6) {
            if(var2 != null && var2.equalsIgnoreCase("initTesRuntimeEnvironment")) {
                Log.e(this.getClass().getSimpleName(), "\'" + var1 + "\' invoke static method \'" + var2 + "\' failed", var6);
                return var6;
            } else {
                Log.e(this.getClass().getSimpleName(), "\'" + var1 + "\' invoke static method \'" + var2 + "\' failed", var6);
                return null;
            }
        }
    }

    public Object invokeMethod(Object var1, String var2, String var3, Class<?>[] var4, Object... var5) {
        try {
            Method var6 = this.mClassLoader.loadClass(var2).getMethod(var3, var4);
            var6.setAccessible(true);
            return var6.invoke(var1, var5);
        } catch (Throwable var7) {
            Log.e(this.getClass().getSimpleName(), "\'" + var2 + "\' invoke method \'" + var3 + "\' failed", var7);
            return null;
        }
    }

    public Object getStaticField(String var1, String var2) {
        try {
            Field var3 = this.mClassLoader.loadClass(var1).getField(var2);
            var3.setAccessible(true);
            return var3.get((Object)null);
        } catch (Throwable var4) {
            Log.e(this.getClass().getSimpleName(), "\'" + var1 + "\' get field \'" + var2 + "\' failed", var4);
            return null;
        }
    }

    public void setStaticField(String var1, String var2, Object var3) {
        try {
            Field var4 = this.mClassLoader.loadClass(var1).getField(var2);
            var4.setAccessible(true);
            var4.set((Object)null, var3);
        } catch (Throwable var5) {
            Log.e(this.getClass().getSimpleName(), "\'" + var1 + "\' set field \'" + var2 + "\' failed", var5);
        }
    }
}
