package intech.juced.intechtest.fragments;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import app.minimize.com.seek_bar_compat.SeekBarCompat;
import intech.juced.intechtest.R;
import intech.juced.intechtest.activities.SongDetailsActivity;
import intech.juced.intechtest.application.IntechApplication;
import intech.juced.intechtest.helpers.AnimationHelper;
import intech.juced.intechtest.helpers.DateHelper;
import intech.juced.intechtest.models.SongItem;
import intech.juced.intechtest.service.music.Constants;
import intech.juced.intechtest.service.music.MusicService;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmModel;
import io.realm.RealmQuery;

public class SongDetailsFragment extends Fragment {

    public static final int SEEK_BAR_UPDATE_STEP = 50;

    private int songId;
    private Realm realm;
    private boolean dataLoaded = false;
    private SongItem songItem;

    private View view;
    private RelativeLayout block_loader;
    private Toolbar toolbar;
    private FloatingActionButton fab;
    private ImageView img_photo;
    private TextView text_title;
    private TextView text_artist;
    private LinearLayout block_seekControls;
    private TextView text_currentPosition;
    private SeekBarCompat seekBar_progress;
    private TextView text_songLength;

    private Handler mHandler = new Handler();

    public SongDetailsFragment() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        realm = Realm.getDefaultInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_song_details, container, false);
            getViewElems();
            setActions();
        }

        if (!dataLoaded) {
            loadData();
        }

        return view;
    }

    private void getViewElems() {
        block_loader = (RelativeLayout) view.findViewById(R.id.block_loader);
        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        toolbar.setTitle("");
        ((SongDetailsActivity) getActivity()).setSupportActionBar(toolbar);

        fab = (FloatingActionButton) view.findViewById(R.id.fab);
        img_photo = (ImageView) view.findViewById(R.id.img_photo);
        text_title = (TextView) view.findViewById(R.id.text_title);
        text_artist = (TextView) view.findViewById(R.id.text_artist);

        // seek control views
        block_seekControls = (LinearLayout) view.findViewById(R.id.block_seekControls);
        text_currentPosition = (TextView) view.findViewById(R.id.text_currentPosition);
        seekBar_progress = (SeekBarCompat) view.findViewById(R.id.seekBar_progress);
        text_songLength = (TextView) view.findViewById(R.id.text_songLength);
        block_seekControls.setVisibility(View.GONE);
    }

    private void setActions() {
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MusicService musicService = IntechApplication.getSingleton().getMusicSrv();
                if (musicService != null) {
                    try {
                        if (musicService.getSongId() == songId) {
                            //musicService.pausePlayer();

                            if (musicService.isPng()) {
                                musicService.pausePlayer();
                            }
                            else {
                                musicService.go();
                            }
                        }
                        else {
                            IntechApplication.getSingleton().songPicked(songId);
                        }
                    }
                    catch (Exception ignored) {
                        IntechApplication.getSingleton().songPicked(songId);
                    }
                }
                else {
                    IntechApplication.getSingleton().songPicked(songId);
                }
            }
        });

        seekBar_progress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {}

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                try {
                    MusicService musicService = IntechApplication.getSingleton().getMusicSrv();
                    musicService.seek(seekBar.getProgress() * SEEK_BAR_UPDATE_STEP);
                }
                catch (Exception ignored) {}
            }
        });
    }

    public void updatePlayerControls() {
        try {
            MusicService musicService = IntechApplication.getSingleton().getMusicSrv();
            if (musicService != null) {
                if (musicService.getSongId() == songId) {
                    if (musicService.isPng()) {
                        AnimationHelper.animateIn(block_seekControls);
                        fab.setImageResource(R.drawable.ic_pause_white_24dp);

                        // fill song length
                        text_songLength.setText(musicService.getDurString());

                        // fill current song position
                        fillCurrentSongPosition();

                        seekBar_progress.setMax(musicService.getDur() / SEEK_BAR_UPDATE_STEP);
                    }
                    else {
                        AnimationHelper.animateOut(block_seekControls);
                        fab.setImageResource(R.drawable.ic_play_arrow_white_24dp);
                    }
                }
                else {
                    block_seekControls.setVisibility(View.GONE);
                    fab.setImageResource(R.drawable.ic_play_arrow_white_24dp);
                }
            }
        }
        catch (Exception ignored) {
            String sss = "123";
        }
    }

    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                MusicService musicService = IntechApplication.getSingleton().getMusicSrv();
                if (musicService.getSongId() == songId) {
                    if (musicService.isPng()) {
                        int mCurrentPosition = musicService.getPosn() / SEEK_BAR_UPDATE_STEP;
                        seekBar_progress.setProgress(mCurrentPosition);

                        // set current position text
                        text_currentPosition.setText(DateHelper.getSongDurationString(musicService.getPosn()));
                    }
                }
            }
            catch (Exception ignored) {}

            mHandler.postDelayed(this, SEEK_BAR_UPDATE_STEP);
        }
    };

    private void fillCurrentSongPosition() {
        getActivity().runOnUiThread(mRunnable);
    }

    private void loadData() {
        dataLoaded = true;
        block_loader.setVisibility(View.VISIBLE);
        fab.setVisibility(View.GONE);

        RealmQuery<SongItem> query = realm.where(SongItem.class);
        query.equalTo("id", songId);
        songItem = query.findFirstAsync();
        songItem.addChangeListener(new RealmChangeListener<RealmModel>() {
            @Override
            public void onChange(RealmModel element) {
                fillData();
            }
        });
    }

    private void fillData() {
        AnimationHelper.animateOut(block_loader);
        fab.show();

        // set photo
        Glide.with(this).load(songItem.getPicUrl()).into(img_photo);

        // set title and artist name
        text_title.setText(songItem.getTitle());
        text_artist.setText(songItem.getArtist());
    }

    public void setSongId(int songId) {
        this.songId = songId;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (realm != null) {
            realm.close();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(receiver);
        mHandler.removeCallbacks(mRunnable);
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(receiver, new IntentFilter(MusicService.CLASS_NAME));
        updatePlayerControls();
        getActivity().runOnUiThread(mRunnable);
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                updatePlayerControls();
            }
            catch (Exception ignored) {}
        }
    };

}
