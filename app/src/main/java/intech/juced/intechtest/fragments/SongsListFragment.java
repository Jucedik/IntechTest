package intech.juced.intechtest.fragments;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import intech.juced.intechtest.R;
import intech.juced.intechtest.adapters.SongsAdapter;
import intech.juced.intechtest.application.IntechApplication;
import intech.juced.intechtest.player.configs.PlayerConfigs;
import intech.juced.intechtest.service.music.Constants;
import intech.juced.intechtest.service.music.MusicService;
import intech.juced.intechtest.views.AutofitRecyclerView;
import intech.juced.intechtest.views.player_controls.PlayerControlsView;
import io.realm.Realm;

public class SongsListFragment extends Fragment {

    private View view;
    private AutofitRecyclerView recyclerView;
    private SongsAdapter songsAdapter;
    private Realm realm;
    private PlayerControlsView playerControlsView;

    public SongsListFragment() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        realm = Realm.getDefaultInstance();
    }

    private void initializePlayerControls() {
        try {
            MusicService musicService = IntechApplication.getSingleton().getMusicSrv();
            if (musicService != null) {
                if (musicService.isPng()) {
                    playerControlsView.showAndSetSongParams(musicService.getSongTitle(), musicService.getArtistName());
                }
                else {
                    playerControlsView.close();
                }
            }
            else {
                playerControlsView.close();
            }
        }
        catch (Exception ignored) {
            // Illegal state exception. Maybe player is null.
            playerControlsView.close();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_songs_list, container, false);
            getViews();
            fillData();
        }
        return view;
    }

    private void fillData() {
        songsAdapter = new SongsAdapter(realm, PlayerConfigs.SongsPresentationType.LIST);
        recyclerView.setAdapter(songsAdapter);
    }

    private void getViews() {
        recyclerView = (AutofitRecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setOnLoadAnimated(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
        playerControlsView = (PlayerControlsView) view.findViewById(R.id.playerControlsView);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        realm.removeAllChangeListeners();
        realm.close();
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(receiver);
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(receiver, new IntentFilter(MusicService.CLASS_NAME));
        initializePlayerControls();
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                String command = intent.getStringExtra(Constants.BROADCAST_KEY_COMMAND);
                if (command.equals(Constants.BROADCAST_COMMAND_PLAY_SONG)) {
                    playerControlsView.showAndSetSongParams(intent.getStringExtra(Constants.BROADCAST_KEY_SONG_NAME), intent.getStringExtra(Constants.BROADCAST_KEY_ARTIST_NAME));
                }
                else if (command.equals(Constants.BROADCAST_COMMAND_PLAY)) {
                    playerControlsView.showAndSetPauseIcon();
                }
                else if (command.equals(Constants.BROADCAST_COMMAND_PAUSE)) {
                    playerControlsView.showAndSetPlayIcon();
                }
                else if (command.equals(Constants.BROADCAST_COMMAND_STOP)) {
                    playerControlsView.close();
                }
            }
            catch (Exception ignored) {}
        }
    };

}
