package com.sleticalboy.okhttp25;

import com.squareup.okhttp.Headers;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public final class HeadersUnitTest {
    
    @Test
    public void headersTest() {
        final Map<String, String> headerMap = new HashMap<>();
        headerMap.put("UserAgent", "mingxing/660");
        headerMap.put("CustomHeader", "sleticalboy");
        headerMap.put("library", "OkHttp/2.5");
        Headers headers = Headers.of(headerMap);
        for (int i = 0, size = headers.size(); i < size; i++) {
            System.out.println(headers.name(i) + ": " + headers.value(i));
        }
    }
}
