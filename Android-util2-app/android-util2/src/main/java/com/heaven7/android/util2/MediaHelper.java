package com.heaven7.android.util2;

import android.media.MediaPlayer;
import android.text.TextUtils;

import com.heaven7.core.util.Logger;
import com.heaven7.java.base.util.Throwables;

import java.io.IOException;

/**
 * this class help we handle media player.
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
        this.mPlayer = new MediaPlayer();
        mPlayer.setOnErrorListener(mCallback);
    }

    /**
     * get media player.
     * @return the media player.
     */
    public MediaPlayer getMediaPlayer(){
        return mPlayer;
    }

    /**
     * Checks whether the MediaPlayer is playing.
     *
     * @return true if currently playing, false otherwise
     * @throws IllegalStateException if the internal player engine has not been
     * initialized or has been released.
     * @see MediaPlayer#isPlaying()
     * @since 1.1.1
     */
    public boolean isPlaying(){
        return getMediaPlayer().isPlaying();
    }

    /**
     * switch to pause or resume play or start play.
     * @param url the target url.
     */
    public void switchPlay(String url){
        switchPlay(url, 0);
    }
    /**
     * switch to pause or resume play or start play.
     * @param url the target url.
     * @param position the expect position
     * @since 1.0.9
     */
    public void switchPlay(String url, int position){
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
                startPlay(url, position);
                break;

            default:
                Logger.w(TAG,"switchPlay","unexpected media state = " + mState);
        }
    }

    /**
     * stop the media.
     */
    public void stop() {
        if(mPlayer != null && mPlayer.isPlaying()) {
            Logger.d(TAG, "stopPlay", "");
            mPlayer.stop();
            mPlayer.reset();
            setStateInternal(STATE_NOT_START);
        }
    }
    /**
     * pause the media.
     */
    public void pause() {
        if(mPlayer != null && mPlayer.isPlaying()) {
            mPlayer.pause();
            setStateInternal(STATE_PAUSED);
        }
    }
    /**
     * resume play the media.
     */
    public void resumePlay() {
        if(mPlayer != null && mState == STATE_PAUSED) {
            setStateInternal(STATE_PLAYING);
            mPlayer.start();
        }
    }

    /**
     * called on destroy the media.
     */
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

    public void startPlay(String url) {
        startPlay(url, 0);
    }

    /**
     * start play the url for target position.
     * @param url the url.
     * @param position the position.
     */
    public void startPlay(String url , int position) {
        String localFile = getLocalFile(url);
        if(localFile == null){
            Logger.w(TAG,"startPlayDownloadFile","local url/file = null");
            return;
        }
        Logger.d(TAG,"startPlayDownloadFile","local url/file = " + localFile);
        startPlayFile(localFile, position);
    }

    /**
     * get the current position of media player.
     * @return the position. or -1 if is destroyed.
     */
    public int getCurrentPosition(){
        return mPlayer != null ? mPlayer.getCurrentPosition() : -1;
    }

    /**
     * get the local file for target url. often we download the url audio/video then play it.
     * @param url the url
     * @return the local file (absolute path)
     */
    protected String getLocalFile(String url){
        return url;
    }

    /**
     * indicate the resource should prepare async or not.
     * @param link the last link. url or file path
     * @return true if should prepare async.
     * @since 1.0.3
     */
    protected boolean shouldPrepareAsync(String  link){
        return link.startsWith("http://") || link.startsWith("https://");
    }
    //====================================================
    private boolean startPlayFile(final String filename, final int position){
        Logger.d(TAG,"startPlayFile","filename/url = " + filename);
        if(!TextUtils.isEmpty(filename) && mPlayer != null){
            try {
                mPlayer.reset();
                mPlayer.setDataSource(filename);
                mPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        MediaHelper.this.onPrepared(mp, position);
                        if(mCallback != null){
                            mCallback.onPrepareComplete(mp, filename);
                        }
                    }
                });
                mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        onPlayFileComplete(filename);
                    }
                });
                setStateInternal(STATE_BUFFERING);
                if(shouldPrepareAsync(filename)) {
                    mPlayer.prepareAsync();
                }else{
                    mPlayer.prepare();
                }
                return true;
            } catch (IOException e) {
                if(mPlayer != null) {
                    mPlayer.reset();
                }
                e.printStackTrace();
            }
        }
        return false;
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
            mCallback.onPlayComplete(mPlayer, filename);
        }
    }

    /**
     * the media callback of control {@linkplain MediaPlayer}.
     */
    public static abstract class MediaCallback implements MediaPlayer.OnErrorListener{

        /**
         * called on play complete.
         * @param mp the media player
         * @param filename the filename or url.
         */
        public abstract void onPlayComplete(MediaPlayer mp, String filename);

        /**
         * called on media state changed.
         * @param mp the media player.
         * @param state the state. see {@linkplain #STATE_BUFFERING} and etc.
         */
        public abstract void onMediaStateChanged(MediaPlayer mp, byte state);

        /**
         * called on prepare complete.
         *  @param mp the media player
         * @param filename the filename or url.
         */
        public void onPrepareComplete(MediaPlayer mp, String filename){

        }

        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            Logger.e(TAG, "onError", "what = " + what + " , extra = " + extra);
            return false;
        }
    }
}
