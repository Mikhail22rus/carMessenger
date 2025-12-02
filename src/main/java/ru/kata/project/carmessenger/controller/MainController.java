package ru.kata.project.carmessenger.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.kata.project.carmessenger.model.User;
import ru.kata.project.carmessenger.model.Car;
import ru.kata.project.carmessenger.service.AuthService;
import ru.kata.project.carmessenger.service.CarService;

import java.util.List;

@Controller
public class MainController {

    private final CarService carService;
    private final AuthService authService;

    public MainController(CarService carService, AuthService authService) {
        this.carService = carService;
        this.authService = authService;
    }

    // ========== АВТОРИЗАЦИЯ ==========
    @GetMapping("/")
    public String home(HttpSession session) {
        if (authService.isLoggedIn(session)) {
            // Если пользователь авторизован - перенаправляем на список автомобилей
            return "redirect:/cars/list";
        } else {
            // Если не авторизован - на страницу логина
            return "redirect:/auth/login";
        }
    }
    @GetMapping("/auth/login")
    public String showLoginPage() {
        return "login";
    }

    @PostMapping("/auth/login")
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        HttpSession session,
                        Model model) {
        if (authService.login(username, password, session)) {
            return "redirect:/cars/list";
        } else {
            model.addAttribute("error", "Неверный логин или пароль");
            return "login";
        }
    }

    @GetMapping("/auth/register")
    public String showRegisterPage() {
        return "register";
    }
    @PostMapping("/auth/register")
    public String register(@RequestParam("username") String username,
                           @RequestParam("password") String password,
                           @RequestParam("securityQuestion") String securityQuestion,
                           @RequestParam("securityAnswer") String securityAnswer,
                           @RequestParam("carBrand") String carBrand,
                           @RequestParam("carLicensePlate") String carLicensePlate,
                           HttpSession session,
                           Model model) {

        try {
            System.out.println("=== DEBUG Регистрация ===");
            System.out.println("Username: " + username);
            System.out.println("Car Brand: " + carBrand);
            System.out.println("License Plate: " + carLicensePlate);

            // 1. Создаем пользователя
            User user = new User();
            user.setUsername(username);
            user.setPassword(password);
            user.setSecurityQuestion(securityQuestion);
            user.setSecurityAnswer(securityAnswer);

            // 2. Регистрируем пользователя
            if (!authService.register(user)) {
                model.addAttribute("error", "Пользователь с таким именем уже существует");
                return "register";
            }

            // 3. Автоматически логиним
            authService.login(username, password, session);

            // 4. Создаем и добавляем автомобиль
            Car car = new Car();
            car.setBrand(carBrand);
            car.setLicensePlate(carLicensePlate);
            car.setOwnerTelegram(username);

            System.out.println("Создаем авто: " + car.getBrand() +
                    ", номер: " + car.getLicensePlate() +
                    ", владелец: " + car.getOwnerTelegram());

            Car savedCar = carService.addCar(car, username);

            System.out.println("Авто сохранено с ID: " + savedCar.getId());

            // 5. Перенаправляем на список автомобилей
            return "redirect:/cars/list?registered=true";

        } catch (Exception e) {
            System.out.println("Ошибка регистрации: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("error", "Ошибка регистрации: " + e.getMessage());
            return "register";
        }
    }

    @GetMapping("/auth/recovery")
    public String showRecoveryPage() {
        return "recovery";
    }

    @GetMapping("/auth/recovery/question")
    public String getSecurityQuestion(@RequestParam String username, Model model) {
        String question = authService.getSecurityQuestion(username);
        if (question == null) {
            model.addAttribute("error", "Пользователь не найден");
            return "recovery";
        }
        model.addAttribute("username", username);
        model.addAttribute("question", question);
        return "recovery-answer";
    }

    @PostMapping("/auth/recovery/reset")
    public String resetPassword(@RequestParam String username,
                                @RequestParam String answer,
                                @RequestParam String newPassword,
                                Model model) {
        if (authService.recoverPassword(username, answer, newPassword)) {
            model.addAttribute("success", "Пароль успешно изменен");
            return "login";
        } else {
            model.addAttribute("error", "Неверный ответ на контрольный вопрос");
            return "recovery-answer";
        }
    }

    @GetMapping("/auth/logout")
    public String logout(HttpSession session) {
        authService.logout(session);
        return "redirect:/auth/login";
    }

    // ========== АВТОМОБИЛИ ==========

    @GetMapping("/cars/list")
    public String listCars(HttpSession session, Model model) {
        if (!authService.isLoggedIn(session)) {
            return "redirect:/auth/login";
        }

        String currentUser = authService.getCurrentUser(session);
        List<Car> allCars = carService.getAllCars();

        model.addAttribute("cars", allCars);
        model.addAttribute("currentUser", currentUser);

        return "car-list";
    }

    @GetMapping("/cars/add")
    public String showAddCarForm(HttpSession session, Model model) {
        if (!authService.isLoggedIn(session)) {
            return "redirect:/auth/login";
        }

        String currentUser = authService.getCurrentUser(session);
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("car", new Car());

        return "car-add";
    }

    @PostMapping("/cars/add")
    public String addCar(@ModelAttribute Car car,
                         HttpSession session,
                         Model model) {
        if (!authService.isLoggedIn(session)) {
            return "redirect:/auth/login";
        }

        String currentUser = authService.getCurrentUser(session);

        try {
            carService.addCar(car, currentUser);
            return "redirect:/cars/list?added=true";

        } catch (Exception e) {
            model.addAttribute("error", "Ошибка при добавлении автомобиля: " + e.getMessage());
            model.addAttribute("currentUser", currentUser);
            model.addAttribute("car", car);
            return "car-add";
        }
    }

    @GetMapping("/cars/message/{id}")
    public String messageCarOwner(@PathVariable Long id, HttpSession session) {
        if (!authService.isLoggedIn(session)) {
            return "redirect:/auth/login";
        }

        Car car = carService.getCarById(id);
        if (car != null) {
            String telegramLink = carService.generateTelegramLink(car.getOwnerTelegram());
            return "redirect:" + telegramLink;
        }

        return "redirect:/cars/list";
    }

    @GetMapping("/cars/delete/{id}")
    public String deleteCar(@PathVariable Long id, HttpSession session) {
        if (!authService.isLoggedIn(session)) {
            return "redirect:/auth/login";
        }

        String currentUser = authService.getCurrentUser(session);

        try {
            // Проверяем, является ли пользователь владельцем авто
            Car car = carService.getCarById(id);
            if (car != null && car.getOwnerTelegram().equals(currentUser)) {
                carService.deleteCar(id);
                return "redirect:/cars/list?deleted=true";
            } else {
                return "redirect:/cars/list?notOwner=true";
            }
        } catch (Exception e) {
            return "redirect:/cars/list?deleteError=true";
        }
    }
}