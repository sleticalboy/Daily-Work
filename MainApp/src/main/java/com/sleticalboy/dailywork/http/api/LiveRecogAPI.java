package com.sleticalboy.dailywork.http.api;

import com.sleticalboy.dailywork.http.Constants;

import java.util.Map;

import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;

/**
 * Created on 18-3-26.
 *
 * @author sleticalboy
 * @description
 */
public interface LiveRecogAPI {

    @FormUrlEncoded
    @POST(Constants.API_QUERY)
    String query(@Field("BolgTp") String bolgTp, @Field("CstNo") String CstNo);

    @FormUrlEncoded
    @POST(Constants.API_QUERY)
    String query(@FieldMap Map<String, String> params);

    @Multipart
    @POST(Constants.API_REGISTER)
    String register(@FieldMap Map<String, String> params);

    @Multipart
    @POST(Constants.API_JUDGE)
    String judge(@FieldMap Map<String, String> params);
}
