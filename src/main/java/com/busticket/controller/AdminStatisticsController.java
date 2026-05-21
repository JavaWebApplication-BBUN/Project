package com.busticket.controller;

import com.busticket.repository.RouteRepository;
import com.busticket.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/statistics")
public class AdminStatisticsController {

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private RouteRepository routeRepository;

    @GetMapping("/summary")
    public ResponseEntity<Map<String, Object>> getSummary() {
        // Compute summary metrics (can also be JPQL if preferred, here we just do basic counting/summing or lightweight calculation)
        long totalRoutes = routeRepository.count();
        long totalTickets = ticketRepository.countPaidTickets();
        Double rev = ticketRepository.getTotalRevenue();
        Double totalRevenue = rev != null ? rev : 0.0;

        Map<String, Object> summary = new HashMap<>();
        summary.put("totalRoutes", totalRoutes);
        summary.put("totalTickets", totalTickets);
        summary.put("totalRevenue", totalRevenue);
        
        return ResponseEntity.ok(summary);
    }

    @GetMapping("/revenue-by-route")
    public ResponseEntity<List<Object[]>> getRevenueByRoute() {
        return ResponseEntity.ok(ticketRepository.getRevenueByRoute());
    }

    @GetMapping("/revenue-by-month")
    public ResponseEntity<List<Object[]>> getRevenueByMonth() {
        return ResponseEntity.ok(ticketRepository.getRevenueByMonth());
    }

    @GetMapping("/top-trips")
    public ResponseEntity<List<Object[]>> getTopTrips() {
        return ResponseEntity.ok(ticketRepository.getTopTrips(PageRequest.of(0, 5)));
    }
}
