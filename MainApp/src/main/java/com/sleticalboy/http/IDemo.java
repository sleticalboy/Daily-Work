package com.sleticalboy.http;

import com.sleticalboy.bean.Apis;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;

/**
 * Created on 21-3-18.
 *
 * @author binlee sleticalboy@gmail.com
 */
public interface IDemo {

    @GET("/index.html")
    Call<String> visit(@Header(value = "Content-Type") String header);

    @GET("/")
    Observable<String> visit();

    @GET("/")
    List<String> list();

    @GET("/")
    byte[] byteArray();

    @GET("/")
    WebPage webPage();

    /**
     * 获取所有的 API
     */
    @GET("/")
    Call<Apis> listApis();

    /**
     * 获取 user 的全部仓库
     * @param user 用户名
     * @return 该用户所有的仓库
     */
    @GET("/users/{user}/repos")
    Call<List<?>> listRepos(@Path("user") String user);
}
