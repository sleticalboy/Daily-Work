package com.binlee.emoji.third;

import androidx.annotation.NonNull;

import com.binlee.emoji.compat.HttpEngine;
import com.binlee.emoji.helper.LogHelper;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.URI;
import java.nio.charset.Charset;

public class SocketHttpEngine extends HttpEngine<Socket, Socket> {

    private static final String TAG = "SocketHttpEngine";

    private static final Charset UTF_8 = Charset.forName("UTF-8");
    private static final byte[] CRLF = {'\r', '\n'};
    private static final byte[] COLONSPACE = {':', ' '};

    private final Config mConfig;

    public SocketHttpEngine() {
        mConfig = createConfig();
    }

    @Override
    protected void setupClient(Object client) {
    }

    @Override
    public BaseResponse request(@NonNull BaseRequest request) throws IOException {
        final Socket socket = adaptRequest(request);
        writeRequest(socket, request);
        return adaptResponse(socket, request);
    }

    @Override
    public void request(@NonNull BaseRequest request, @NonNull Callback callback) {
    }

    @Override
    protected Socket adaptRequest(BaseRequest request) throws IOException {
        return createSocket(request);
    }

    @Override
    protected BaseResponse adaptResponse(Socket raw, BaseRequest current) throws IOException {
        if (raw.isClosed()) {
            return null;
        }
        final BaseResponse response = new BaseResponse();
        response.current = current;
        final BufferedInputStream in = new BufferedInputStream(raw.getInputStream());

        // read status line
        int ch;
        StringBuilder buf = new StringBuilder();
        while ((ch = in.read()) != '\n') {
            if (ch == '\r') {
                continue;
            }
            buf.append(((char) ch));
        }
        final String statusLine = buf.toString();
        LogHelper.debug(TAG, "status line: " + buf);
        final String[] elements = statusLine.split(" ");
        if (elements.length > 2) {
            response.protocol = elements[0];
            response.code = Integer.parseInt(elements[1]);
            response.msg = statusLine.substring(statusLine.indexOf(elements[1]) + 4);
        }

        // read response header
        buf = new StringBuilder();
        while ((ch = in.read()) != -1) {
            if (ch == '\r') {
                continue;
            } else if (ch == '\n') {
                break;
            }
            buf.append(((char) ch));
        }
        final String headers = buf.toString();
        LogHelper.debug(TAG, "response headers:\n" + headers);

        // read response body

        raw.close();
        trace(TAG, response);
        return response;
    }

    private Socket createSocket(BaseRequest request) throws IOException {
        final URI uri = URI.create(request.url);
        int port = uri.getPort();
        if (port < 0) {
            final String scheme = uri.getScheme();
            if ("http".equals(scheme.toLowerCase())) {
                port = 80;
            } else if ("https".equals(scheme.toLowerCase())) {
                port = 443;
            }
        }
        // final Socket socket = SocketFactory.getDefault().createSocket();
        final Socket socket = new Socket();
        SocketAddress endpoint = new InetSocketAddress(uri.getHost(), port);
        socket.connect(endpoint, mConfig.connectTimeout * 1000);
        return socket;
    }

    private void writeRequest(Socket socket, BaseRequest request) throws IOException {
        final BufferedOutputStream out = new BufferedOutputStream(socket.getOutputStream());
        // write request line
        out.write(request.method.getBytes(UTF_8));
        out.write(COLONSPACE[1]);
        out.write(buildUrl(request).getBytes(UTF_8));
        out.write(COLONSPACE[1]);
        out.write("HTTP/1.1".getBytes(UTF_8));
        out.write(CRLF);/*request line end*/

        // write headers
        if (request.headers != null && request.headers.size() != 0) {
            for (String name : request.headers.keySet()) {
                out.write(name.getBytes(UTF_8));
                out.write(COLONSPACE);
                out.write(request.headers.get(name).getBytes(UTF_8));
                out.write(CRLF);
            }
            out.write(CRLF);/*header end*/
            out.flush();
        }

        // write request body
        if (request.params != null && request.params.size() != 0) {
            // have not implemented yet
        }

        out.write(CRLF);/*request end*/
        out.flush();
    }
}
