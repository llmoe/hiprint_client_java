package com.llmoe.print;


import com.llmoe.print.pojo.PushMessage;
import io.socket.client.IO;
import io.socket.client.IO.Options;
import io.socket.client.Socket;
import io.socket.emitter.Emitter.Listener;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

/**
 * Java Socket.io 客户端代码
 *
 * @author LLmoe
 */
public class SocketClient {

    public static void main(String[] args) throws URISyntaxException, JSONException {
        Options options = new Options();
        options.transports = new String[]{"websocket", "xhr-polling", "jsonp-polling"};
        options.reconnectionAttempts = 2;
        options.reconnectionDelay = 1000;
        options.timeout = 500;

        final Socket socket = IO.socket("http://127.0.0.1:17521", options);

        socket.on(Socket.EVENT_CONNECT, new Listener() {
            @Override
            public void call(Object... objects) {
                System.out.println("client connect...");
            }
        });

        socket.on(Socket.EVENT_DISCONNECT, new Listener() {
            @Override
            public void call(Object... objects) {
                System.out.println("disconnect ... ");
            }
        });

        socket.on(Socket.EVENT_MESSAGE, new Listener() {
            @Override
            public void call(Object... objects) {
                socket.send();
            }
        });

        socket.connect();
        PushMessage pushMessage = new PushMessage();
        pushMessage.setUsername("xxx");
        pushMessage.setMessage("msg");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("username", "asd");
        jsonObject.put("message", "66");
        socket.send(jsonObject);

    }
}
