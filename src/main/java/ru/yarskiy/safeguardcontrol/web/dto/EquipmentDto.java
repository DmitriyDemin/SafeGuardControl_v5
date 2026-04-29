package ru.yarskiy.safeguardcontrol.web.dto;

import java.time.LocalDate;

public class EquipmentDto {
    private String inventoryNumber;
    private String name;
    private String type;
    private String manufacturer;
    private String specification;
    private LocalDate issueDate;
    private LocalDate lastTestDate;
    private String status;
    private String notes;

    // Getters and Setters
    public String getInventoryNumber() { return inventoryNumber; }
    public void setInventoryNumber(String inventoryNumber) { this.inventoryNumber = inventoryNumber; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getManufacturer() { return manufacturer; }
    public void setManufacturer(String manufacturer) { this.manufacturer = manufacturer; }

    public String getSpecification() { return specification; }
    public void setSpecification(String specification) { this.specification = specification; }

    public LocalDate getIssueDate() { return issueDate; }
    public void setIssueDate(LocalDate issueDate) { this.issueDate = issueDate; }

    public LocalDate getLastTestDate() { return lastTestDate; }
    public void setLastTestDate(LocalDate lastTestDate) { this.lastTestDate = lastTestDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}