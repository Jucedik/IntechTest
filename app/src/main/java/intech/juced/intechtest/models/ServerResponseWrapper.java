package intech.juced.intechtest.models;

import java.util.List;

/**
 * Created by juced on 04.06.2016.
 */
public class ServerResponseWrapper {

    private List<SongItem> melodies;

    public ServerResponseWrapper() {
    }

    public ServerResponseWrapper(List<SongItem> melodies) {
        this.melodies = melodies;
    }

    public List<SongItem> getMelodies() {
        return melodies;
    }

    public void setMelodies(List<SongItem> melodies) {
        this.melodies = melodies;
    }
}
