package org.tenok.coin.data.websocket.impl;

import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

import org.json.simple.JSONObject;

public class BybitEncoder implements Encoder.Text<JSONObject> {

    @Override
    public void init(EndpointConfig config) {
        // TODO Auto-generated method stub

    }

    @Override
    public void destroy() {
        // TODO Auto-generated method stub

    }

    @Override
    public String encode(JSONObject object) throws EncodeException {
        // TODO Auto-generated method stub
        return null;
    }
    
}
