package intech.juced.intechtest.helpers;

import android.content.Context;
import android.content.SharedPreferences;

import intech.juced.intechtest.application.IntechApplication;
import intech.juced.intechtest.models.SongItem;
import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by juced on 03.06.2016.
 */
public class RealmHelper {

    public static final String DB_NAME = "intech_audio_player.realm";
    public static final int DB_VERSION = 1;

    public static final String PREFERENCES_TAG = "INTECH_PREFERENCES_TAG_REALM_HELPER";
    public static final String KEY_PREF_ALL_SONGS_LOADED = "KEY_PREF_ALL_SONGS_LOADED";

    public static void initializeRealm() {
        try {
            RealmConfiguration config = new RealmConfiguration.Builder(IntechApplication.getSingleton().getApplicationContext())
                    .name(RealmHelper.DB_NAME)
                    .schemaVersion(RealmHelper.DB_VERSION)
                    .deleteRealmIfMigrationNeeded()
                    .build();

            Realm.setDefaultConfiguration(config);
        }
        catch (Exception ignored) {}
    }

    public static void removeAllSongs() {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.where(SongItem.class).findAll().deleteAllFromRealm();
        realm.commitTransaction();
        realm.close();

        setAllSongsLoaded(false);
    }

    public static boolean isAllSongsLoaded() {
        SharedPreferences spref = IntechApplication.getSingleton().getSharedPreferences(PREFERENCES_TAG, Context.MODE_PRIVATE);
        return spref.getBoolean(KEY_PREF_ALL_SONGS_LOADED, false);
    }

    public static void setAllSongsLoaded(boolean value) {
        SharedPreferences spref = IntechApplication.getSingleton().getSharedPreferences(PREFERENCES_TAG, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = spref.edit();
        editor.putBoolean(KEY_PREF_ALL_SONGS_LOADED, value);
        editor.apply();
    }

}
