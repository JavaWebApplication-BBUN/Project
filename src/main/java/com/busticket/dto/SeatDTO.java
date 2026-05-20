package com.busticket.dto;

public class SeatDTO {
    private Long id;
    private String seatNumber;
    private String status;

    public SeatDTO(Long id, String seatNumber, String status) {
        this.id = id;
        this.seatNumber = seatNumber;
        this.status = status;
    }

    public Long getId() { return id; }
    public String getSeatNumber() { return seatNumber; }
    public String getStatus() { return status; }
}