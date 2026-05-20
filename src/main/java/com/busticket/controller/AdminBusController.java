package com.busticket.controller;

import com.busticket.dto.BusDTO;
import com.busticket.service.BusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/buses")
public class AdminBusController {

    @Autowired
    private BusService busService;

    @GetMapping
    public ResponseEntity<List<BusDTO>> getAllBuses() {
        return ResponseEntity.ok(busService.getAllBuses());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BusDTO> getBusById(@PathVariable Long id) {
        return ResponseEntity.ok(busService.getBusById(id));
    }

    @PostMapping
    public ResponseEntity<?> createBus(@RequestBody BusDTO busDTO) {
        BusDTO createdBus = busService.createBus(busDTO);
        return ResponseEntity.ok("Thêm xe thành công!");
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateBus(@PathVariable Long id, @RequestBody BusDTO busDTO) {
        BusDTO updatedBus = busService.updateBus(id, busDTO);
        return ResponseEntity.ok("Cập nhật thông tin xe thành công!");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBus(@PathVariable Long id) {
        busService.deleteBus(id);
        return ResponseEntity.ok("Đã xóa xe thành công!");
    }
}