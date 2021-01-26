package org.tenok.coin.data.websocket.impl;

import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

import org.json.simple.JSONObject;

public class BybitEncoder implements Encoder.Text<JSONObject> {

    @Override
    public void init(EndpointConfig config) { }

    @Override
    public void destroy() { }

    @Override
    public String encode(JSONObject object) throws EncodeException {
        return object.toJSONString();
    }
    
}
