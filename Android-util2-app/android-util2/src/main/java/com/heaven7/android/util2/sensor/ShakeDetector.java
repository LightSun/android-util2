package com.heaven7.android.util2.sensor;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

/**
 * Detects a shake
 * @since 1.3.0
 */
public class ShakeDetector implements SensorEventListener {

    private static float shakeThresholdGravity = 3.0F;
    private static final int SHAKE_SPACING_TIME_MS = 500;

    private OnShakeListener listener;
    private long shakeTimestamp;

    private SensorManager sensorManager;
    private Sensor accelerometer;


    public interface OnShakeListener {
        void onShake();
    }

    public void setOnShakeListener(OnShakeListener listener) {
        this.listener = listener;
    }

    public void register(Context context){
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
    }
    public void unregister(){
        sensorManager.unregisterListener(this);
        accelerometer = null;
    }

    public void setShakeGestureSensitivity(float sensitivity) {
        shakeThresholdGravity = sensitivity;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (listener != null) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            float gX = x / SensorManager.GRAVITY_EARTH;
            float gY = y / SensorManager.GRAVITY_EARTH;
            float gZ = z / SensorManager.GRAVITY_EARTH;

            // gForce will be close to 1 when there is no movement
            float gForce = (float) Math.sqrt(gX * gX + gY * gY + gZ * gZ);

            if (gForce > shakeThresholdGravity) {
                final long now = System.currentTimeMillis();
                // ignore shake events too close to each other (500ms)
                if (shakeTimestamp + SHAKE_SPACING_TIME_MS > now) {
                    return;
                }

                shakeTimestamp = now;
                listener.onShake();
            }
        }
    }
}