package intech.juced.intechtest.service.load_playlist;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;

import intech.juced.intechtest.application.IntechApplication;

/**
 * Created by juced on 04.06.2016.
 */
public class LoadSongsWorker {

    public static final int LIMIT = 20;

    public static boolean isIntentServiceRunning() {
        ActivityManager manager = (ActivityManager) IntechApplication.getSingleton().getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (LoadSongsService.CLASS_FULL_NAME.equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public static void fireWork(int offset, int limit) {
        Context context = IntechApplication.getSingleton().getApplicationContext();

        Intent i = new Intent(context, LoadSongsService.class);
        i.putExtra(LoadSongsService.KEY_COMMAND, LoadSongsService.COMMAND_FIRE_WORK);
        i.putExtra(LoadSongsService.KEY_VAR_LIMIT, limit);
        i.putExtra(LoadSongsService.KEY_VAR_OFFSET, offset);
        context.startService(i);
    }

}
