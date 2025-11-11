FROM eclipse-temurin:17-jre-alpine

# Установка curl для проверки health
RUN apk update && apk add curl

# Создаем директорию для логов
RUN mkdir -p /shared-logs

# Копируем JAR
COPY build/libs/*.jar app.jar

# Создаем пользователя для безопасности
RUN addgroup -S spring && adduser -S spring -G spring
RUN chown -R spring:spring /shared-logs
USER spring

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app.jar"]