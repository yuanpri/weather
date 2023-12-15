package com.gpnu.yuan.warmweather.utils.music;

import android.content.Context;
import android.media.MediaPlayer;

public class MusicPlayer {

    private MediaPlayer mediaPlayer;

    public MusicPlayer(Context context, int resourceId) {
        // 初始化MediaPlayer
        mediaPlayer = MediaPlayer.create(context, resourceId);
    }

    public void playPauseToggle(boolean isPlaying) {
        if (isPlaying) {
            mediaPlayer.start();
        } else {
            mediaPlayer.pause();
        }
    }

    public void release() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

}
