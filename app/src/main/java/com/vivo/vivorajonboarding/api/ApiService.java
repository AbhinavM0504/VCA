package com.vivo.vivorajonboarding.api;
import com.vivo.vivorajonboarding.model.ApiResponseAlbum;
import com.vivo.vivorajonboarding.model.ApiResponse;
import com.vivo.vivorajonboarding.model.ApiResponseImages;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiService {
    @GET("get_feed.php")
    Call<ApiResponse> getFeed();
    @GET("get_albums.php")
    Call<ApiResponseAlbum> getAlbums();


    @GET("get_album_images.php")
    Call<ApiResponseImages> getAlbumImages(@Query("album_id") int albumId);
}
