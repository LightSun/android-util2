package com.heaven7.android.util2;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;

import androidx.annotation.RawRes;

import com.heaven7.core.util.Logger;
import com.heaven7.java.base.anno.Nullable;

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

    /**
     * load the sound resource with target load listener.
     * @param rawId the raw resource id
     * @param l the load listener
     * @return the sound processor for target resource
     */
    public SoundProcessor load(@RawRes int rawId,@Nullable SoundPool.OnLoadCompleteListener l) {
        SoundProcessor sp = new SoundProcessor(mContext, mPool);
        sp.load(rawId, l);
        mHandlers.add(sp);
        return sp;
    }

    /**
     * create sound processor .
     * @return an instance of {@linkplain SoundProcessor}
     */
    public SoundProcessor newSoundProcessor() {
        SoundProcessor sp = new SoundProcessor(mContext, mPool);
        mHandlers.add(sp);
        return sp;
    }

    /**
     * set volume for all sound which are all loaded.
     * @param leftVolume the left volume
     * @param rightVolume the right volume
     */
    public void setVolume(float leftVolume, float rightVolume) {
        for (SoundProcessor sp : mHandlers) {
            sp.setVolume(leftVolume, rightVolume);
        }
    }

    /**
     * the the sound to loop or not.
     * @param loop true to loop
     */
    public void setLoop(boolean loop) {
        for (SoundProcessor sp : mHandlers) {
            sp.setLoop(loop);
        }
    }

    /**
     * Change playback rate.
     *
     * The playback rate allows the application to vary the playback
     * rate (pitch) of the sound. A value of 1.0 means playback at
     * the original frequency. A value of 2.0 means playback twice
     * as fast, and a value of 0.5 means playback at half speed.
     * If the stream does not exist, it will have no effect.
     *
     * @param rate playback rate (1.0 = normal playback, range 0.5 to 2.0)
     * @see SoundPool#setRate(int, float)
     */
    public void setRate(float rate) {
        for (SoundProcessor sp : mHandlers) {
            sp.setRate(rate);
        }
    }

    /**
     * Change stream priority.
     *
     * Change the priority of the stream specified by the streamID.
     * This is the value returned by the play() function. Affects the
     * order in which streams are re-used to play new sounds. If the
     * stream does not exist, it will have no effect.
     *
     * @param priority the priority
     */
    public void setPriority(int priority) {
        for (SoundProcessor sp : mHandlers) {
            sp.setPriority(priority);
        }
    }

    /**
     * release the all sound.
     */
    public void release() {
        mHandlers.clear();
        mPool.setOnLoadCompleteListener(null);
        mPool.release();
    }

    /**
     * pause all active sound.
     * @see #resumeAll()
     */
    public void pauseAll() {
        mPool.autoPause();
    }

    /**
     * resume all sound which are all paused.
     * @see #pauseAll()
     */
    public void resumeAll() {
        mPool.autoResume();
    }

    /**
     * the sound processor help we handle sound.
     * @since 1.1.4
     */
    public static class SoundProcessor {
        private final SoundPool mPool;
        private final Context mContext;
        private int mSoundId;

        private SoundProcessor(Context context, SoundPool mPool) {
            this.mPool = mPool;
            this.mContext = context;
        }

        /**
         * load sound and auto play it when load done.
         * @param rawId the resource id
         */
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

        /**
         * load the sound resource with target load listener.
         * @param rawId the raw resource id
         * @param l the load listener
         * @return the sound processor for target resource
         */
        public void load(@RawRes int rawId, SoundPool.OnLoadCompleteListener l) {
            mPool.setOnLoadCompleteListener(l);
            mSoundId = mPool.load(mContext, rawId, 1);
        }

        /**
         * set the sound to play loop or not.
         * @param loop true to loop
         */
        public void setLoop(boolean loop) {
            mPool.setLoop(mSoundId, loop ? -1 : 0);
        }

        /**
         * Change playback rate.
         *
         * The playback rate allows the application to vary the playback
         * rate (pitch) of the sound. A value of 1.0 means playback at
         * the original frequency. A value of 2.0 means playback twice
         * as fast, and a value of 0.5 means playback at half speed.
         * If the stream does not exist, it will have no effect.
         *
         * @param rate playback rate (1.0 = normal playback, range 0.5 to 2.0)
         */
        public void setRate(float rate) {
            mPool.setRate(mSoundId, rate);
        }

        /**
         * Change stream priority.
         *
         * Change the priority of the stream specified by the streamID.
         * This is the value returned by the play() function. Affects the
         * order in which streams are re-used to play new sounds. If the
         * stream does not exist, it will have no effect.
         *
         * @param priority the priority
         */
        public void setPriority(int priority) {
            mPool.setPriority(mSoundId, priority);
        }

        /**
         * Set stream volume.
         *
         * Sets the volume on the stream specified by the streamID.
         * This is the value returned by the play() function. The
         * value must be in the range of 0.0 to 1.0. If the stream does
         * not exist, it will have no effect.
         *
         * @param leftVolume left volume value (range = 0.0 to 1.0)
         * @param rightVolume right volume value (range = 0.0 to 1.0)
         */
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

        /**
         * Unload a sound from a sound ID.
         *
         * Unloads the sound specified by the soundID. This is the value
         * returned by the load() function. Returns true if the sound is
         * successfully unloaded, false if the sound was already unloaded.
         *
         * @return true if just unloaded, false if previously unloaded
         */
        public boolean unload() {
            if (mPool.unload(mSoundId)) {
                mSoundId = 0;
                return true;
            }
            return false;
        }

        /**
         * Pause a playback stream.
         *
         * Pause the stream specified by the streamID. This is the
         * value returned by the play() function. If the stream is
         * playing, it will be paused. If the stream is not playing
         * (e.g. is stopped or was previously paused), calling this
         * function will have no effect.
         */
        public void pause() {
            mPool.pause(mSoundId);
        }

        /**
         * Resume a playback stream.
         *
         * Resume the stream specified by the streamID. This
         * is the value returned by the play() function. If the stream
         * is paused, this will resume playback. If the stream was not
         * previously paused, calling this function will have no effect.
         */
        public void resume() {
            mPool.resume(mSoundId);
        }
    }

}
