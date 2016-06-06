package intech.juced.intechtest.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import intech.juced.intechtest.R;
import intech.juced.intechtest.activities.SongsActivity;
import intech.juced.intechtest.application.IntechConstants;
import intech.juced.intechtest.helpers.RealmHelper;
import intech.juced.intechtest.models.SongItem;
import io.realm.Realm;

public class SplashFragment extends Fragment {

    private View view;

    public SplashFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_splash, container, false);
            fireWork();
        }

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private void fireWork() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                showMainActivity();
            }
        }, IntechConstants.SPLASH_DELAY);
    }

    private void showMainActivity() {
        RealmHelper.removeAllSongs(); /* temporary function. Remove it after testing */

        Intent intent = new Intent(getContext(), SongsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        getActivity().finish();
    }

}
