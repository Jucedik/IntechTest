package intech.juced.intechtest.activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import intech.juced.intechtest.R;
import intech.juced.intechtest.application.IntechConstants;
import intech.juced.intechtest.fragments.SongDetailsFragment;
import intech.juced.intechtest.fragments.SplashFragment;

public class SongDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_details);
        setContentFragment();
    }

    private void setContentFragment() {
        FragmentManager fm = getSupportFragmentManager();
        SongDetailsFragment fragment = (SongDetailsFragment) fm.findFragmentByTag(IntechConstants.FRAGMENT_TAG_SONG_DETAILS);
        if (fragment == null) {
            fragment = new SongDetailsFragment();
            fragment.setSongId(getIntent().getIntExtra(IntechConstants.KEY_SONG_ID, 0));
            fm.beginTransaction().add(R.id.block_content, fragment, IntechConstants.FRAGMENT_TAG_SONG_DETAILS)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .commit();
        }
        else {
            fm.beginTransaction().show(fragment).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
