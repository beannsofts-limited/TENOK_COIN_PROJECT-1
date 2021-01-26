package org.tenok.coin.data.websocket.impl;

import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class BybitDecoder implements Decoder.Text<JSONObject> {

    @Override
    public void init(EndpointConfig config) { }

    @Override
    public void destroy() { }

    @Override
    public JSONObject decode(String s) throws DecodeException {
        try {
            return (JSONObject) new JSONParser().parse(s);
        } catch (ParseException e) {
            throw new DecodeException(s, "JSON Parse Failed", e);
        }
    }

    @Override
    public boolean willDecode(String s) {
        return true;
    }
    
}
