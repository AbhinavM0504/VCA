package com.vivo.vivorajonboarding.api;
import com.vivo.vivorajonboarding.model.ApiResponse1;
import com.vivo.vivorajonboarding.model.ApiResponse2;
import com.vivo.vivorajonboarding.model.ApiResponse3;
import com.vivo.vivorajonboarding.model.ApiResponseAlbum;
import com.vivo.vivorajonboarding.model.ApiResponse;
import com.vivo.vivorajonboarding.model.ApiResponseImages;
import com.vivo.vivorajonboarding.model.DocumentUploadResponse;
import com.vivo.vivorajonboarding.model.EducationRequest;
import com.vivo.vivorajonboarding.model.ExperienceSubmitRequest;
import com.vivo.vivorajonboarding.model.NomineeRequest;
import com.vivo.vivorajonboarding.model.NomineeSubmissionRequest;
import com.vivo.vivorajonboarding.model.UploadResponse;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface ApiService {
    @GET("get_feed.php")
    Call<ApiResponse> getFeed();
    @GET("get_albums.php")
    Call<ApiResponseAlbum> getAlbums();


    @GET("get_album_images.php")
    Call<ApiResponseImages> getAlbumImages(@Query("album_id") int albumId);

    @POST("save_education.php")
    Call<ApiResponse1> saveEducation(@Body EducationRequest request);

    @Multipart
    @POST("upload_documents.php")  // Match the exact PHP filename
    Call<DocumentUploadResponse> uploadDocument(@Part MultipartBody.Part document);

    @POST("submit_nominees.php")
    Call<ApiResponse2> submitNominees(@Body NomineeSubmissionRequest request);

    @POST("submit_insurance_nominees.php")
    Call<ApiResponse1> submitNominees(@Body NomineeRequest request);

    @Multipart
    @POST("submit_experience.php")
    Call<ApiResponse3> submitExperience(
            @Part("data") RequestBody data,
            @Part List<MultipartBody.Part> files
    );

    @Multipart
    @POST("upload_personel_documents.php")
    Call<UploadResponse> uploadDocuments(
            @Part("user_id") RequestBody userId,
            @Part List<MultipartBody.Part> documents
    );
}
