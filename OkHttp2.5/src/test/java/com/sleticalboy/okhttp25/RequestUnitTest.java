package com.sleticalboy.okhttp25;

import com.sleticalboy.okhttp25.http.HttpCallback;
import com.sleticalboy.okhttp25.http.HttpClient;
import com.sleticalboy.okhttp25.http.builder.GetBuilder;
import com.sleticalboy.okhttp25.http.builder.RequestBuilder;

import org.junit.Test;

/**
 * 测试 http 请求的类
 */
public final class RequestUnitTest {

    @Test
    public void testGet() {

        final RequestBuilder getBuilder = new GetBuilder()
                .url("http://www.baidu.com/")
                .header("custom-header", "minxing");
        HttpClient.getInstance().asyncExecute(getBuilder, new HttpCallback<String>() {
            @Override
            public void onSuccess(String response) {
                System.out.println(response);
            }

            @Override
            public void onFailure(Throwable e) {
                System.out.println(e.getMessage());
            }
        });
    }
}
