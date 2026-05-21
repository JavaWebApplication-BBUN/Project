package com.busticket.service;

import com.busticket.dto.SeatDTO;
import com.busticket.dto.TripSummaryDTO;
import com.busticket.entity.Trip;
import com.busticket.repository.SeatRepository;
import com.busticket.repository.TripRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TripService {

    @Autowired private TripRepository tripRepository;
    @Autowired private SeatRepository seatRepository;

    public List<TripSummaryDTO> searchTrips(Long fromId, Long toId, LocalDate date) {
        List<Trip> trips = tripRepository.searchTrips(fromId, toId, date);

        return trips.stream().map(t -> {
            // Đếm số ghế còn trạng thái AVAILABLE
            long availableSeats = seatRepository.countByTripIdAndStatus(t.getId(), "AVAILABLE");
            return new TripSummaryDTO(
                    t.getId(), t.getBus().getPlateNumber(), t.getBus().getBusType(),
                    t.getDriverName(), t.getDepartureTime(), t.getPrice(), availableSeats);
        }).collect(Collectors.toList());
    }

    public List<SeatDTO> getSeatMap(Long tripId) {
        return seatRepository.findByTripIdOrderBySeatNumberAsc(tripId)
                .stream()
                .map(s -> new SeatDTO(s.getId(), s.getSeatNumber(), s.getStatus()))
                .collect(Collectors.toList());
    }
}