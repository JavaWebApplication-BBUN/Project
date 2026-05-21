package com.busticket.dto;

public class BusDTO {
    private Long id;
    private String plateNumber;
    private String busType;
    private Integer totalSeats;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getPlateNumber() { return plateNumber; }
    public void setPlateNumber(String plateNumber) { this.plateNumber = plateNumber; }
    public String getBusType() { return busType; }
    public void setBusType(String busType) { this.busType = busType; }
    public Integer getTotalSeats() { return totalSeats; }
    public void setTotalSeats(Integer totalSeats) { this.totalSeats = totalSeats; }
}