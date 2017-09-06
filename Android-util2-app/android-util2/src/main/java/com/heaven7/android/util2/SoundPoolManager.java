package com.heaven7.android.util2;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.support.annotation.RawRes;

import com.heaven7.core.util.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * the sound pool manager
 * Created by heaven7 on 2017/9/6 0006.
 *
 * @since 1.1.4
 */

public final class SoundPoolManager {

    private static final int MAX_STREAM = 5;
    private final List<SoundProcessor> mHandlers;
    private final SoundPool mPool;
    private final Context mContext;

    public SoundPoolManager(Context mContext) {
        this.mHandlers = new ArrayList<>();
        this.mContext = mContext;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            this.mPool = new SoundPool.Builder()
                    .setMaxStreams(MAX_STREAM)
                    .setAudioAttributes(new AudioAttributes.Builder()
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .setLegacyStreamType(AudioManager.STREAM_MUSIC)
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .build())
                    .build();
        } else {
            mPool = new SoundPool(MAX_STREAM, AudioManager.STREAM_MUSIC, 0);
        }
    }

    public SoundProcessor load(@RawRes int rawId, SoundPool.OnLoadCompleteListener l) {
        SoundProcessor sp = new SoundProcessor(mContext, mPool);
        sp.load(rawId, l);
        mHandlers.add(sp);
        return sp;
    }

    public SoundProcessor newSoundProcessor() {
        SoundProcessor sp = new SoundProcessor(mContext, mPool);
        mHandlers.add(sp);
        return sp;
    }

    public void setVolume(float leftVolume, float rightVolume) {
        for (SoundProcessor sp : mHandlers) {
            sp.setVolume(leftVolume, rightVolume);
        }
    }

    public void setLoop(boolean loop) {
        for (SoundProcessor sp : mHandlers) {
            sp.setLoop(loop);
        }
    }

    public void setRate(float rate) {
        for (SoundProcessor sp : mHandlers) {
            sp.setRate(rate);
        }
    }

    public void setPriority(int priority) {
        for (SoundProcessor sp : mHandlers) {
            sp.setPriority(priority);
        }
    }

    public void release() {
        mPool.release();
        mHandlers.clear();
    }

    public void pauseAll() {
        mPool.autoPause();
    }

    public void resumeAll() {
        mPool.autoResume();
    }

    public static class SoundProcessor {
        private final SoundPool mPool;
        private final Context mContext;
        private int mSoundId;

        private SoundProcessor(Context context, SoundPool mPool) {
            this.mPool = mPool;
            this.mContext = context;
        }

        public void loadAndAutoPlay(@RawRes int rawId) {
            mPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
                @Override
                public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                    if (mSoundId != 0) {
                        if (!play()) {
                            Logger.w("SoundProcessor", "onLoadComplete", "auto play failed.");
                        }
                    }
                }
            });
            mSoundId = mPool.load(mContext, rawId, 1);
        }

        public void load(@RawRes int rawId, SoundPool.OnLoadCompleteListener l) {
            mPool.setOnLoadCompleteListener(l);
            mSoundId = mPool.load(mContext, rawId, 1);
        }

        public void setLoop(boolean loop) {
            mPool.setLoop(mSoundId, loop ? -1 : 0);
        }

        public void setRate(float rate) {
            mPool.setRate(mSoundId, rate);
        }

        public void setPriority(int priority) {
            mPool.setPriority(mSoundId, priority);
        }

        public void setVolume(float leftVolume, float rightVolume) {
            mPool.setVolume(mSoundId, leftVolume, rightVolume);
        }

        /**
         * play the sound
         *
         * @return true if play success.
         */
        public boolean play() {
            return mPool.play(mSoundId, 1f, 1f, 0, 0, 1) != 0;
        }

        public void unload() {
            if (mPool.unload(mSoundId)) {
                mSoundId = 0;
            }
        }

        public void pause() {
            mPool.pause(mSoundId);
        }

        public void resume() {
            mPool.resume(mSoundId);
        }
    }

}
