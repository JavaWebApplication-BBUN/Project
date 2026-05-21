package com.busticket.repository;

import com.busticket.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import org.springframework.data.domain.Pageable;
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

    List<Ticket> findByOrderCode(Long orderCode);

    @Query("SELECT SUM(t.totalPrice) FROM Ticket t WHERE t.status = 'PAID'")
    Double getTotalRevenue();

    @Query("SELECT COUNT(t) FROM Ticket t WHERE t.status = 'PAID'")
    Long countPaidTickets();

    @Query("SELECT CONCAT(fl.name, ' - ', tl.name), SUM(t.totalPrice) " +
           "FROM Ticket t " +
           "JOIN t.seat s " +
           "JOIN s.trip tr " +
           "JOIN tr.route r " +
           "JOIN r.fromLocation fl " +
           "JOIN r.toLocation tl " +
           "WHERE t.status = 'PAID' " +
           "GROUP BY fl.name, tl.name")
    List<Object[]> getRevenueByRoute();

    @Query("SELECT YEAR(t.bookingTime), MONTH(t.bookingTime), SUM(t.totalPrice) " +
           "FROM Ticket t " +
           "WHERE t.status = 'PAID' " +
           "GROUP BY YEAR(t.bookingTime), MONTH(t.bookingTime) " +
           "ORDER BY YEAR(t.bookingTime) ASC, MONTH(t.bookingTime) ASC")
    List<Object[]> getRevenueByMonth();

    @Query("SELECT tr.id, CONCAT(fl.name, ' - ', tl.name), tr.departureTime, COUNT(t.id) " +
           "FROM Ticket t " +
           "JOIN t.seat s " +
           "JOIN s.trip tr " +
           "JOIN tr.route r " +
           "JOIN r.fromLocation fl " +
           "JOIN r.toLocation tl " +
           "WHERE t.status = 'PAID' " +
           "GROUP BY tr.id, fl.name, tl.name, tr.departureTime " +
           "ORDER BY COUNT(t.id) DESC")
    List<Object[]> getTopTrips(Pageable pageable);
}
