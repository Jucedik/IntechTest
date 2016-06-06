package intech.juced.intechtest.service.music;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.NotificationTarget;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import intech.juced.intechtest.R;
import intech.juced.intechtest.activities.SongDetailsActivity;
import intech.juced.intechtest.activities.SongsActivity;
import intech.juced.intechtest.application.IntechApplication;
import intech.juced.intechtest.application.IntechConstants;
import intech.juced.intechtest.helpers.DateHelper;
import intech.juced.intechtest.models.SongItem;
import intech.juced.intechtest.views.player_controls.PlayerControlsView;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by juced on 04.06.2016.
 */
public class MusicService extends Service  implements
        MediaPlayer.OnPreparedListener,
        MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener {

    @Override
    public void onDestroy() {
        //super.onDestroy();
        stopForeground(true);
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        //if (player.getCurrentPosition() > 0){
        mp.reset();
        playNext();
        //}
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();

        // update notification
        createPlayerControlsNotification();

        // notify receivers
        sendDataToReceivers(Constants.BROADCAST_COMMAND_PLAY_SONG, new HashMap<String, String>() {{
            put(Constants.BROADCAST_KEY_SONG_NAME, songTitle);
            put(Constants.BROADCAST_KEY_ARTIST_NAME, artistName);
            put(Constants.BROADCAST_KEY_SONG_ID, String.valueOf(songId));
        }});
    }

    private void createPlayerControlsNotification() {
        // set big view
        RemoteViews notificationBigView = new RemoteViews(getPackageName(), R.layout.notification_big_player);
        notificationBigView.setTextViewText(R.id.text_songName, songTitle);
        notificationBigView.setTextViewText(R.id.text_artistName, artistName);

        // set small view
        RemoteViews notificationSmallView = new RemoteViews(getPackageName(), R.layout.notification_small_player);
        notificationSmallView.setTextViewText(R.id.text_songName, songTitle);
        notificationSmallView.setTextViewText(R.id.text_artistName, artistName);

        // set play\pause button icon
        if (!isPng()) {
            // set play icon
            notificationSmallView.setImageViewResource(R.id.btn_playPause, R.drawable.ic_play_arrow_black_24dp);
            notificationBigView.setImageViewResource(R.id.btn_playPause, R.drawable.ic_play_arrow_black_24dp);
        }
        else {
            // set pause icon
            notificationSmallView.setImageViewResource(R.id.btn_playPause, R.drawable.ic_pause_black_24dp);
            notificationBigView.setImageViewResource(R.id.btn_playPause, R.drawable.ic_pause_black_24dp);
        }

        // create pending intent for notification
        Intent notIntent = new Intent(this, SongDetailsActivity.class);
        notIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        notIntent.putExtra(IntechConstants.KEY_SONG_ID, songId);
        PendingIntent pendInt = PendingIntent.getActivity(this, 0, notIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        // build notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder
                .setContentIntent(pendInt)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.ic_play_arrow_white_24dp)
                .setOngoing(true)
                .setContentIntent(pendInt);
        Notification notification = builder.build();
        notification.flags |= Notification.FLAG_NO_CLEAR;
        notification.bigContentView = notificationBigView;
        notification.contentView = notificationSmallView;

        // set close player action
        Intent closeIntent = new Intent(this, PlayerNotificationListener.class);
        closeIntent.putExtra(PlayerNotificationListener.KEY_ACTION, PlayerNotificationListener.VALUE_ACTION_STOP);
        PendingIntent pendingCloseIntent = PendingIntent.getBroadcast(this, 1, closeIntent, 0);
        notificationBigView.setOnClickPendingIntent(R.id.btn_close, pendingCloseIntent);
        notificationSmallView.setOnClickPendingIntent(R.id.btn_close, pendingCloseIntent);

        // set play/pause music player action
        Intent playPauseIntent = new Intent(this, PlayerNotificationListener.class);
        playPauseIntent.putExtra(PlayerNotificationListener.KEY_ACTION, PlayerNotificationListener.VALUE_ACTION_PLAY_PAUSE);
        PendingIntent pendingPlayPauseIntent = PendingIntent.getBroadcast(this, 2, playPauseIntent, 0);
        notificationBigView.setOnClickPendingIntent(R.id.btn_playPause, pendingPlayPauseIntent);
        notificationSmallView.setOnClickPendingIntent(R.id.btn_playPause, pendingPlayPauseIntent);

        // set previous action
        Intent previousIntent = new Intent(this, PlayerNotificationListener.class);
        previousIntent.putExtra(PlayerNotificationListener.KEY_ACTION, PlayerNotificationListener.VALUE_ACTION_PREVIOUS);
        PendingIntent pendingPreviousIntent = PendingIntent.getBroadcast(this, 3, previousIntent, 0);
        notificationBigView.setOnClickPendingIntent(R.id.btn_previous, pendingPreviousIntent);

        // set next action
        Intent nextIntent = new Intent(this, PlayerNotificationListener.class);
        nextIntent.putExtra(PlayerNotificationListener.KEY_ACTION, PlayerNotificationListener.VALUE_ACTION_NEXT);
        PendingIntent pendingNextIntent = PendingIntent.getBroadcast(this, 4, nextIntent, 0);
        notificationBigView.setOnClickPendingIntent(R.id.btn_next, pendingNextIntent);
        notificationSmallView.setOnClickPendingIntent(R.id.btn_next, pendingNextIntent);

        // fire notification
        startForeground(NOTIFY_ID, notification);

        // load image into big view
        try {
            NotificationTarget notificationTarget = new NotificationTarget(getApplicationContext(), notificationBigView, R.id.img_photo, notification, NOTIFY_ID);
            Glide.with(getApplicationContext()).load(imageUrl).asBitmap().into(notificationTarget);
        }
        catch (Exception ignored) {}

        // load image to into view
        try {
            NotificationTarget notificationTarget = new NotificationTarget(getApplicationContext(), notificationSmallView, R.id.img_photo, notification, NOTIFY_ID);
            Glide.with(getApplicationContext()).load(imageUrl).asBitmap().into(notificationTarget);
        }
        catch (Exception ignored) {}
    }

    public static class PlayerNotificationListener extends BroadcastReceiver {

        public static final String KEY_ACTION = "KEY_ACTION";
        public static final String VALUE_ACTION_STOP = "VALUE_ACTION_STOP";
        public static final String VALUE_ACTION_PLAY_PAUSE = "VALUE_ACTION_PLAY_PAUSE";
        public static final String VALUE_ACTION_PREVIOUS = "VALUE_ACTION_PREVIOUS";
        public static final String VALUE_ACTION_NEXT = "VALUE_ACTION_NEXT";

        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                String actionName = intent.getStringExtra(KEY_ACTION);
                if (actionName.equals(VALUE_ACTION_STOP)) {
                    stopPlayer();
                }
                else if (actionName.equals(VALUE_ACTION_PLAY_PAUSE)) {
                    playPause();
                }
                else if (actionName.equals(VALUE_ACTION_PREVIOUS)) {
                    playPrevious();
                }
                else if (actionName.equals(VALUE_ACTION_NEXT)) {
                    playNext();
                }
            }
            catch (Exception ignored) {}
        }

        private void stopPlayer() {
            MusicService musicService = IntechApplication.getSingleton().getMusicSrv();
            if (musicService != null) {
                musicService.stopMusic(true);
            }
        }

        private void playPause() {
            MusicService musicService = IntechApplication.getSingleton().getMusicSrv();
            if (musicService.isPng()) {
                // pause
                musicService.pausePlayer();
            }
            else {
                // play
                musicService.go();
            }
        }

        private void playPrevious() {
            MusicService musicService = IntechApplication.getSingleton().getMusicSrv();
            musicService.playPrev();
        }

        private void playNext() {
            MusicService musicService = IntechApplication.getSingleton().getMusicSrv();
            musicService.playNext();
        }
    }

    private MediaPlayer player;
    private RealmResults<SongItem> songs;
    private int songId;
    private int currentSongPosition;
    private final IBinder musicBind = new MusicBinder();
    private Realm realm;
    private String songTitle = "";
    private String artistName = "";
    private String imageUrl = "";
    private static final int NOTIFY_ID = 1;
    private boolean shuffle = false;
    private Random rand;
    public static final String CLASS_NAME = "intech.juced.intechtest.service.music";

    @Override
    public IBinder onBind(Intent arg0) {
        return musicBind;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        player.stop();
        player.release();
        return false;
    }

    public void onCreate(){
        super.onCreate();
        songId = 0;
        initMusicPlayer();
        realm = Realm.getDefaultInstance();
        rand = new Random();
    }

    public void initMusicPlayer() {
        player = new MediaPlayer();
        player.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        player.setOnPreparedListener(this);
        player.setOnCompletionListener(this);
        player.setOnErrorListener(this);
    }

    public class MusicBinder extends Binder {
        public MusicService getService() {
            return MusicService.this;
        }
    }

    public void playSong(){
        // try to reset player
        try {
            player.reset();
        }
        catch (Exception ignored) {
            // reinitialize player
            initMusicPlayer();
        }

        if (songs == null) {
            songs = realm.where(SongItem.class).findAll();
        }

        //get song
        SongItem playSong = realm.where(SongItem.class).equalTo("id", songId).findFirst(); //songs.get(songId);
        currentSongPosition = songs.indexOf(playSong);
        songTitle = playSong.getTitle();
        artistName = playSong.getArtist();
        imageUrl = playSong.getPicUrl();

        // immediately create notification
        createPlayerControlsNotification();

        // notify receivers
        sendDataToReceivers(Constants.BROADCAST_COMMAND_PLAY_SONG, new HashMap<String, String>() {{
            put(Constants.BROADCAST_KEY_SONG_NAME, songTitle);
            put(Constants.BROADCAST_KEY_ARTIST_NAME, artistName);
            put(Constants.BROADCAST_KEY_SONG_ID, String.valueOf(songId));
        }});

        try{
            player.setDataSource(playSong.getDemoUrl());
        }
        catch(Exception e){
            Log.e("MUSIC SERVICE", "Error setting data source", e);
        }

        player.prepareAsync();
    }

    public void setSongId(int songId) {
        this.songId = songId;
    }

    public int getPosn() {
        return player.getCurrentPosition();
    }

    public int getDur() {
        return player.getDuration();
    }

    public String getDurString() {
        return DateHelper.getSongDurationString(player.getDuration());
    }

    public boolean isPng() {
        return player.isPlaying();
    }

    public void pausePlayer() {
        sendDataToReceivers(Constants.BROADCAST_COMMAND_PAUSE, null);

        player.pause();

        // update notification
        createPlayerControlsNotification();
    }

    public void seek(int posn) {
        player.seekTo(posn);
    }

    public void go() {
        sendDataToReceivers(Constants.BROADCAST_COMMAND_PLAY, null);

        player.start();

        // update notification
        createPlayerControlsNotification();
    }

    public void playPrev() {
        currentSongPosition --;
        if (currentSongPosition < 0) {
            currentSongPosition = songs.size() - 1;
        }
        songId = songs.get(currentSongPosition).getId();
        playSong();
    }

    public void playNext() {
        currentSongPosition ++;
        if (currentSongPosition >= songs.size()) {
            currentSongPosition = 0;
        }
        songId = songs.get(currentSongPosition).getId();
        playSong();
    }

    public void stopMusic(boolean removeNotification) {
        sendDataToReceivers(Constants.BROADCAST_COMMAND_STOP, null);

        player.stop();
        player.release();

        if (removeNotification) {
            stopForeground(true);
        }
    }

    private void sendDataToReceivers(String command, HashMap<String, String> params) {
        Intent intent = new Intent(CLASS_NAME);
        intent.putExtra(Constants.BROADCAST_KEY_COMMAND, command);

        if (params != null) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                intent.putExtra(key, value);
            }
        }

        sendBroadcast(intent);
    }

    public void togglePlayState() {
        if (isPng()) {
            // pause
            pausePlayer();
        }
        else {
            // play
            go();
        }
    }

    public String getSongTitle() {
        return songTitle;
    }

    public String getArtistName() {
        return artistName;
    }

    public int getSongId() {
        return songId;
    }
}
