package ru.kata.project.carmessenger.service;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import ru.kata.project.carmessenger.model.Car;
import ru.kata.project.carmessenger.model.User;
import ru.kata.project.carmessenger.repository.CarRepository;
import ru.kata.project.carmessenger.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CarService {

    private final CarRepository carRepository;
    private final UserRepository userRepository;

    public CarService(CarRepository carRepository, UserRepository userRepository) {
        this.carRepository = carRepository;
        this.userRepository = userRepository;
    }

    public Car addCar(Car car, String currentUserUsername) {
        // Используем findById который возвращает Optional
        Optional<User> userOptional = userRepository.findById(currentUserUsername);

        if (userOptional.isEmpty()) {
            throw new RuntimeException("Пользователь не найден");
        }

        User user = userOptional.get();
        car.setOwner(user);
        return carRepository.save(car);
    }

    // ВАЖНО: Этот метод возвращает ВСЕ автомобили
    public List<Car> getAllCars() {
        return carRepository.findAll();
    }

    // Этот метод возвращает только автомобили конкретного пользователя
    public List<Car> getUserCars(String username) {
        return carRepository.findByOwnerUsername(username);
    }

    public Car getCarById(Long id) {
        Optional<Car> carOptional = carRepository.findById(id);

        if (carOptional.isEmpty()) {
            throw new RuntimeException("Автомобиль не найден");
        }

        return carOptional.get();
    }

    public void deleteCar(Long id) {
        if (!carRepository.existsById(id)) {
            throw new RuntimeException("Автомобиль не найден");
        }
        carRepository.deleteById(id);
    }

    public String generateTelegramLink(String telegramUsername) {
        if (telegramUsername == null || telegramUsername.trim().isEmpty()) {
            return "#";
        }

        String cleanUsername = telegramUsername.trim();

        if (cleanUsername.startsWith("@")) {
            cleanUsername = cleanUsername.substring(1);
        }

        return "https://t.me/" + cleanUsername;
    }
}