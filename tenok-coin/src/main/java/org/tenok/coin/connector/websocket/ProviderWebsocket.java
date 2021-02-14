package org.tenok.coin.connector.websocket;

import javax.websocket.EndpointConfig;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint("/tenok/server")
public class ProviderWebsocket {
    @OnOpen
    public void onOpen(Session session, EndpointConfig config) {

    }

    @OnMessage
    public void onMessage(String message, Session session) {

    }
}
