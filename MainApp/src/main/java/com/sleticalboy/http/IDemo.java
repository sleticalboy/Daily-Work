package com.sleticalboy.http;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

/**
 * Created on 21-3-18.
 *
 * @author binlee sleticalboy@gmail.com
 */
public interface IDemo {

    @GET(value = "/index.html")
    Call<String> visit(@Header(value = "Content-Type") String header);
}
