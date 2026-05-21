package com.busticket.repository;

import com.busticket.entity.Trip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TripRepository extends JpaRepository<Trip, Long> {

    // Tìm chuyến xe theo Điểm đi, Điểm đến và Ngày đi (Sử dụng hàm DATE của MySQL)
    @Query("SELECT t FROM Trip t WHERE t.route.fromLocation.id = :fromId " +
            "AND t.route.toLocation.id = :toId AND DATE(t.departureTime) = :depDate")
    List<Trip> searchTrips(@Param("fromId") Long fromId,
                           @Param("toId") Long toId,
                           @Param("depDate") LocalDate depDate);

    boolean existsByBusId(Long busId);
}