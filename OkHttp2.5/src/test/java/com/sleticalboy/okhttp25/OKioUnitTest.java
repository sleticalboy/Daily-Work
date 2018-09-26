package com.sleticalboy.okhttp25;

import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;

import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;
import okio.Sink;
import okio.Source;

public final class OKioUnitTest {

    private final String fileName = "src/test/write.txt";

    @Test
    public void readTest() {
        Source source = null;
        BufferedSource bufferedSource = null;
        try {
            source = Okio.source(new File(fileName));
            bufferedSource = Okio.buffer(source);
            final String string = bufferedSource.readString(Charset.forName("utf-8"));
            System.out.println(string);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            OkUtils.closeSilently(source);
            OkUtils.closeSilently(bufferedSource);
        }
    }

    @Test
    public void writeTest() {
//        final String fileName = "src/test/write.txt";
        Sink sink = null;
        BufferedSink bufferedSink = null;
        try {
            sink = Okio.sink(new File(fileName));
            bufferedSink = Okio.buffer(sink);
            bufferedSink.writeString("okio test", Charset.forName("utf-8"));
            bufferedSink.writeUtf8(Arrays.toString(new byte[]{'\n'}));
            bufferedSink.writeString("okio test", Charset.forName("utf-8"));
            bufferedSink.writeUtf8(Arrays.toString(new byte[]{'\n'}));
            bufferedSink.writeString("okio test", Charset.forName("utf-8"));
            bufferedSink.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            OkUtils.closeSilently(sink);
            OkUtils.closeSilently(bufferedSink);
        }
    }
}
