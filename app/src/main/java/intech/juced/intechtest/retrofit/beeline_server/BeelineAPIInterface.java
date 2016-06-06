package intech.juced.intechtest.retrofit.beeline_server;

import java.util.List;

import intech.juced.intechtest.models.ServerResponseWrapper;
import intech.juced.intechtest.models.SongItem;
import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created by juced on 03.06.2016.
 */
public interface BeelineAPIInterface {

    @GET("/public/marketplaces/1/tags/4/melodies")
    Call<ServerResponseWrapper> loadSongs(
            @Query("limit") int limit,
            @Query("from") int offset
    );

}
