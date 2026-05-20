package com.busticket.exception;

// Lỗi logic nghiệp vụ chuyên biệt (Ghế đã bị đặt)
public class SeatConflictException extends RuntimeException {
    public SeatConflictException(String message) {
        super(message);
    }
}