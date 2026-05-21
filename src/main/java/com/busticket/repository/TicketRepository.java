package com.busticket.repository;

import com.busticket.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    
    @Query("SELECT t FROM Ticket t " +
           "JOIN FETCH t.seat s " +
           "JOIN FETCH s.trip tr " +
           "JOIN FETCH tr.bus b " +
           "JOIN FETCH tr.route r " +
           "JOIN FETCH r.fromLocation sl " +
           "JOIN FETCH r.toLocation el " +
           "WHERE t.ticketCode = :ticketCode AND t.customerPhone = :customerPhone")
    List<Ticket> findByTicketCodeAndCustomerPhoneWithDetails(
            @Param("ticketCode") String ticketCode, 
            @Param("customerPhone") String customerPhone);

    @Query("SELECT t FROM Ticket t " +
           "JOIN FETCH t.seat s " +
           "JOIN FETCH s.trip tr " +
           "JOIN FETCH tr.bus b " +
           "JOIN FETCH tr.route r " +
           "JOIN FETCH r.fromLocation sl " +
           "JOIN FETCH r.toLocation el " +
           "WHERE t.customerPhone = :customerPhone ORDER BY t.bookingTime DESC")
    List<Ticket> findByCustomerPhoneWithDetails(@Param("customerPhone") String customerPhone);

    @Query("SELECT t FROM Ticket t " +
           "JOIN FETCH t.seat s " +
           "JOIN FETCH s.trip tr " +
           "JOIN FETCH tr.bus b " +
           "JOIN FETCH tr.route r " +
           "JOIN FETCH r.fromLocation sl " +
           "JOIN FETCH r.toLocation el " +
           "ORDER BY t.bookingTime DESC")
    List<Ticket> findAllWithDetails();
}
