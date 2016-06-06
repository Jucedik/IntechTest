package intech.juced.intechtest.models;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by juced on 03.06.2016.
 */
public class SongItem extends RealmObject {

    @PrimaryKey
    private int id;

    private String title;
    private int artistId;
    private String artist;
    private String picUrl;
    private String demoUrl;

    public SongItem() {
    }

    public SongItem(int id, String title, int artistId, String artist, String picUrl, String demoUrl) {
        this.id = id;
        this.title = title;
        this.artistId = artistId;
        this.artist = artist;
        this.picUrl = picUrl;
        this.demoUrl = demoUrl;
    }

    // getters and setters -------------------------------------------------------------------------
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getArtistId() {
        return artistId;
    }

    public void setArtistId(int artistId) {
        this.artistId = artistId;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public String getDemoUrl() {
        return demoUrl;
    }

    public void setDemoUrl(String demoUrl) {
        this.demoUrl = demoUrl;
    }
    // ---------------------------------------------------------------------------------------------
}
