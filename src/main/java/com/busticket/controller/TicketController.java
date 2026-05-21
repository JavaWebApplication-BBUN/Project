package com.busticket.controller;

import com.busticket.dto.TicketRequestDTO;
import com.busticket.dto.TicketResponseDTO;
import com.busticket.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    @Autowired
    private TicketService ticketService;

    @PostMapping("/book")
    public ResponseEntity<?> bookTickets(@RequestBody TicketRequestDTO request) {
        List<TicketResponseDTO> tickets = ticketService.bookTickets(request);
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Đặt vé thành công. Vui lòng thanh toán!");
        response.put("tickets", tickets);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchTicket(@RequestParam String ticketCode, @RequestParam String customerPhone) {
        List<TicketResponseDTO> tickets = ticketService.searchTicket(ticketCode, customerPhone);
        return ResponseEntity.ok(tickets);
    }
    
    @GetMapping("/my-tickets")
    public ResponseEntity<List<TicketResponseDTO>> getMyTickets() {
        return ResponseEntity.ok(ticketService.getMyTickets());
    }
    
    @PostMapping("/cancel")
    public ResponseEntity<?> cancelTicketByCustomer(@RequestParam String ticketCode, @RequestParam String customerPhone) {
        ticketService.cancelTicketByCustomer(ticketCode, customerPhone);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Đã hủy vé thành công. Vui lòng liên hệ hotline nếu cần hỗ trợ hoàn tiền.");
        return ResponseEntity.ok(response);
    }
    
    // --- STAFF ENDPOINTS ---
    
    @GetMapping("/staff/all")
    public ResponseEntity<?> getAllTickets() {
        return ResponseEntity.ok(ticketService.getAllTickets());
    }

    @PostMapping("/staff/approve/{id}")
    public ResponseEntity<?> approveTicketByStaff(@PathVariable Long id) {
        ticketService.approveTicket(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Đã duyệt thanh toán thành công.");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/staff/cancel/{id}")
    public ResponseEntity<?> cancelTicketByStaff(@PathVariable Long id) {
        ticketService.cancelTicketByStaff(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Đã hủy vé quá hạn thành công.");
        return ResponseEntity.ok(response);
    }
}
