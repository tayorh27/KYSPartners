package com.kys.kyspartners;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

/**
 * Created by sanniAdewale on 12/05/2017.
 */

public interface ApiConfig {

    @Multipart
    @POST("upload.php")
    Call<ServerResponse> uploadDisplayImage(@Part MultipartBody.Part file1);
}
