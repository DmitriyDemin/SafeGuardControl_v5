package ru.yarskiy.safeguardcontrol.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.yarskiy.safeguardcontrol.model.EquipmentStatus;
import ru.yarskiy.safeguardcontrol.model.ProtectiveEquipment;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository  // ⬅️ Помечаем как компонент Spring
public interface ProtectiveEquipmentRepository extends JpaRepository<ProtectiveEquipment, Long> {

    // 🔹 1. Найти СИЗ по инвентарному номеру
    Optional<ProtectiveEquipment> findByInventoryNumber(String inventoryNumber);

    // 🔹 2. Найти все СИЗ по типу
    List<ProtectiveEquipment> findByType(String type);

    // 🔹 3. Найти все СИЗ по статусу
    List<ProtectiveEquipment> findByStatus(EquipmentStatus status);

    // 🔹 4. Найти просроченные СИЗ: nextTestDate <= today
    @Query("SELECT p FROM ProtectiveEquipment p WHERE p.nextTestDate <= :today AND p.status <> 'СНЯТО_С_УЧЁТА'")
    List<ProtectiveEquipment> findExpiredOrDueForTesting(@Param("today") LocalDate today);

    // 🔹 5. Найти СИЗ, у которых проверка истекает в ближайшие N дней
    @Query("SELECT p FROM ProtectiveEquipment p " +
           "WHERE p.nextTestDate BETWEEN :startDate AND :endDate " +
           "AND p.status <> 'СНЯТО_С_УЧЁТА'")
    List<ProtectiveEquipment> findDueForTestingSoon(
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );

    // 🔹 6. Подсчёт количества СИЗ по статусу
    long countByStatus(EquipmentStatus status);
}