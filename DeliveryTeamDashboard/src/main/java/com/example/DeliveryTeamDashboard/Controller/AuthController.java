package com.example.DeliveryTeamDashboard.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.DeliveryTeamDashboard.Entity.User;
import com.example.DeliveryTeamDashboard.Service.AuthService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<User> register(
            @RequestParam String name,
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam String role) {
        User user = authService.register(name, email, password, role);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(
            @RequestParam String email,
            @RequestParam String password) {
        String token = authService.login(email, password);
        return ResponseEntity.ok(token);
    }
}