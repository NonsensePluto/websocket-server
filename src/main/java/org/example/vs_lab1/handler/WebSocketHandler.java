package org.example.vs_lab1.handler;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.ArrayList;
import java.util.List;

public class WebSocketHandler extends TextWebSocketHandler {

    public final List<WebSocketSession> sessions = new ArrayList<WebSocketSession>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.add(session);
        System.out.println("Новое соединение: " + session.getId());

        String welcomeString =  "{\"Server\":\" Привет от сервера \"}";
        session.sendMessage(new TextMessage(welcomeString));

        System.out.println("Кол-во сессий: " + sessions.size());

    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.remove(session);
        System.out.println("Cессия закрыта: " + session.getId());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String messageFromClient = message.getPayload();
        System.out.println("Получено сообщение от клиента: " + session.getId() + "текст: " + messageFromClient);

    }
}
