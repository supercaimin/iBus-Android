package cn.homecaught.ibus_saas.activity;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.annotation.Nullable;

import cn.homecaught.ibus_saas.R;

public class PlayerMusicService extends Service {

    private final static String TAG = PlayerMusicService.class.getSimpleName();

    private MediaPlayer mMediaPlayer;



    @Nullable

    @Override

    public IBinder onBind(Intent intent) {

        return null;

    }



    @Override

    public void onCreate() {

        super.onCreate();


        mMediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.nb);

        mMediaPlayer.setLooping(true);

        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        mMediaPlayer.setVolume(0, 0);

    }



    @Override

    public int onStartCommand(Intent intent, int flags, int startId) {

        new Thread(new Runnable() {

            @Override

                    public void run() {

                startPlayMusic();

            }

        }).start();

        return START_STICKY;

    }



    private void startPlayMusic() {

        if(mMediaPlayer != null) {


            mMediaPlayer.start();

        }

    }



    private void stopPlayMusic() {

        if(mMediaPlayer != null) {


            mMediaPlayer.stop();

        }

    }



    @Override

    public void onDestroy() {

        super.onDestroy();

        stopPlayMusic();


        // 重启自己

        Intent intent = new Intent(getApplicationContext(), PlayerMusicService.class);

        startService(intent);

    }

}

