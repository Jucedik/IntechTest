package intech.juced.intechtest.service.load_playlist;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import intech.juced.intechtest.helpers.RealmHelper;
import intech.juced.intechtest.models.ServerResponseWrapper;
import intech.juced.intechtest.retrofit.beeline_server.BeelineAPIInterface;
import intech.juced.intechtest.retrofit.beeline_server.Params;
import io.realm.Realm;
import retrofit.Call;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;

public class LoadSongsService extends IntentService {

    public static final String CLASS_FULL_NAME = "intech.juced.intechtest.service.load_playlist.LoadSongsService";

    public static final String KEY_COMMAND = "KEY_COMMAND";
    public static final String COMMAND_FIRE_WORK = "COMMAND_FIRE_WORK";

    public static final String KEY_VAR_LIMIT = "KEY_VAR_LIMIT";
    public static final String KEY_VAR_OFFSET= "KEY_VAR_OFFSET";

    public LoadSongsService() {
        super("LoadSongsService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i("LoadSongsService", "onHandleIntent running...");

        try {
            String command = intent.getExtras().getString(KEY_COMMAND, "");
            if (command.equals(COMMAND_FIRE_WORK)) {
                startLoadSongs(intent.getExtras().getInt(KEY_VAR_OFFSET, 0), intent.getExtras().getInt(KEY_VAR_LIMIT, LoadSongsWorker.LIMIT));
            }
        }
        catch (Exception ignored) {
            String sss = "123";
        }
    }

    private void startLoadSongs(int offset, int limit) throws Exception {
        // correct offset
        if (offset < 0) {
            offset = 0;
        }

        // load songs
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Params.SERVER_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        BeelineAPIInterface beelineAPIInterface = retrofit.create(BeelineAPIInterface.class);
        Call<ServerResponseWrapper> call = beelineAPIInterface.loadSongs(limit, offset);
        ServerResponseWrapper serverResponse = call.execute().body();

        if (serverResponse != null && serverResponse.getMelodies() != null && !serverResponse.getMelodies().isEmpty()) {
            // write loaded songs to realm
            Realm realm = Realm.getDefaultInstance();
            realm.beginTransaction();
            realm.copyToRealmOrUpdate(serverResponse.getMelodies());
            realm.commitTransaction();
            realm.close();
        }
        else {
            // all songs loaded
            RealmHelper.setAllSongsLoaded(true);
        }
    }

}
