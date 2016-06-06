package intech.juced.intechtest.application;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.MediaController;

import intech.juced.intechtest.R;
import intech.juced.intechtest.helpers.RealmHelper;
import intech.juced.intechtest.player.MusicController;
import intech.juced.intechtest.service.music.MusicService;
import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by juced on 03.06.2016.
 */
public class IntechApplication extends Application /*implements MediaController.MediaPlayerControl*/ {

    /*@Override
    public void start() {
        musicSrv.go();
    }

    @Override
    public void pause() {
        musicSrv.pausePlayer();
    }

    @Override
    public int getDuration() {
        if (musicSrv != null && IntechApplication.getSingleton().isMusicBound() && musicSrv.isPng()) {
            return musicSrv.getDur();
        }
        else {
            return 0;
        }
    }

    @Override
    public int getCurrentPosition() {
        if (musicSrv != null && IntechApplication.getSingleton().isMusicBound() && musicSrv.isPng()) {
            return musicSrv.getPosn();
        }
        else {
            return 0;
        }
    }

    @Override
    public void seekTo(int pos) {
        musicSrv.seek(pos);
    }

    @Override
    public boolean isPlaying() {
        if (musicSrv != null && IntechApplication.getSingleton().isMusicBound()){
            return musicSrv.isPng();
        }
        else {
            return false;
        }
    }

    @Override
    public int getBufferPercentage() {
        return 0;
    }

    @Override
    public boolean canPause() {
        return true;
    }

    @Override
    public boolean canSeekBackward() {
        return true;
    }

    @Override
    public boolean canSeekForward() {
        return true;
    }

    @Override
    public int getAudioSessionId() {
        return 0;
    }*/

    private static IntechApplication singleton;
    private MusicService musicSrv;
    private Intent playIntent;
    private boolean musicBound = false;

    public IntechApplication getInstance() {
        return singleton;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        singleton = this;
        RealmHelper.initializeRealm();
        startMusicService();
    }

    public static IntechApplication getSingleton() {
        return singleton;
    }

    //connect to the service
    private ServiceConnection musicConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.MusicBinder binder = (MusicService.MusicBinder) service;
            musicSrv = binder.getService();
            musicBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicBound = false;
        }
    };

    public void startMusicService() {
        if (playIntent == null){
            playIntent = new Intent(this, MusicService.class);
            bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
            startService(playIntent);
        }
    }

    public void songPicked(int songId){
        musicSrv.setSongId(songId);
        musicSrv.playSong();
    }

    public void stopMusicService() {
        unbindService(musicConnection);
        stopService(playIntent);
        musicSrv = null;
    }

    public MusicService getMusicSrv() {
        return musicSrv;
    }

    public boolean isMusicBound() {
        return musicBound;
    }
}
