package com.busticket.dto;

import java.time.LocalDateTime;

public class TripSummaryDTO {
    private Long tripId;
    private String busPlate;
    private String busType;
    private String driverName;
    private LocalDateTime departureTime;
    private Double price;
    private long availableSeats;

    public TripSummaryDTO(Long tripId, String busPlate, String busType, String driverName,
                          LocalDateTime departureTime, Double price, long availableSeats) {
        this.tripId = tripId;
        this.busPlate = busPlate;
        this.busType = busType;
        this.driverName = driverName;
        this.departureTime = departureTime;
        this.price = price;
        this.availableSeats = availableSeats;
    }

    public Long getTripId() { return tripId; }
    public String getBusPlate() { return busPlate; }
    public String getBusType() { return busType; }
    public String getDriverName() { return driverName; }
    public LocalDateTime getDepartureTime() { return departureTime; }
    public Double getPrice() { return price; }
    public long getAvailableSeats() { return availableSeats; }
}