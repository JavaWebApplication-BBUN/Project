package com.busticket.controller;

import com.busticket.dto.RouteDTO;
import com.busticket.entity.Location;
import com.busticket.entity.Route;
import com.busticket.repository.LocationRepository;
import com.busticket.repository.RouteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/admin/routes")
public class AdminRouteController {

    @Autowired
    private RouteRepository routeRepository;

    @Autowired
    private LocationRepository locationRepository;

    @GetMapping
    public ResponseEntity<List<RouteDTO>> getAllRoutes() {
        List<RouteDTO> routes = routeRepository.findAll().stream().map(this::convertToDTO).collect(Collectors.toList());
        return ResponseEntity.ok(routes);
    }

    @GetMapping("/locations")
    public ResponseEntity<List<Location>> getLocations() {
        return ResponseEntity.ok(locationRepository.findAll());
    }

    @PostMapping
    public ResponseEntity<?> createRoute(@RequestBody RouteDTO routeDTO) {
        if (routeDTO.getFromLocationId().equals(routeDTO.getToLocationId())) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Điểm đi và điểm đến không được trùng nhau!");
            return ResponseEntity.badRequest().body(response);
        }
        
        Location from = locationRepository.findById(routeDTO.getFromLocationId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy điểm đi"));
        Location to = locationRepository.findById(routeDTO.getToLocationId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy điểm đến"));
                
        Route route = new Route();
        route.setFromLocation(from);
        route.setToLocation(to);
        route.setDistanceKm(routeDTO.getDistanceKm());
        
        routeRepository.save(route);
        return ResponseEntity.ok(convertToDTO(route));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateRoute(@PathVariable Long id, @RequestBody RouteDTO routeDTO) {
        if (routeDTO.getFromLocationId().equals(routeDTO.getToLocationId())) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Điểm đi và điểm đến không được trùng nhau!");
            return ResponseEntity.badRequest().body(response);
        }

        Route route = routeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tuyến đường"));
                
        Location from = locationRepository.findById(routeDTO.getFromLocationId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy điểm đi"));
        Location to = locationRepository.findById(routeDTO.getToLocationId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy điểm đến"));
                
        route.setFromLocation(from);
        route.setToLocation(to);
        route.setDistanceKm(routeDTO.getDistanceKm());
        
        routeRepository.save(route);
        return ResponseEntity.ok(convertToDTO(route));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRoute(@PathVariable Long id) {
        routeRepository.deleteById(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Đã xóa tuyến đường thành công!");
        return ResponseEntity.ok(response);
    }

    private RouteDTO convertToDTO(Route route) {
        return new RouteDTO(
                route.getId(),
                route.getFromLocation().getId(),
                route.getFromLocation().getName(),
                route.getToLocation().getId(),
                route.getToLocation().getName(),
                route.getDistanceKm()
        );
    }
}
