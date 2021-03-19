package com.sleticalboy.http;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Url;

/**
 * Created on 21-3-18.
 *
 * @author binlee sleticalboy@gmail.com
 */
public interface IDemo {

    @GET(value = "/index.html")
    Call<String> visit(@Header(value = "Content-Type") String header);

    @GET(value = "/")
    Observable<String> visit();

    @GET(value = "/")
    List<String> list();

    @GET(value = "/")
    byte[] byteArray();

    @GET(value = "/")
    WebPage webPage();
}
