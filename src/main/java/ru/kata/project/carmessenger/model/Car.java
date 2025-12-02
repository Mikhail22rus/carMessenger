package ru.kata.project.carmessenger.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "cars")
public class Car {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String brand;


    @Column(name = "license_plate", nullable = false)
    private String licensePlate;

    @Column(name = "owner_telegram", nullable = false)
    private String ownerTelegram;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // Связь с таблицей app_users
    @ManyToOne
    @JoinColumn(name = "user_username", referencedColumnName = "username")
    private User owner;

    // Конструкторы
    public Car() {
        this.createdAt = LocalDateTime.now();
    }

    public Car(String brand, String model, String licensePlate, String ownerTelegram) {
        this();
        this.brand = brand;

        this.licensePlate = licensePlate;
        this.ownerTelegram = ownerTelegram;
    }

    // Геттеры и сеттеры


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }


    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    public String getOwnerTelegram() {
        return ownerTelegram;
    }

    public void setOwnerTelegram(String ownerTelegram) {
        this.ownerTelegram = ownerTelegram;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }
}