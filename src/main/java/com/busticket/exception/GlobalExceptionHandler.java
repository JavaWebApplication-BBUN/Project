package com.busticket.exception;

import com.busticket.dto.ErrorResponse;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 1. Bắt các lỗi không tìm thấy dữ liệu (Sẽ trả về HTTP Status 404 - Not Found)
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(ResourceNotFoundException ex) {
        ErrorResponse error = new ErrorResponse(HttpStatus.NOT_FOUND.value(), ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    // 2. Bắt các lỗi xung đột nghiệp vụ (Sẽ trả về HTTP Status 409 - Conflict)
    @ExceptionHandler(SeatConflictException.class)
    public ResponseEntity<ErrorResponse> handleSeatConflict(SeatConflictException ex) {
        ErrorResponse error = new ErrorResponse(HttpStatus.CONFLICT.value(), ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    // 3. Bắt lỗi vi phạm ràng buộc dữ liệu (Sẽ trả về HTTP Status 409 - Conflict)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrity(DataIntegrityViolationException ex) {
        ErrorResponse error = new ErrorResponse(HttpStatus.CONFLICT.value(), "Dữ liệu bị trùng lặp hoặc vi phạm ràng buộc!");
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    // 4. Bắt các lỗi nghiệp vụ chung (Sẽ trả về HTTP Status 400 - Bad Request)
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException ex) {
        ErrorResponse error = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    // 5. Bắt CẢ NHỮNG LỖI CHƯA LƯỜNG TRƯỚC (VD: NullPointer, Lỗi kết nối DB...)
    // Đảm bảo server không bao giờ nhả ra một đống stacktrace khó hiểu cho người dùng
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(Exception ex) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Hệ thống đang gặp sự cố, vui lòng thử lại sau! Chi tiết: " + ex.getMessage()
        );
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}