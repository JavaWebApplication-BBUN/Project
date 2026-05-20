package com.busticket.service;

import com.busticket.dto.BusDTO;
import com.busticket.entity.Bus;
import com.busticket.exception.ResourceNotFoundException;
import com.busticket.repository.BusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BusService {

    @Autowired
    private BusRepository busRepository;

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
        return mapToDTO(busRepository.save(bus));
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

        mapToEntity(dto, bus);
        return mapToDTO(busRepository.save(bus));
    }

    @Transactional
    public void deleteBus(Long id) {
        Bus bus = busRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy xe để xóa"));
        busRepository.delete(bus);
    }

    //Hàm Hỗ trợ Mapping Entity - DTO
    private BusDTO mapToDTO(Bus bus) {
        BusDTO dto = new BusDTO();
        dto.setId(bus.getId());
        dto.setPlateNumber(bus.getPlateNumber());
        dto.setBusType(bus.getBusType());
        dto.setTotalSeats(bus.getTotalSeats());
        dto.setDriverName(bus.getDriverName());
        return dto;
    }

    private void mapToEntity(BusDTO dto, Bus bus) {
        bus.setPlateNumber(dto.getPlateNumber());
        bus.setBusType(dto.getBusType());
        bus.setTotalSeats(dto.getTotalSeats());
        bus.setDriverName(dto.getDriverName());
    }
}