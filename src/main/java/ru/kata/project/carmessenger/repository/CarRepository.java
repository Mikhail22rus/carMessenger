package ru.kata.project.carmessenger.repository;


import ru.kata.project.carmessenger.model.Car;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CarRepository extends JpaRepository<Car, Long> {



    List<Car> findByOwnerUsername(String username);

    // Дополнительные методы если нужны:
    List<Car> findByOwnerTelegram(String ownerTelegram);
    List<Car> findByLicensePlate(String licensePlate);
    List<Car> findByBrand(String brand);
}