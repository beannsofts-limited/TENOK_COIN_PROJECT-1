package org.tenok.coin;

import java.util.Date;

import javax.websocket.*;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

@ClientEndpoint(encoders = { BybitEncoder.class }, // DIP 의존성주입
        decoders = { BybitDecoder.class })
public class BybitWebsocket {

    @OnOpen
    public void onOpen(Session session) {
        System.out.println("Connected to endpoint: " + session.getBasicRemote());
        try {
            App.session = session;
            System.out.println(App.session);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @OnMessage
    public void processMessage(JSONObject message) {
        // long data = (long) message.get("index_price_e4");
        JSONArray arr = (JSONArray) message.get("data");
        // System.out.println(String.format("리스트 크기: %d", arr.size()));
        // Object arrReturn = arr.get(0);
        // arrReturn.toString();
        // string + object -> string + object.toString()
        // JSONObject jsonObject = (JSONObject) arrReturn;
        // jsonObject.get("confirm");
        if ((boolean) ((JSONObject) arr.get(0)).get("confirm")) {

            if (((JSONObject) arr.get(1)).get("volume").toString().equals("0")) {
                System.out.println("Received message in client: open :  " + ((JSONObject) arr.get(0)).get("open"));
                System.out.println("Received message in client: close : " + ((JSONObject) arr.get(0)).get("close"));
            }
        }
        

    }

    @OnError
    public void processError(Throwable t) {
        t.printStackTrace();
    }
}
