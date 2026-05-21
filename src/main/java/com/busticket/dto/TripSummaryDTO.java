package com.busticket.dto;

import java.time.LocalDateTime;

public class TripSummaryDTO {
    private Long tripId;
    private String fromLocation;
    private String toLocation;
    private String busPlate;
    private String busType;
    private String driverName;
    private LocalDateTime departureTime;
    private Double price;
    private long availableSeats;
    private boolean canBook;

    public TripSummaryDTO(Long tripId, String fromLocation, String toLocation, String busPlate, String busType, String driverName,
                          LocalDateTime departureTime, Double price, long availableSeats, boolean canBook) {
        this.tripId = tripId;
        this.fromLocation = fromLocation;
        this.toLocation = toLocation;
        this.busPlate = busPlate;
        this.busType = busType;
        this.driverName = driverName;
        this.departureTime = departureTime;
        this.price = price;
        this.availableSeats = availableSeats;
        this.canBook = canBook;
    }

    public Long getTripId() { return tripId; }
    public String getFromLocation() { return fromLocation; }
    public String getToLocation() { return toLocation; }
    public String getBusPlate() { return busPlate; }
    public String getBusType() { return busType; }
    public String getDriverName() { return driverName; }
    public LocalDateTime getDepartureTime() { return departureTime; }
    public Double getPrice() { return price; }
    public long getAvailableSeats() { return availableSeats; }
    public boolean isCanBook() { return canBook; }
}