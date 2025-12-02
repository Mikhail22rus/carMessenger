package ru.kata.project.carmessenger.config;



import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    // Можно добавить интерцепторы для проверки авторизации
    // но для простоты используем проверку в контроллерах
}