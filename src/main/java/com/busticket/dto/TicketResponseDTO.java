package com.busticket.dto;

import java.time.LocalDateTime;

public class TicketResponseDTO {
    private Long id;
    private String ticketCode;
    private String customerName;
    private String customerPhone;
    private Double totalPrice;
    private String status;
    private LocalDateTime bookingTime;
    
    // Thông tin JOIN
    private String seatNumber;
    private Long tripId;
    private String busPlate;
    private String routeStart;
    private String routeEnd;
    private LocalDateTime departureTime;
    private String driverName;

    public TicketResponseDTO() {}

    public TicketResponseDTO(String ticketCode, String customerName, String customerPhone, 
                             Double totalPrice, String status, LocalDateTime bookingTime, 
                             String seatNumber, Long tripId, String busPlate, 
                             String routeStart, String routeEnd, LocalDateTime departureTime,
                             String driverName) {
        this.ticketCode = ticketCode;
        this.customerName = customerName;
        this.customerPhone = customerPhone;
        this.totalPrice = totalPrice;
        this.status = status;
        this.bookingTime = bookingTime;
        this.seatNumber = seatNumber;
        this.tripId = tripId;
        this.busPlate = busPlate;
        this.routeStart = routeStart;
        this.routeEnd = routeEnd;
        this.departureTime = departureTime;
        this.driverName = driverName;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTicketCode() { return ticketCode; }
    public void setTicketCode(String ticketCode) { this.ticketCode = ticketCode; }
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public String getCustomerPhone() { return customerPhone; }
    public void setCustomerPhone(String customerPhone) { this.customerPhone = customerPhone; }
    public Double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(Double totalPrice) { this.totalPrice = totalPrice; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getBookingTime() { return bookingTime; }
    public void setBookingTime(LocalDateTime bookingTime) { this.bookingTime = bookingTime; }
    public String getSeatNumber() { return seatNumber; }
    public void setSeatNumber(String seatNumber) { this.seatNumber = seatNumber; }
    public Long getTripId() { return tripId; }
    public void setTripId(Long tripId) { this.tripId = tripId; }
    public String getBusPlate() { return busPlate; }
    public void setBusPlate(String busPlate) { this.busPlate = busPlate; }
    public String getRouteStart() { return routeStart; }
    public void setRouteStart(String routeStart) { this.routeStart = routeStart; }
    public String getRouteEnd() { return routeEnd; }
    public void setRouteEnd(String routeEnd) { this.routeEnd = routeEnd; }
    public LocalDateTime getDepartureTime() { return departureTime; }
    public void setDepartureTime(LocalDateTime departureTime) { this.departureTime = departureTime; }
    public String getDriverName() { return driverName; }
    public void setDriverName(String driverName) { this.driverName = driverName; }
}
