package com.example.donghobaothuc;

import android.media.MediaPlayer;

import androidx.appcompat.app.AppCompatActivity;

public class AlarmApplication extends AppCompatActivity {
    private static MediaPlayer mediaPlayer;

    public static MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    public static void setMediaPlayer(MediaPlayer player) {
        mediaPlayer = player;
    }
}
