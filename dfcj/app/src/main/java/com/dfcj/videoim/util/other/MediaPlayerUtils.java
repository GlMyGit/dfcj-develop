package com.dfcj.videoim.util.other;

import android.content.Context;
import android.media.MediaPlayer;

public class MediaPlayerUtils {

  private static   MediaPlayer mediaPlayer;

    public static  MediaPlayer initMedia(Context mcon,int st){

        //播放 assets/a2.mp3 音乐文件
        mediaPlayer = MediaPlayer.create(mcon, st);
        mediaPlayer.setLooping(false);//是否循环播放。

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                LogUtils.logd("调用了播放完成");
                destroyMedia(mediaPlayer);
            }
        });

        return mediaPlayer;
    }


    public static  void startMedia(MediaPlayer mediaPlayer){
        if(mediaPlayer!=null){
            mediaPlayer.start();
        }
    }

    public static  void startMedia(){
        if(mediaPlayer!=null){
            mediaPlayer.start();
        }
    }

    public static  void stopMedia(MediaPlayer mediaPlayer){
        if(mediaPlayer!=null){
            mediaPlayer.stop();
        }
    }

    public static  void stopMedia(){
        if(mediaPlayer!=null){
            mediaPlayer.stop();
        }
    }


    //暂停
    public static  void pauseMedia(MediaPlayer mediaPlayer){
        if(mediaPlayer!=null){
            mediaPlayer.pause();
        }
    }
    public static  void pauseMedia(){
        if(mediaPlayer!=null){
            mediaPlayer.pause();
        }
    }

    //回收
    public static  void destroyMedia(MediaPlayer mediaPlayer){
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    public static  void destroyMedia(){
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }


}
