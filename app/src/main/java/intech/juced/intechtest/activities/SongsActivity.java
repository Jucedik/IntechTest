package intech.juced.intechtest.activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import intech.juced.intechtest.R;
import intech.juced.intechtest.application.IntechApplication;
import intech.juced.intechtest.application.IntechConstants;
import intech.juced.intechtest.fragments.SongsListFragment;
import intech.juced.intechtest.fragments.SongsTableFragment;
import intech.juced.intechtest.fragments.SplashFragment;
import intech.juced.intechtest.player.configs.PlayerConfigs;
import intech.juced.intechtest.service.music.MusicService;
import io.realm.Realm;

public class SongsActivity extends AppCompatActivity/* implements SongsActivityInterface*/ {

    // interface implements ------------------------------------------------------------------------
    /*@Override
    public Realm getRealm() {
        return null;
    }*/
    // ---------------------------------------------------------------------------------------------

    private PlayerConfigs.SongsPresentationType songsPresentationType;
    private Menu mainMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_songs_list);
        //realm = Realm.getDefaultInstance();
        getData();
        setupToolBar();
        setContentView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void getData() {
        // get type of view for songs presentation
        this.songsPresentationType = PlayerConfigs.getSongsPresentationType();
    }

    private void setupToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private void setContentView() {
        FragmentManager fm = getSupportFragmentManager();

        SongsTableFragment songsTableFragment = (SongsTableFragment) fm.findFragmentByTag(IntechConstants.FRAGMENT_TAG_SONGS_TABLE);
        SongsListFragment songsListFragment = (SongsListFragment) fm.findFragmentByTag(IntechConstants.FRAGMENT_TAG_SONGS_LIST);

        if (songsPresentationType == PlayerConfigs.SongsPresentationType.TABLE) {
            if (songsListFragment != null && songsListFragment.isVisible()) {
                fm.beginTransaction().hide(songsListFragment).commit();
            }

            if (songsTableFragment == null) {
                songsTableFragment = new SongsTableFragment();
                fm.beginTransaction().add(R.id.block_content, songsTableFragment, IntechConstants.FRAGMENT_TAG_SONGS_TABLE)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .commit();
            }
            else {
                fm.beginTransaction().show(songsTableFragment).commit();
            }
        }
        else {
            if (songsTableFragment != null && songsTableFragment.isVisible()) {
                fm.beginTransaction().hide(songsTableFragment).commit();
            }

            if (songsListFragment == null) {
                songsListFragment = new SongsListFragment();
                fm.beginTransaction().add(R.id.block_content, songsListFragment, IntechConstants.FRAGMENT_TAG_SONGS_LIST)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .commit();
            }
            else {
                fm.beginTransaction().show(songsListFragment).commit();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        mainMenu = menu;
        getMenuInflater().inflate(R.menu.menu_songs, menu);
        setMainMenuIcons();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_toggleSongsPresentationType) {
            songsPresentationType = songsPresentationType == PlayerConfigs.SongsPresentationType.TABLE ? PlayerConfigs.SongsPresentationType.LIST : PlayerConfigs.SongsPresentationType.TABLE;
            setMainMenuIcons();
            setContentView();
            saveSongsPresentationType();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void saveSongsPresentationType() {
        PlayerConfigs.saveSongsPresentationType(songsPresentationType);
    }

    private void setMainMenuIcons() {
        if (mainMenu != null) {
            mainMenu.getItem(0).setIcon(songsPresentationType == PlayerConfigs.SongsPresentationType.TABLE ? R.drawable.ic_view_module_white_24dp : R.drawable.ic_view_list_white_24dp);
        }
    }

}
