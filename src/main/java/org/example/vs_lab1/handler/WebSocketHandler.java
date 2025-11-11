package org.example.vs_lab1.handler;

import org.example.vs_lab1.db.DatabaseHelper;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class WebSocketHandler extends TextWebSocketHandler {

    private final List<WebSocketSession> sessions = new ArrayList<WebSocketSession>();
    private final DatabaseHelper databaseHelper;
    private final String instanceId;

    public WebSocketHandler(DatabaseHelper databaseHelper, String instanceId) {
        this.databaseHelper = databaseHelper;
        this.instanceId = instanceId;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.add(session);
        System.out.println("Новое соединение: " + session.getId());

        String clientIp = getClientIp(session);

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
        String clientIp = getClientIp(session);

        log("Получено сообщение от клиента: " + session.getId() + "\nтекст: " + messageFromClient);

        try {
            // Сохраняем сообщение в БД
            var savedMessage = databaseHelper.saveMessage(messageFromClient, clientIp);

            String response = createJsonMessage("Server",
                    "Сообщение сохранено в БД с ID: " + savedMessage.getId() + " (инстанс: " + instanceId + ")");
            session.sendMessage(new TextMessage(response));

            log("Сообщение сохранено в БД с ID: " + savedMessage.getId());

        } catch (Exception e) {
            String error = createJsonMessage("Server", "Ошибка сохранения в БД: " + e.getMessage());
            session.sendMessage(new TextMessage(error));
            log("Ошибка БД: " + e.getMessage());
        }
    }

    private String getClientIp(WebSocketSession session) {
        return session.getRemoteAddress() != null ?
                session.getRemoteAddress().getAddress().getHostAddress() : "unknown";
    }

    private void log(String message) {
        System.out.println("[" + instanceId + "] " + message);
    }

    private String createJsonMessage(String from, String text) {
        return String.format(
                "{\"from\":\"%s\", \"text\":\"%s\", \"timestamp\":\"%s\", \"instance\":\"%s\"}",
                from, text, getCurrentTime(), instanceId
        );
    }

    private String getCurrentTime() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
    }
}
