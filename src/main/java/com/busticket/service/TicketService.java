package com.busticket.service;

import com.busticket.dto.BookingResponseDTO;
import com.busticket.dto.TicketRequestDTO;
import com.busticket.dto.TicketResponseDTO;
import com.busticket.entity.Seat;
import com.busticket.entity.Ticket;
import com.busticket.repository.SeatRepository;
import com.busticket.repository.TicketRepository;
import com.busticket.repository.UserRepository;
import com.busticket.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Value;
import vn.payos.PayOS;
import vn.payos.model.v2.paymentRequests.CreatePaymentLinkRequest;
import vn.payos.model.v2.paymentRequests.CreatePaymentLinkResponse;
import vn.payos.model.v2.paymentRequests.PaymentLinkItem;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TicketService {

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private PayOS payOS;

    @Value("${PAYOS_WEBHOOK_DOMAIN}")
    private String domain;

    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional(rollbackFor = Exception.class)
    public BookingResponseDTO bookTickets(TicketRequestDTO request) {
        String custName = request.getCustomerName();
        String custPhone = request.getCustomerPhone();

        // 1. Kiểm tra Auth (Bắt buộc dùng JWT)
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        if (username != null && !username.equals("anonymousUser")) {
            User user = userRepository.findByUsername(username).orElse(null);
            if (user != null && user.getProfile() != null) {
                custName = user.getProfile().getFullName();
                custPhone = user.getProfile().getPhoneNumber();
            }
        }

        if (custName == null || custName.trim().isEmpty() || custPhone == null || custPhone.trim().isEmpty()) {
            throw new RuntimeException("Bạn phải đăng nhập để thực hiện đặt vé.");
        }

        List<Ticket> bookedTickets = new ArrayList<>();
        
        // Sinh 1 orderCode chung cho tất cả các vé trong lần đặt này
        long orderCode = Long.parseLong(String.valueOf(System.currentTimeMillis()).substring(2, 11) + (int)(Math.random() * 1000));
        int totalAmount = 0;
        List<PaymentLinkItem> items = new ArrayList<>();

        for (Long seatId : request.getSeatIds()) {
            Seat seat = seatRepository.findById(seatId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy ghế: " + seatId));

            // Kiểm tra trạng thái ghế
            if (!"AVAILABLE".equals(seat.getStatus())) {
                throw new RuntimeException("Ghế " + seat.getSeatNumber() + " đã được đặt hoặc đang chờ xử lý.");
            }

            // Kiểm tra thời gian khởi hành (không cho đặt nếu cách giờ khởi hành <= 2 tiếng)
            if (seat.getTrip().getDepartureTime().isBefore(LocalDateTime.now().plusHours(2))) {
                throw new RuntimeException("Không thể đặt vé vì chuyến xe sẽ khởi hành trong vòng 2 giờ tới.");
            }

            // Đổi trạng thái ghế thành PENDING
            seat.setStatus("PENDING");
            seatRepository.save(seat);

            // Tạo vé
            Ticket ticket = new Ticket();
            ticket.setTicketCode(generateTicketCode());
            ticket.setOrderCode(orderCode);
            ticket.setSeat(seat);
            ticket.setCustomerName(custName);
            ticket.setCustomerPhone(custPhone);
            ticket.setStatus("PENDING");
            ticket.setTotalPrice(seat.getTrip().getPrice());
            ticket.setBookingTime(LocalDateTime.now());

            bookedTickets.add(ticketRepository.save(ticket));
            
            totalAmount += ticket.getTotalPrice().intValue();
            items.add(PaymentLinkItem.builder()
                    .name("Vé xe tuyến " + seat.getTrip().getRoute().getFromLocation().getName() + " - " + seat.getTrip().getRoute().getToLocation().getName() + " Ghế " + seat.getSeatNumber())
                    .quantity(1)
                    .price(ticket.getTotalPrice().longValue())
                    .build());
        }

        // Chuyển sang DTO trả về
        List<TicketResponseDTO> ticketResponseDTOs = bookedTickets.stream().map(this::mapToSimpleResponse).collect(Collectors.toList());
        
        String checkoutUrl = "";
        try {
            CreatePaymentLinkRequest paymentData = CreatePaymentLinkRequest.builder()
                    .orderCode(orderCode)
                    .amount((long) totalAmount)
                    .description("Thanh toan ve xe")
                    .returnUrl(domain + "/success.html")
                    .cancelUrl(domain + "/cancel.html")
                    .items(items)
                    .build();

            CreatePaymentLinkResponse data = payOS.paymentRequests().create(paymentData);
            checkoutUrl = data.getCheckoutUrl();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Lỗi khi tạo link thanh toán PayOS: " + e.getMessage());
        }

        return new BookingResponseDTO(ticketResponseDTOs, checkoutUrl);
    }

    public List<TicketResponseDTO> searchTicket(String ticketCode, String customerPhone) {
        List<Ticket> tickets = ticketRepository.findByTicketCodeAndCustomerPhoneWithDetails(ticketCode, customerPhone);
        
        if (tickets.isEmpty()) {
            throw new RuntimeException("Không tìm thấy vé nào khớp với thông tin cung cấp.");
        }

        return tickets.stream().map(this::mapToDetailedResponse).collect(Collectors.toList());
    }
    
    public List<TicketResponseDTO> getMyTickets() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        String phone = user.getProfile().getPhoneNumber();
        
        return ticketRepository.findByCustomerPhoneWithDetails(phone)
                .stream()
                .map(this::mapToDetailedResponse)
                .collect(Collectors.toList());
    }
    
    public List<TicketResponseDTO> getAllTickets() {
        return ticketRepository.findAllWithDetails().stream().map(this::mapToDetailedResponse).collect(Collectors.toList());
    }

    @Transactional(rollbackFor = Exception.class)
    public void approveTicket(Long ticketId) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy vé: " + ticketId));
        
        if (!"PENDING".equals(ticket.getStatus())) {
            throw new RuntimeException("Chỉ có thể duyệt vé đang ở trạng thái PENDING.");
        }
        
        ticket.setStatus("PAID");
        ticketRepository.save(ticket);
        
        Seat seat = ticket.getSeat();
        if (seat != null) {
            seat.setStatus("BOOKED");
            seatRepository.save(seat);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void completePayment(Long orderCode) {
        List<Ticket> tickets = ticketRepository.findByOrderCode(orderCode);
        for (Ticket ticket : tickets) {
            if ("PENDING".equals(ticket.getStatus())) {
                ticket.setStatus("PAID");
                ticketRepository.save(ticket);
                
                Seat seat = ticket.getSeat();
                if (seat != null) {
                    seat.setStatus("BOOKED");
                    seatRepository.save(seat);
                }
            }
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void cancelTicketByStaff(Long ticketId) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy vé: " + ticketId));
        
        if (!"PENDING".equals(ticket.getStatus())) {
            throw new RuntimeException("Staff chỉ hủy những vé chưa thanh toán (PENDING).");
        }
        
        cancelTicketLogic(ticket);
    }
    
    @Transactional(rollbackFor = Exception.class)
    public void cancelTicketByCustomer(String ticketCode, String customerPhone) {
        List<Ticket> tickets = ticketRepository.findByTicketCodeAndCustomerPhoneWithDetails(ticketCode, customerPhone);
        if (tickets.isEmpty()) {
            throw new RuntimeException("Không tìm thấy vé hợp lệ.");
        }
        
        Ticket ticket = tickets.get(0); // Vì mỗi ticketCode là duy nhất
        
        // Ràng buộc 12h
        LocalDateTime departureTime = ticket.getSeat().getTrip().getDepartureTime();
        if (LocalDateTime.now().isAfter(departureTime.minusHours(12))) {
            throw new RuntimeException("Quá hạn hủy vé! Bạn chỉ được phép hủy trước giờ khởi hành ít nhất 12 tiếng.");
        }
        
        if ("CANCELLED".equals(ticket.getStatus())) {
            throw new RuntimeException("Vé này đã bị hủy từ trước.");
        }
        
        cancelTicketLogic(ticket);
    }
    
    private void cancelTicketLogic(Ticket ticket) {
        // Hủy vé
        ticket.setStatus("CANCELLED");
        ticketRepository.save(ticket);
        
        // Giải phóng ghế
        Seat seat = ticket.getSeat();
        seat.setStatus("AVAILABLE");
        seatRepository.save(seat);
    }

    private String generateTicketCode() {
        // Sinh mã vé ngẫu nhiên 8 ký tự
        return "T" + UUID.randomUUID().toString().substring(0, 7).toUpperCase();
    }

    private TicketResponseDTO mapToSimpleResponse(Ticket ticket) {
        TicketResponseDTO dto = new TicketResponseDTO();
        dto.setId(ticket.getId());
        dto.setTicketCode(ticket.getTicketCode());
        dto.setCustomerName(ticket.getCustomerName());
        dto.setCustomerPhone(ticket.getCustomerPhone());
        dto.setTotalPrice(ticket.getTotalPrice());
        dto.setStatus(ticket.getStatus());
        dto.setBookingTime(ticket.getBookingTime());
        // Lấy thông tin cơ bản
        dto.setSeatNumber(ticket.getSeat().getSeatNumber());
        dto.setTripId(ticket.getSeat().getTrip().getId());
        return dto;
    }

    private TicketResponseDTO mapToDetailedResponse(Ticket ticket) {
        TicketResponseDTO dto = mapToSimpleResponse(ticket);
        
        // Thông tin JOIN thêm
        dto.setBusPlate(ticket.getSeat().getTrip().getBus().getPlateNumber());
        dto.setRouteStart(ticket.getSeat().getTrip().getRoute().getFromLocation().getName());
        dto.setRouteEnd(ticket.getSeat().getTrip().getRoute().getToLocation().getName());
        dto.setDepartureTime(ticket.getSeat().getTrip().getDepartureTime());
        dto.setDriverName(ticket.getSeat().getTrip().getDriverName());
        
        return dto;
    }
}
