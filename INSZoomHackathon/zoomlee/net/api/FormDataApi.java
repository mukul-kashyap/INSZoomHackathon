package com.zoomlee.Zoomlee.net.api;

import com.zoomlee.Zoomlee.net.CommonResponse;
import com.zoomlee.Zoomlee.net.model.Form;

import java.util.List;

import retrofit.http.DELETE;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Query;

/**
 * Created by  Evgen Marinin <ievgen.marinin@alterplay.com> on 26.05.15.
 */
public interface FormDataApi {

    @GET("/forms/")
    CommonResponse<List<Form>> getForms(@Header("zoomle_key") String privateKey, @Query("time") int time);

    @POST("/forms/")
    CommonResponse<Form> postForm(@Header("zoomle_key") String privateKey, @Query("id") int id,
                                  @Query("data") String jsonFields);
}
