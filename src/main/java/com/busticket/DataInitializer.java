package com.busticket;

import com.busticket.entity.*;
import com.busticket.repository.*;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired private UserRepository userRepository;
    @Autowired private LocationRepository locationRepository;
    @Autowired private RouteRepository routeRepository;
    @Autowired private BusRepository busRepository;
    @Autowired private TripRepository tripRepository;
    @Autowired private SeatRepository seatRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {

        // 1. KHỞI TẠO TÀI KHOẢN ADMIN
        if (userRepository.findByUsername("admin").isEmpty()) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPasswordHash(BCrypt.hashpw("admin123", BCrypt.gensalt(12)));
            admin.setRole("ADMIN");

            UserProfile profile = new UserProfile();
            profile.setUser(admin);
            profile.setFullName("Quản Trị Viên Hệ Thống");
            profile.setPhoneNumber("0999999999");
            profile.setEmail("admin@busticket.com");
            admin.setProfile(profile);

            userRepository.save(admin);
            System.out.println("Đã khởi tạo tài khoản Admin (admin / admin123)");
        }

        // 2. KHỞI TẠO TỈNH THÀNH (LOCATIONS)
        Location hn = null, hp = null, dn = null, hcm = null, dl = null, nt = null;
        if (locationRepository.count() == 0) {
            hn = new Location(); hn.setName("Hà Nội");
            hp = new Location(); hp.setName("Hải Phòng");
            dn = new Location(); dn.setName("Đà Nẵng");
            hcm = new Location(); hcm.setName("Hồ Chí Minh");
            dl = new Location(); dl.setName("Đà Lạt");
            nt = new Location(); nt.setName("Nha Trang");

            locationRepository.saveAll(Arrays.asList(hn, hp, dn, hcm, dl, nt));
            System.out.println("Đã khởi tạo danh sách Tỉnh thành (Locations)");
        }

        // 3. KHỞI TẠO TUYẾN ĐƯỜNG (ROUTES)
        if (routeRepository.count() == 0 && locationRepository.count() > 0) {
            // Lấy lại dữ liệu nếu đã tồn tại để tránh lỗi null
            if (hn == null) {
                var locs = locationRepository.findAll();
                hn = locs.stream().filter(l -> l.getName().equals("Hà Nội")).findFirst().orElse(null);
                hp = locs.stream().filter(l -> l.getName().equals("Hải Phòng")).findFirst().orElse(null);
                dn = locs.stream().filter(l -> l.getName().equals("Đà Nẵng")).findFirst().orElse(null);
                hcm = locs.stream().filter(l -> l.getName().equals("Hồ Chí Minh")).findFirst().orElse(null);
                dl = locs.stream().filter(l -> l.getName().equals("Đà Lạt")).findFirst().orElse(null);
                nt = locs.stream().filter(l -> l.getName().equals("Nha Trang")).findFirst().orElse(null);
            }

            Route r1 = new Route(); r1.setFromLocation(hn); r1.setToLocation(hp); r1.setDistanceKm(120.0);
            Route r2 = new Route(); r2.setFromLocation(hn); r2.setToLocation(dn); r2.setDistanceKm(766.0);
            Route r3 = new Route(); r3.setFromLocation(hcm); r3.setToLocation(dl); r3.setDistanceKm(300.0);
            Route r4 = new Route(); r4.setFromLocation(hcm); r4.setToLocation(nt); r4.setDistanceKm(430.0);

            routeRepository.saveAll(Arrays.asList(r1, r2, r3, r4));
            System.out.println("Đã khởi tạo danh sách Tuyến đường (Routes)");
        }

        // 4. KHỞI TẠO XE MẪU (BUSES) CHO ADMIN
        if (busRepository.count() == 0) {
            Bus bus1 = new Bus();
            bus1.setPlateNumber("29B-123.45");
            bus1.setBusType("Giường nằm 40 chỗ");
            bus1.setTotalSeats(40);
            bus1.setDriverName("Nguyễn Văn A");

            Bus bus2 = new Bus();
            bus2.setPlateNumber("51C-987.65");
            bus2.setBusType("Limousine 11 chỗ");
            bus2.setTotalSeats(11);
            bus2.setDriverName("Trần Văn B");

            busRepository.saveAll(Arrays.asList(bus1, bus2));
            System.out.println("Đã khởi tạo danh sách Xe mẫu (Buses)");
        }

        // 5. KHỞI TẠO 1 CHUYẾN XE MẪU & TẠO SƠ ĐỒ GHẾ
        if (tripRepository.count() == 0 && busRepository.count() > 0 && routeRepository.count() > 0) {
            // Lấy tuyến đường HN -> Hải Phòng và Xe 29B-123.45
            Route routeHN_HP = routeRepository.findAll().get(0);
            Bus bus1 = busRepository.findAll().get(0);

            Trip trip = new Trip();
            trip.setRoute(routeHN_HP);
            trip.setBus(bus1);
            trip.setDepartureTime(LocalDateTime.now().plusDays(2)); // Chạy vào 2 ngày sau
            trip.setPrice(150000.0);
            tripRepository.save(trip);

            // Sinh tự động 40 ghế cho chuyến xe này
            for (int i = 1; i <= 40; i++) {
                Seat seat = new Seat();
                seat.setTrip(trip);
                seat.setSeatNumber(i < 10 ? "A0" + i : "A" + i);

                // Giả lập: Ghế A02 đang bị người khác đặt chờ thanh toán, A05 đã bán
                if (i == 2) seat.setStatus("PENDING");
                else if (i == 5) seat.setStatus("BOOKED");
                else seat.setStatus("AVAILABLE");

                seatRepository.save(seat);
            }
            System.out.println("Đã khởi tạo 1 Chuyến xe mẫu (Trip) và 40 Ghế (Seats)");
        }

        System.out.println("Seed Data Complete");
    }
}