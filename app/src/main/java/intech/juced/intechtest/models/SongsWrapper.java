package intech.juced.intechtest.models;

import java.util.ArrayList;

import io.realm.RealmResults;

/**
 * Created by juced on 03.06.2016.
 */
public class SongsWrapper {

    public static final int TYPE_LOADER = 0;
    public static final int TYPE_ITEM = 1;
    public static final int TYPE_SPACE = 2;

    private int type;
    private SongItem songItem;

    public SongsWrapper() {
    }

    public SongsWrapper(int type) {
        this.type = type;
    }

    public SongsWrapper(int type, SongItem songItem) {
        this.type = type;
        this.songItem = songItem;
    }

    public static ArrayList<SongsWrapper> createInitialList() {
        return new ArrayList<SongsWrapper>() {{add(new SongsWrapper(TYPE_LOADER)); add(new SongsWrapper(TYPE_SPACE));}};
    }

    public static ArrayList<SongsWrapper> prepareItems(RealmResults<SongItem> elements, boolean appendLoader) {
        ArrayList<SongsWrapper> res = new ArrayList<>();

        for (int i = 0; i < elements.size(); i++) {
            res.add(new SongsWrapper(TYPE_ITEM, elements.get(i)));
        }

        if (appendLoader) {
            res.add(new SongsWrapper(TYPE_LOADER));
        }

        res.add(new SongsWrapper(TYPE_SPACE));

        return res;
    }

    // getters and setters -------------------------------------------------------------------------
    public SongItem getSongItem() {
        return songItem;
    }

    public void setSongItem(SongItem songItem) {
        this.songItem = songItem;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
    // ---------------------------------------------------------------------------------------------
}
