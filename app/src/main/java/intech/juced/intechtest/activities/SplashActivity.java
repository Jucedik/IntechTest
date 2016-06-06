package intech.juced.intechtest.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;

import intech.juced.intechtest.R;
import intech.juced.intechtest.application.IntechConstants;
import intech.juced.intechtest.fragments.SplashFragment;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        setContentFragment();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    private void setContentFragment() {
        FragmentManager fm = getSupportFragmentManager();
        SplashFragment fragment = (SplashFragment) fm.findFragmentByTag(IntechConstants.FRAGMENT_TAG_SPLASH_ACTIVITY);
        if (fragment == null) {
            fragment = new SplashFragment();
            fm.beginTransaction().add(R.id.block_content, fragment, IntechConstants.FRAGMENT_TAG_SPLASH_ACTIVITY)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .commit();
        }
        else {
            fm.beginTransaction().show(fragment).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit();
        }
    }

}
