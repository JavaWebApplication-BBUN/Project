package com.busticket.controller;

import com.busticket.dto.SeatDTO;
import com.busticket.dto.TripSummaryDTO;
import com.busticket.service.TripService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/trips")
public class TripAPIController {

    @Autowired private TripService tripService;

    @GetMapping("/search")
    public ResponseEntity<List<TripSummaryDTO>> searchTrips(
            @RequestParam Long fromId,
            @RequestParam Long toId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(tripService.searchTrips(fromId, toId, date));
    }

    // Chọn ghế -> Trả về sơ đồ
    @GetMapping("/{tripId}/seats")
    public ResponseEntity<List<SeatDTO>> getSeatMap(@PathVariable Long tripId) {
        return ResponseEntity.ok(tripService.getSeatMap(tripId));
    }
}