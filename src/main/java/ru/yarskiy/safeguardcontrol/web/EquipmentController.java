package ru.yarskiy.safeguardcontrol.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yarskiy.safeguardcontrol.model.EquipmentStatus;
import ru.yarskiy.safeguardcontrol.model.EquipmentType; // ✅ Добавлен импорт
import ru.yarskiy.safeguardcontrol.model.ProtectiveEquipment;
import ru.yarskiy.safeguardcontrol.service.EquipmentService;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/equipment")
@CrossOrigin(origins = "*")
public class EquipmentController {

    private final EquipmentService equipmentService;

    @Autowired
    public EquipmentController(EquipmentService equipmentService) {
        this.equipmentService = equipmentService;
    }

    @GetMapping
    public ResponseEntity<List<ProtectiveEquipment>> getAllEquipment() {
        List<ProtectiveEquipment> equipmentList = equipmentService.findAll();
        return ResponseEntity.ok(equipmentList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProtectiveEquipment> getEquipmentById(@PathVariable Long id) {
        return equipmentService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ProtectiveEquipment> addNewEquipment(@RequestBody Map<String, Object> payload) {
        try {
            String inventoryNumber = (String) payload.get("inventoryNumber");
            String name = (String) payload.get("name");
            String typeStr = (String) payload.get("type");
            String manufacturer = (String) payload.get("manufacturer");
            String specification = (String) payload.get("specification");

            LocalDate issueDate = parseLocalDate(payload.get("issueDate"));
            LocalDate lastTestDate = parseLocalDate(payload.get("lastTestDate"));

            if (inventoryNumber == null || name == null || typeStr == null || lastTestDate == null) {
                return ResponseEntity.badRequest().build();
            }

            EquipmentType type = EquipmentType.valueOf(typeStr); // ✅ Теперь компилятор знает, что такое EquipmentType

            ProtectiveEquipment equipment = equipmentService.addNewEquipment(
                    inventoryNumber, name, type, manufacturer, specification,
                    issueDate, lastTestDate
            );

            return ResponseEntity.ok(equipment);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProtectiveEquipment> updateEquipment(
            @PathVariable Long id,
            @RequestBody Map<String, Object> payload) {

        try {
            String name = (String) payload.get("name");
            String manufacturer = (String) payload.get("manufacturer");
            String specification = (String) payload.get("specification");

            LocalDate issueDate = parseLocalDate(payload.get("issueDate"));
            LocalDate lastTestDate = parseLocalDate(payload.get("lastTestDate"));

            String statusStr = (String) payload.get("status");
            EquipmentStatus status = statusStr != null ? EquipmentStatus.valueOf(statusStr) : null;

            String notes = (String) payload.get("notes");

            ProtectiveEquipment updated = equipmentService.updateEquipment(
                    id, name, manufacturer, specification,
                    issueDate, lastTestDate, status, notes
            );

            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/overdue")
    public ResponseEntity<List<ProtectiveEquipment>> getOverdueEquipment() {
        List<ProtectiveEquipment> overdue = equipmentService.findAll().stream()
                .filter(eq -> eq.getNextTestDate().isBefore(LocalDate.now()) ||
                              eq.getNextTestDate().isEqual(LocalDate.now()))
                .filter(eq -> eq.getStatus() != EquipmentStatus.СНЯТО_С_УЧЁТА)
                .toList();
        return ResponseEntity.ok(overdue);
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Long>> getEquipmentStats() {
        Map<String, Long> stats = new HashMap<>();
        for (EquipmentStatus status : EquipmentStatus.values()) {
            long count = equipmentService.countByStatus(status);
            stats.put(status.name(), count);
        }
        return ResponseEntity.ok(stats);
    }

    private LocalDate parseLocalDate(Object dateObj) {
        if (dateObj == null) return null;
        if (dateObj instanceof String str) {
            return LocalDate.parse(str);
        }
        if (dateObj instanceof LocalDate ld) {
            return ld;
        }
        return null;
    }
}