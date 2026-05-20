package com.busticket.exception;

// Lỗi khi tài nguyên không tìm thấy (Vé không tồn tại, Chuyến xe không có...)
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}