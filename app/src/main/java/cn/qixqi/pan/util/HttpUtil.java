package cn.qixqi.pan.util;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.WebSocketListener;


public class HttpUtil {

    // OkHttp Get 方式获取数据
    public static void sendOkHttpRequest(final String address, okhttp3.Callback callback){
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(address)
                .build();
        client.newCall(request).enqueue(callback);
    }


    // OkHttp Post 方式提交数据
    public static void sendOkHttpRequest(final String address, RequestBody requestBody, okhttp3.Callback callback){
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(address)
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(callback);
    }


    // OkHttp WebSocket 连接方式
    public static void sendOkHttpRequestWithWebSocket(final String address, WebSocketListener listener){
        OkHttpClient client = new OkHttpClient.Builder()
                .readTimeout(3, TimeUnit.SECONDS)
                .writeTimeout(3, TimeUnit.SECONDS)
                .connectTimeout(3, TimeUnit.SECONDS)
                .build();
        Request request = new Request.Builder()
                .url(address)
                .build();
        client.newWebSocket(request, listener);
        client.dispatcher().executorService().shutdown();
    }



}
