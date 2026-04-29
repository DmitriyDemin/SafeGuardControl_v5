package ru.yarskiy.safeguardcontrol.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yarskiy.safeguardcontrol.model.*;
import ru.yarskiy.safeguardcontrol.repository.ProtectiveEquipmentRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class EquipmentService {

    private final ProtectiveEquipmentRepository equipmentRepository;

    @Autowired
    public EquipmentService(ProtectiveEquipmentRepository equipmentRepository) {
        this.equipmentRepository = equipmentRepository;
    }

    // 🔹 1. Добавление нового СИЗ
    @Transactional
    public ProtectiveEquipment addNewEquipment(String inventoryNumber,
                                               String name,
                                               EquipmentType type,
                                               String manufacturer,
                                               String specification,
                                               LocalDate issueDate,
                                               LocalDate lastTestDate) {

        // Проверка на дубль по инвентарному номеру
        if (equipmentRepository.findByInventoryNumber(inventoryNumber).isPresent()) {
            throw new IllegalArgumentException("СИЗ с инвентарным номером " + inventoryNumber + " уже существует");
        }

        // Расчёт следующей даты проверки
        LocalDate nextTestDate = calculateNextTestDate(lastTestDate, type);

        ProtectiveEquipment equipment = new ProtectiveEquipment(inventoryNumber, name, type);
        equipment.setManufacturer(manufacturer);
        equipment.setSpecification(specification);
        equipment.setIssueDate(issueDate);
        equipment.setLastTestDate(lastTestDate);
        equipment.setNextTestDate(nextTestDate);
        equipment.setStatus(EquipmentStatus.РАБОТОСПОСОБНО);

        return equipmentRepository.save(equipment);
    }

    // 🔹 2. Обновление существующего СИЗ
    @Transactional
    public ProtectiveEquipment updateEquipment(Long id,
                                               String name,
                                               String manufacturer,
                                               String specification,
                                               LocalDate issueDate,
                                               LocalDate lastTestDate,
                                               EquipmentStatus status,
                                               String notes) {

        ProtectiveEquipment equipment = equipmentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("СИЗ с ID " + id + " не найдено"));

        equipment.setName(name);
        equipment.setManufacturer(manufacturer);
        equipment.setSpecification(specification);
        equipment.setIssueDate(issueDate);
        equipment.setLastTestDate(lastTestDate);

        // При обновлении даты последней проверки — пересчитываем следующую
        LocalDate nextTestDate = calculateNextTestDate(lastTestDate, equipment.getType());
        equipment.setNextTestDate(nextTestDate);

        // Если статус изменился — обновляем
        if (status != null) {
            equipment.setStatus(status);
        }
        equipment.setNotes(notes);

        return equipmentRepository.save(equipment);
    }

    // 🔹 3. Найти СИЗ по ID
    public Optional<ProtectiveEquipment> findById(Long id) {
        return equipmentRepository.findById(id);
    }

    // 🔹 4. Найти все СИЗ
    public List<ProtectiveEquipment> findAll() {
        return equipmentRepository.findAll();
    }

    // 🔹 5. Найти СИЗ по инвентарному номеру
    public Optional<ProtectiveEquipment> findByInventoryNumber(String inventoryNumber) {
        return equipmentRepository.findByInventoryNumber(inventoryNumber);
    }

    // 🔹 6. Автоматическая проверка просроченных СИЗ (запускается по расписанию)
    @Scheduled(cron = "0 0 6 * * ?") // Каждый день в 6:00 утра
    @Transactional
    public void checkOverdueEquipment() {
        LocalDate today = LocalDate.now();

        // Находим все СИЗ, которым пора проходить проверку
        List<ProtectiveEquipment> overdue = equipmentRepository.findExpiredOrDueForTesting(today);

        for (ProtectiveEquipment eq : overdue) {
            if (eq.getStatus() != EquipmentStatus.ТРЕБУЕТ_ПРОВЕРКИ) {
                eq.setStatus(EquipmentStatus.ТРЕБУЕТ_ПРОВЕРКИ);
                equipmentRepository.save(eq); // Изменения сохраняются благодаря @Transactional
            }
        }
    }

    // 🔹 7. Подсчёт количества СИЗ по статусу
    public long countByStatus(EquipmentStatus status) {
        return equipmentRepository.countByStatus(status);
    }

    // 🔹 8. Вспомогательный метод: рассчитывает следующую дату проверки
    private LocalDate calculateNextTestDate(LocalDate lastTestDate, EquipmentType type) {
        if (lastTestDate == null) {
            throw new IllegalArgumentException("Дата последней проверки не может быть null");
        }

        return switch (type) {
            case ДИЭЛЕКТРИЧЕСКИЕ_ПЕРЧАТКИ -> lastTestDate.plusMonths(6);
            case УКАЗАТЕЛЬ_НАПРЯЖЕНИЯ -> lastTestDate.plusYears(1);
            case ДИЭЛЕКТРИЧЕСКИЕ_БОТЫ -> lastTestDate.plusYears(3);
            case ИЗОЛИРУЮЩАЯ_ШТАНГА -> lastTestDate.plusYears(2);
            case ДИЭЛЕКТРИЧЕСКИЙ_КОВЕР -> lastTestDate.plusYears(6);
            case ОГРАЖДЕНИЕ -> lastTestDate.plusYears(1);
            case КЛЕЩИ_ДЛЯ_СНЯТИЯ_ПРЕДОХРАНИТЕЛЕЙ -> lastTestDate.plusYears(1);
            case ШЛЕМ_ЗАЩИТНЫЙ, ОЧКИ_ЗАЩИТНЫЕ -> lastTestDate.plusYears(1);
        };
    }
}