package ru.yarskiy.safeguardcontrol.model;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "protective_equipment",
       uniqueConstraints = @UniqueConstraint(columnNames = "inventoryNumber"))
public class ProtectiveEquipment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "inventory_number", nullable = false, unique = true, length = 50)
    private String inventoryNumber;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(nullable = false, length = 100)
    @Enumerated(EnumType.STRING)
    private EquipmentType type;

    @Column(length = 100)
    private String manufacturer;

    @Column(name = "specification", length = 200)
    private String specification;

    @Column(name = "issue_date")
    private LocalDate issueDate;

    @Column(name = "last_test_date", nullable = false)
    private LocalDate lastTestDate;

    @Column(name = "next_test_date", nullable = false)
    private LocalDate nextTestDate;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private EquipmentStatus status;

    @Column(length = 1000)
    private String notes;

    // Конструкторы

    public ProtectiveEquipment() {}

    public ProtectiveEquipment(String inventoryNumber, String name, EquipmentType type) {
        this.inventoryNumber = inventoryNumber;
        this.name = name;
        this.type = type;
        this.status = EquipmentStatus.РАБОТОСПОСОБНО;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getInventoryNumber() { return inventoryNumber; }
    public void setInventoryNumber(String inventoryNumber) { this.inventoryNumber = inventoryNumber; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public EquipmentType getType() { return type; }
    public void setType(EquipmentType type) { this.type = type; }

    public String getManufacturer() { return manufacturer; }
    public void setManufacturer(String manufacturer) { this.manufacturer = manufacturer; }

    public String getSpecification() { return specification; }
    public void setSpecification(String specification) { this.specification = specification; }

    public LocalDate getIssueDate() { return issueDate; }
    public void setIssueDate(LocalDate issueDate) { this.issueDate = issueDate; }

    public LocalDate getLastTestDate() { return lastTestDate; }
    public void setLastTestDate(LocalDate lastTestDate) { this.lastTestDate = lastTestDate; }

    public LocalDate getNextTestDate() { return nextTestDate; }
    public void setNextTestDate(LocalDate nextTestDate) { this.nextTestDate = nextTestDate; }

    public EquipmentStatus getStatus() { return status; }
    public void setStatus(EquipmentStatus status) { this.status = status; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}