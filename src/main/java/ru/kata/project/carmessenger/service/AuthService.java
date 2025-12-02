package ru.kata.project.carmessenger.service;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;
import ru.kata.project.carmessenger.model.User;
import ru.kata.project.carmessenger.repository.UserRepository;

import java.util.Optional;

@Service
public class AuthService {
    private final UserRepository userRepository;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public boolean register(User user) {
        // Используем existsById вместо existsByUsername
        if (userRepository.existsById(user.getUsername())) {
            return false;
        }
        userRepository.save(user);
        return true;
    }

    public boolean login(String username, String password, HttpSession session) {
        // Используем findById вместо findByUsername
        Optional<User> userOptional = userRepository.findById(username);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (user.getPassword().equals(password)) {
                session.setAttribute("currentUser", username);
                return true;
            }
        }
        return false;
    }

    public boolean recoverPassword(String username, String securityAnswer, String newPassword) {
        Optional<User> userOptional = userRepository.findById(username);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (user.getSecurityAnswer().equalsIgnoreCase(securityAnswer)) {
                user.setPassword(newPassword);
                userRepository.save(user);
                return true;
            }
        }
        return false;
    }

    public String getSecurityQuestion(String username) {
        Optional<User> userOptional = userRepository.findById(username);
        return userOptional.map(User::getSecurityQuestion).orElse(null);
    }

    public boolean isLoggedIn(HttpSession session) {
        return session.getAttribute("currentUser") != null;
    }

    public String getCurrentUser(HttpSession session) {
        return (String) session.getAttribute("currentUser");
    }

    public void logout(HttpSession session) {
        session.invalidate();
    }
}