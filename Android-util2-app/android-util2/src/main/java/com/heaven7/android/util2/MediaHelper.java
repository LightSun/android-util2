package com.heaven7.android.util2;

import android.media.MediaPlayer;
import android.text.TextUtils;

import com.classroom100.android.cache.QuestionCacheHelper;
import com.heaven7.core.util.Logger;
import com.heaven7.java.base.util.Throwables;

import java.io.File;
import java.io.IOException;

/**
 * Created by heaven7 on 2017/7/5 0005.
 */

public class MediaHelper {

    public static final byte STATE_PLAYING    = 1;
    public static final byte STATE_PAUSED     = 2;
    public static final byte STATE_NOT_START  = 3;
    public static final byte STATE_BUFFERING  = 4;
    public static final byte STATE_RELEASE    = 5;

    private static final String TAG = "MediaHelper";
    private final MediaCallback mCallback;
    private MediaPlayer mPlayer;
    private byte mState = STATE_NOT_START;

    public MediaHelper(MediaCallback callback){
        Throwables.checkNull(callback);
        this.mCallback = callback;
        mPlayer = new MediaPlayer();
        mPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                Logger.e(TAG, "showNormalUi", "what = " + what + " , extra = " + extra);
                return false;
            }
        });
    }

    /**
     * switch to pause or resume play or start play.
     * @param url the target url.
     */
    public void switchPlay(String url){
        if(mPlayer == null){
            Logger.w("MediaHelper","switchPlay","player == null");
            return;
        }
        switch (mState){

            case STATE_BUFFERING:
            case STATE_PLAYING:
                mPlayer.pause();
                setStateInternal(STATE_PAUSED);
                break;

            case STATE_PAUSED:
                setStateInternal(STATE_PLAYING);
                mPlayer.start();
                break;

            case STATE_NOT_START:
                startPlayDownloadFile(url);
                break;

            default:
                Logger.w(TAG,"switchPlay","unexpected media state = " + mState);
        }
    }

    public void stopPlay() {
        if(mPlayer != null && mPlayer.isPlaying()) {
            Logger.d(TAG, "stopPlay", "");
            mPlayer.stop();
            setStateInternal(STATE_NOT_START);
        }
    }

    public void pausePlay() {
        if(mPlayer != null && mPlayer.isPlaying()) {
            mPlayer.pause();
            setStateInternal(STATE_PAUSED);
        }
    }
    public void resumePlay() {
        if(mPlayer != null && mState == STATE_PAUSED) {
            setStateInternal(STATE_PLAYING);
            mPlayer.start();
        }
    }
    public void onDestroy() {
        if(mPlayer != null) {
            if (mPlayer.isPlaying()) {
                mPlayer.stop();
            }
            mPlayer.reset();
            mPlayer.release();
            mPlayer = null;
            setStateInternal(STATE_RELEASE);
        }
    }

    public void startPlayDownloadFile(String url) {
        startPlayDownloadFile(url, 0);
    }

    public void startPlayDownloadFile(String url , int position) {
        File file = QuestionCacheHelper.getInstance().getAudio(url);
        if(file == null){
            Logger.w(TAG,"startPlayDownloadFile","file = null");
            return;
        }
        Logger.i(TAG,"startPlayDownloadFile","file = " + file);
        startPlayFile(file.getAbsolutePath(), position);
    }

    private boolean startPlayFile(final String filename, final int position){
        Logger.d(TAG,"startPlayFile","filename = " + filename);
        if(!TextUtils.isEmpty(filename) && mPlayer != null){
            try {
                mPlayer.reset();
                mPlayer.setDataSource(filename);
                mPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        MediaHelper.this.onPrepared(mp, position);
                        onPrepareFileComplete(filename);
                    }
                });
                mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        onPlayFileComplete(filename);
                    }
                });
                setStateInternal(STATE_BUFFERING);
                mPlayer.prepareAsync();
                return true;
            } catch (IOException e) {
                resetPlayer();
                e.printStackTrace();
            }
        }
        return false;
    }

    public int getPlayPosition(){
        return mPlayer != null ? mPlayer.getCurrentPosition() : 0;
    }

    private void setStateInternal(byte newState){
        if(mState != newState){
            mState = newState;
            if(mCallback != null){
                mCallback.onMediaStateChanged(mPlayer, newState);
            }
        }
    }

    private void onPrepared(MediaPlayer mp, int position) {
        if(position > 0 && position < mp.getDuration()) {
            mp.seekTo(position);
        }
        setStateInternal(STATE_PLAYING);
        mp.start();
    }

    private void onPlayFileComplete(String filename) {
        setStateInternal(STATE_NOT_START); //done to unstart
        if(mCallback != null){
            mCallback.onPlayFileComplete(filename);
        }
    }

    private void onPrepareFileComplete(String filename) {
        if(mCallback != null){
            mCallback.onPrepareFileComplete(filename);
        }
    }

    private void resetPlayer(){
        if(mPlayer != null) {
            mPlayer.reset();
        }
    }

    public interface MediaCallback{

        void onPlayFileComplete(String filename);

        void onPrepareFileComplete(String filename);

        void onMediaStateChanged(MediaPlayer mp, byte state);
    }
}
