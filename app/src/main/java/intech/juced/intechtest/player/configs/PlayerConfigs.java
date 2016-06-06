package intech.juced.intechtest.player.configs;

import android.content.Context;
import android.content.SharedPreferences;

import intech.juced.intechtest.application.IntechApplication;

/**
 * Created by juced on 03.06.2016.
 */
public class PlayerConfigs {

    public static final String PREFERENCES_TAG = "INTECH_PREFERENCES_TAG";
    public static final String KEY_PREF_SONGS_PRESENTATION_TYPE = "KEY_PREF_SONGS_PRESENTATION_TYPE";

    public enum SongsPresentationType {
        TABLE,
        LIST
    }

    public static SongsPresentationType getSongsPresentationType() {
        SharedPreferences spref = IntechApplication.getSingleton().getSharedPreferences(PREFERENCES_TAG, Context.MODE_PRIVATE);
        return SongsPresentationType.valueOf(spref.getString(KEY_PREF_SONGS_PRESENTATION_TYPE, SongsPresentationType.TABLE.name()));
    }

    public static void saveSongsPresentationType(SongsPresentationType type) {
        SharedPreferences spref = IntechApplication.getSingleton().getSharedPreferences(PREFERENCES_TAG, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = spref.edit();
        editor.putString(KEY_PREF_SONGS_PRESENTATION_TYPE, type.name());
        editor.apply();
    }

}
