# Используем легкий образ
FROM eclipse-temurin:21-jre-alpine

# Устанавливаем curl для healthcheck (нужен для Render)
RUN apk add --no-cache curl

# Создаем директорию приложения
WORKDIR /app

# Копируем JAR файл (предполагается, что вы собираете JAR)
COPY target/*.jar app.jar

# Создаем пользователя для безопасности
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Открываем порт
EXPOSE 8080

# Запускаем приложение
ENTRYPOINT ["java", "-jar", "app.jar"]