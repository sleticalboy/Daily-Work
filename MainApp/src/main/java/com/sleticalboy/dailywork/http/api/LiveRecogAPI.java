package com.sleticalboy.dailywork.http.api;

import com.sleticalboy.dailywork.http.Constants;

import java.util.Map;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;

/**
 * Created on 18-3-26.
 *
 * @author sleticalboy
 * @description 活体检测接口
 */
public interface LiveRecogAPI {

    @FormUrlEncoded // application/x-www-form-urlencoded
    @POST(Constants.API_QUERY)
    Call<String> query(@FieldMap Map<String, Object> params);

    @Multipart // application/multi-part
    @POST(Constants.API_REGISTER)
    Call<String> register(@PartMap Map<String, Object> params, @Part MultipartBody.Part image);

    @Multipart
    @POST(Constants.API_JUDGE)
    Call<String> judge(@PartMap Map<String, Object> params, @Part MultipartBody.Part image);
}
