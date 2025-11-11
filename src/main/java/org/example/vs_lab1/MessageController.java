package org.example.vs_lab1;

import org.example.vs_lab1.db.model.Message;
import org.example.vs_lab1.db.DatabaseHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class MessageController {

    private final DatabaseHelper databaseHelper;

    @Value("${server.port}")
    private String serverPort;

    @Value("${app.instance.id:unknown}")
    private String instanceId;

    @Autowired
    public MessageController(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    // Получение сообщения по ID
    @GetMapping("/messages/{id}")
    public ResponseEntity<?> getMessage(@PathVariable Long id) {
        try {
            System.out.println("[" + instanceId + "] Запрос сообщения с ID: " + id);

            Optional<Message> message = databaseHelper.getMessageById(id);

            if (message.isPresent()) {
                Map<String, Object> response = new HashMap<>();
                response.put("id", message.get().getId());
                response.put("text", message.get().getText());
                response.put("createdAt", message.get().getCreatedAt());
                response.put("senderIp", message.get().getSenderIp());
                response.put("instance", instanceId);

                System.out.println("[" + instanceId + "] Сообщение найдено: " + id);
                return ResponseEntity.ok(response);
            } else {
                System.out.println("[" + instanceId + "] Сообщение не найдено: " + id);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            System.err.println("[" + instanceId + "] Ошибка при получении сообщения: " + e.getMessage());
            return ResponseEntity.internalServerError().body("Ошибка БД: " + e.getMessage());
        }
    }

    // Health check
    @GetMapping("/health")
    public Map<String, String> health() {
        Map<String, String> healthInfo = new HashMap<>();
        try {
            healthInfo.put("status", "UP");
            healthInfo.put("instance", instanceId);
            healthInfo.put("host", InetAddress.getLocalHost().getHostName());
            healthInfo.put("port", serverPort);
        } catch (Exception e) {
            healthInfo.put("status", "ERROR");
            healthInfo.put("error", e.getMessage());
        }
        return healthInfo;
    }

    // Принудительное завершение приложения (для тестирования восстановления)
    @PostMapping("/break")
    public String breakInstance() {
        System.out.println("[" + instanceId + "] Принудительное завершение по запросу /break");
        System.exit(1);
        return "Завершение...";
    }
}