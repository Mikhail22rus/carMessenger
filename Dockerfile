# Используем JDK (чтобы собрать) и JRE (чтобы запустить) в одном образе
FROM eclipse-temurin:21-jdk-alpine

# Устанавливаем Maven и curl
RUN apk add --no-cache maven curl

WORKDIR /app

# Копируем все файлы проекта
COPY . .

# Собираем проект
RUN mvn clean package -DskipTests

# Запускаем
EXPOSE 8080

CMD ["java", "-jar", "target/CarMessenger-0.0.1-SNAPSHOT.jar"]