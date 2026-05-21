package com.busticket;

import com.busticket.entity.*;
import com.busticket.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    @Autowired private PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) throws Exception {

        // 1. KHỞI TẠO TÀI KHOẢN ADMIN & STAFF
        if (userRepository.findByUsername("admin").isEmpty()) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPasswordHash(passwordEncoder.encode("admin123"));
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
        
        if (userRepository.findByUsername("staff").isEmpty()) {
            User staff = new User();
            staff.setUsername("staff");
            staff.setPasswordHash(passwordEncoder.encode("staff123"));
            staff.setRole("STAFF");

            UserProfile profile = new UserProfile();
            profile.setUser(staff);
            profile.setFullName("Nhân Viên Bán Vé");
            profile.setPhoneNumber("0888888888");
            profile.setEmail("staff@busticket.com");
            staff.setProfile(profile);

            userRepository.save(staff);
            System.out.println("Đã khởi tạo tài khoản Staff (staff / staff123)");
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

        // 4. KHỞI TẠO XE MẪU (BUSES) - Không còn driverName ở Bus
        if (busRepository.count() == 0) {
            Bus bus1 = new Bus();
            bus1.setPlateNumber("29B-123.45");
            bus1.setBusType("Giường nằm 40 chỗ");
            bus1.setTotalSeats(40);

            Bus bus2 = new Bus();
            bus2.setPlateNumber("51C-987.65");
            bus2.setBusType("Limousine 11 chỗ");
            bus2.setTotalSeats(11);

            busRepository.saveAll(Arrays.asList(bus1, bus2));
            System.out.println("Đã khởi tạo danh sách Xe mẫu (Buses)");
        }

        // 5. KHỞI TẠO CHUYẾN XE MẪU & TẠO SƠ ĐỒ GHẾ
        if (tripRepository.count() == 0 && busRepository.count() > 0 && routeRepository.count() > 0) {
            Route routeHN_HP = routeRepository.findAll().get(0);
            Bus bus1 = busRepository.findAll().get(0);

            // Chuyến 1: Đủ xa để khách hủy (sau 2 ngày)
            Trip trip1 = new Trip();
            trip1.setRoute(routeHN_HP);
            trip1.setBus(bus1);
            trip1.setDepartureTime(LocalDateTime.now().plusDays(2));
            trip1.setPrice(150000.0);
            trip1.setDriverName("Nguyễn Văn A");
            tripRepository.save(trip1);

            for (int i = 1; i <= 40; i++) {
                Seat seat = new Seat();
                seat.setTrip(trip1);
                seat.setSeatNumber(i < 10 ? "A0" + i : "A" + i);
                seat.setStatus("AVAILABLE");
                seatRepository.save(seat);
            }
            
            // Chuyến 2: Khởi hành quá sát giờ (sau 5 tiếng) - Để test Khách Hủy bị chặn
            Trip trip2 = new Trip();
            trip2.setRoute(routeHN_HP);
            trip2.setBus(bus1);
            trip2.setDepartureTime(LocalDateTime.now().plusHours(5));
            trip2.setPrice(150000.0);
            trip2.setDriverName("Trần Văn B");
            tripRepository.save(trip2);

            for (int i = 1; i <= 40; i++) {
                Seat seat = new Seat();
                seat.setTrip(trip2);
                seat.setSeatNumber(i < 10 ? "B0" + i : "B" + i);
                seat.setStatus("AVAILABLE");
                seatRepository.save(seat);
            }

            System.out.println("Đã khởi tạo 2 Chuyến xe mẫu (Trip) và 80 Ghế (Seats)");
        }

        System.out.println("Seed Data Complete");
    }
}
