package org.example.vs_lab1.db;

import org.example.vs_lab1.db.model.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
public class DatabaseHelper {

    private final DataSource dataSource;

    @Autowired
    public DatabaseHelper(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Message saveMessage(String text, String senderIp) {
        String sql = "INSERT INTO messages (text, created_at, sender_ip) VALUES (?, ?, ?)";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            // Имитация задержки
            TimeUnit.MILLISECONDS.sleep(200);

            statement.setString(1, text);
            statement.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            statement.setString(3, senderIp);

            int affectedRows = statement.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating message failed, no rows affected.");
            }

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    Long id = generatedKeys.getLong(1);

                    // Дополнительная задержка
                    TimeUnit.MILLISECONDS.sleep(100);

                    return new Message(id, text, LocalDateTime.now(), senderIp);
                } else {
                    throw new SQLException("Creating message failed, no ID obtained.");
                }
            }
        } catch (SQLException | InterruptedException e) {
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            throw new RuntimeException("Database operation failed", e);
        }
    }

    public Optional<Message> getMessageById(Long id) {
        String sql = "SELECT id, text, created_at, sender_ip FROM messages WHERE id = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            // Имитация задержки чтения
            TimeUnit.MILLISECONDS.sleep(150);

            statement.setLong(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    Message message = new Message();
                    message.setId(resultSet.getLong("id"));
                    message.setText(resultSet.getString("text"));
                    message.setCreatedAt(resultSet.getTimestamp("created_at").toLocalDateTime());
                    message.setSenderIp(resultSet.getString("sender_ip"));

                    // Дополнительная задержка
                    TimeUnit.MILLISECONDS.sleep(50);

                    return Optional.of(message);
                } else {
                    return Optional.empty();
                }
            }
        } catch (SQLException | InterruptedException e) {
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            throw new RuntimeException("Database operation failed", e);
        }
    }
}
