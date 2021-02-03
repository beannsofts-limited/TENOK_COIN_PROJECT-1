package org.tenok.coin.data.websocket.impl;

import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.tenok.coin.data.websocket.WebsocketResponseEnum;

public class BybitDecoder implements Decoder.Text<JSONObject> {

    @Override
    public void init(EndpointConfig config) {
    }

    @Override
    public void destroy() {
    }

    @Override
    @SuppressWarnings("unchecked")
    public JSONObject decode(String s) throws DecodeException {
        WebsocketResponseEnum resEnum = null;
        try {
            JSONObject jsonObject = (JSONObject) new JSONParser().parse(s);
            if (jsonObject.containsKey("ret_msg")) {
                String retMsg = (String) jsonObject.get("ret_msg");

                resEnum = retMsg.equals("pong") ? WebsocketResponseEnum.PING : WebsocketResponseEnum.SUBSCRIPTION;
            } else {
                resEnum = WebsocketResponseEnum.TOPIC;
            }

            jsonObject.put("response_type", resEnum);
            return jsonObject;
        } catch (ParseException e) {
            throw new DecodeException(s, "JSON Parse Failed", e);
        }
    }

    @Override
    public boolean willDecode(String s) {
        return true;
    }

}
