package com.busticket.service;

import com.busticket.dto.BusDTO;
import com.busticket.entity.Bus;
import com.busticket.exception.ResourceNotFoundException;
import com.busticket.repository.BusRepository;
import com.busticket.repository.TripRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BusService {

    @Autowired
    private BusRepository busRepository;

    @Autowired
    private TripRepository tripRepository;

    public List<BusDTO> getAllBuses() {
        return busRepository.findAll().stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    public BusDTO getBusById(Long id) {
        Bus bus = busRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy xe có ID: " + id));
        return mapToDTO(bus);
    }

    @Transactional
    public BusDTO createBus(BusDTO dto) {
        if (busRepository.findByPlateNumber(dto.getPlateNumber()).isPresent()) {
            throw new RuntimeException("Biển số xe '" + dto.getPlateNumber() + "' đã tồn tại trong hệ thống!");
        }
        Bus bus = new Bus();
        mapToEntity(dto, bus);
        try {
            return mapToDTO(busRepository.save(bus));
        } catch (DataIntegrityViolationException ex) {
            throw new RuntimeException("Biển số xe '" + dto.getPlateNumber() + "' đã tồn tại trong hệ thống!");
        }
    }

    @Transactional
    public BusDTO updateBus(Long id, BusDTO dto) {
        Bus bus = busRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy xe để cập nhật"));

        // Kiểm tra nếu đổi biển số thì biển số mới có bị trùng với xe khác không
        if (!bus.getPlateNumber().equals(dto.getPlateNumber()) &&
                busRepository.findByPlateNumber(dto.getPlateNumber()).isPresent()) {
            throw new RuntimeException("Biển số xe mới đã bị trùng với một xe khác!");
        }

        // Kiểm tra nếu thay đổi số ghế khi xe đã có chuyến đi
        if (!bus.getTotalSeats().equals(dto.getTotalSeats()) && tripRepository.existsByBusId(id)) {
            throw new RuntimeException("Không thể thay đổi số ghế vì xe đã được gán vào chuyến!");
        }

        mapToEntity(dto, bus);
        try {
            return mapToDTO(busRepository.save(bus));
        } catch (DataIntegrityViolationException ex) {
            throw new RuntimeException("Biển số xe mới đã bị trùng với một xe khác!");
        }
    }

    @Transactional
    public void deleteBus(Long id) {
        Bus bus = busRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy xe để xóa"));

        if (tripRepository.existsByBusId(id)) {
            throw new RuntimeException("Không thể xóa xe đã có chuyến đi!");
        }

        busRepository.delete(bus);
    }

    // Hàm Hỗ trợ Mapping Entity - DTO
    private BusDTO mapToDTO(Bus bus) {
        BusDTO dto = new BusDTO();
        dto.setId(bus.getId());
        dto.setPlateNumber(bus.getPlateNumber());
        dto.setBusType(bus.getBusType());
        dto.setTotalSeats(bus.getTotalSeats());
        return dto;
    }

    private void mapToEntity(BusDTO dto, Bus bus) {
        bus.setPlateNumber(dto.getPlateNumber());
        bus.setBusType(dto.getBusType());
        bus.setTotalSeats(dto.getTotalSeats());
    }
}