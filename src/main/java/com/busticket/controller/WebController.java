package com.busticket.controller;

import com.busticket.repository.LocationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController {
    @Autowired
    private LocationRepository locationRepository;

    @GetMapping("/")
    public String homePage(Model model) {
        // Truyền danh sách tỉnh thành xuống file HTML
        model.addAttribute("locations", locationRepository.findAll());
        return "search-trip";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    @GetMapping("/admin/buses")
    public String adminBusesPage() {
        return "admin-buses";
    }
}