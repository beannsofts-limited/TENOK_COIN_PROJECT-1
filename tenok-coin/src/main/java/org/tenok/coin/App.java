package org.tenok.coin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.security.Timestamp;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.HttpsURLConnection;
import javax.websocket.*;

import org.json.simple.JSONObject;

import jdk.javadoc.internal.doclets.toolkit.resources.doclets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class App {

    static String api_key = "asld2k4fj12kd3";
    static String api_secret = "634dl1ld54el2";
    static Session session;
    public static final String MAINNET_PUBLIC_URI = "wss://stream.bybit.com/realtime_public";
    public static final String MAINNET_PRIVATE_URI = "wss://stream.bybit.com/realtime_private";

    public static String generate_signature(String expires) {
        return sha256_HMAC("GET/realtime" + expires, api_secret);
    }

    private static String byteArrayToHexString(byte[] b) {
        StringBuilder hs = new StringBuilder();
        String stmp;
        for (int n = 0; b != null && n < b.length; n++) {
            stmp = Integer.toHexString(b[n] & 0XFF);
            if (stmp.length() == 1)
                hs.append('0');
            hs.append(stmp);
        }
        return hs.toString().toLowerCase();
    }

    public static String sha256_HMAC(String message, String secret) {
        String hash = "";
        try {
            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret_key = new SecretKeySpec(secret.getBytes(), "HmacSHA256");
            sha256_HMAC.init(secret_key);
            byte[] bytes = sha256_HMAC.doFinal(message.getBytes());
            hash = byteArrayToHexString(bytes);
        } catch (Exception e) {
            System.out.println("Error HmacSHA256 ===========" + e.getMessage());
        }
        return hash;

    }

    @SuppressWarnings("unchecked")
    public static String getAuthMessage() {
        JSONObject req = new JSONObject();
        req.put("op", "auth");
        List<String> args = new LinkedList<String>();
        String expires = String.valueOf(System.currentTimeMillis() + 1000);
        args.add(api_key);
        args.add(expires);
        args.add(generate_signature(expires));
        req.put("args", args);
        return (req.toString());
    }

    @SuppressWarnings("unchecked")
    public static String subscribe(String op, String argv) {
        JSONObject req = new JSONObject();
        req.put("op", op);
        List<String> args = new LinkedList<String>();
        args.add(argv);
        req.put("args", args);
        return req.toString();
    }

    @SuppressWarnings("unchecked")
    public static void main(String[] args) throws MalformedURLException, IOException {
        Map<String, String> request = new HashMap<>();
        request.put("symbol", "LINKUS");
        request.put("interval", "15");
        request.put("limit", "200");
        request.put("from", "1611187200");
        
        StringBuilder url = new StringBuilder("https://api.bybit.com/public/linear/kline?");
        request.forEach((k, v) -> {
            url.append(String.format("%s=%s&", k, v));
        });
        url.deleteCharAt(url.length()-1);

        HttpsURLConnection conn = (HttpsURLConnection) new URL(url.toString())
                .openConnection();
                conn.setRequestMethod("GET");

                conn.setDoInput(true);

        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = br.readLine()) != null) {
            sb.append(line).append("\n");

        }

        br.close();

        System.out.println(sb);
        

//         try {
//             WebSocketContainer container = ContainerProvider.getWebSocketContainer();
//             //String uri = "wss://stream.bybit.com/realtime";
//             // String uri = "wss://stream.bytick.com/realtime";
//             // String uri = "wss://stream-testnet.bybit-cn.com/realtime";
//             // String uri = "wss://stream-testnet.bybit.com/realtime";
//             container.connectToServer(BybitWebsocket.class, URI.create(MAINNET_PUBLIC_URI));

//             JSONObject obj = new JSONObject();
//             obj.put("op", "ping");
//             session.getBasicRemote().sendObject(obj);
            
//             session.getBasicRemote().sendText(getAuthMessage());
//             // session.getBasicRemote().sendText(subscribe("subscribe", "instrument_info.100ms.BTCUSD"));
//             session.getBasicRemote().sendText(subscribe("subscribe","candle.1.BTCUSDT"));
// //            session.getBasicRemote().sendText(subscribe("subscribe", "order"));
//             java.io.BufferedReader r = new java.io.BufferedReader(new java.io.InputStreamReader( System.in));
//             while(true){
//                 String line=r.readLine();
//                 if(line.equals("quit")) break;
//                 session.getBasicRemote().sendText(line);
//             }

//         } catch (Exception ex) {
//             ex.printStackTrace();
//         }
    }
}






