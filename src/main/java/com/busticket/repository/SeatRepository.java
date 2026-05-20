package com.busticket.repository;

import com.busticket.entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Long> {
    List<Seat> findByTripIdOrderBySeatNumberAsc(Long tripId);

    long countByTripIdAndStatus(Long tripId, String status);
}