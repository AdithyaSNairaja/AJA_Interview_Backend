package com.example.DeliveryTeamDashboard.Controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.DeliveryTeamDashboard.Entity.User;
import com.example.DeliveryTeamDashboard.Service.AuthService;
import com.example.DeliveryTeamDashboard.config.JwtUtil;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

	 private final AuthService authService;
	    private final JwtUtil jwtUtil;

	    public AuthController(AuthService authService, JwtUtil jwtUtil) {
	        this.authService = authService;
	        this.jwtUtil = jwtUtil;
	    }
    
//    @PostMapping("/register")
//    public ResponseEntity<?> register(
//            @RequestParam String fullName,
//            @RequestParam String empId,
//            @RequestParam String email,
//            @RequestParam String password,
//            @RequestParam String role,
//            @RequestParam(required = false) String technology,
//            @RequestParam(required = false) String resourceType) {
//        try {
//            User user = authService.register(fullName, empId, email, password, role, technology, resourceType);
//            return ResponseEntity.status(HttpStatus.CREATED).body(user);
//        } catch (IllegalArgumentException e) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
//        }
//    }
//
////    @PostMapping("/login")
////    public ResponseEntity<String> login(
////            @RequestParam String email,
////            @RequestParam String password) {
////        String token = authService.login(email, password);
////        return ResponseEntity.ok(token);
////    }
//    
//    @PostMapping("/login")
//    public ResponseEntity<Map<String, String>> login(
//            @RequestParam String email,
//            @RequestParam String password) {
//        String token = authService.login(email, password);
//        User user = authService.getUserByEmail(email); // Assume AuthService has this method
//        Map<String, String> response = new HashMap<>();
//        response.put("token", token);
//        response.put("role", user.getRole());
//        return ResponseEntity.ok(response);
//    }
	    @PostMapping("/register")
	    public ResponseEntity<?> register(
	            @RequestParam String fullName,
	            @RequestParam String empId,
	            @RequestParam String email,
	            @RequestParam String password,
	            @RequestParam String role,
	            @RequestParam(required = false) String technology,
	            @RequestParam(required = false) String resourceType) {
	        try {
	            User user = authService.register(fullName, empId, email, password, role, technology, resourceType);
	            String token = jwtUtil.generateToken(user.getEmail(), user.getRole());
	            return ResponseEntity.status(HttpStatus.CREATED).body(new AuthResponse(token, user.getRole()));
	        } catch (IllegalArgumentException e) {
	            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
	        }
	    }

	    @PostMapping("/login")
	    public ResponseEntity<?> login(
	            @RequestParam String email,
	            @RequestParam String password) {
	        try {
	            User user = authService.login(email, password);
	            String token = jwtUtil.generateToken(user.getEmail(), user.getRole());
	            return ResponseEntity.status(HttpStatus.OK).body(new AuthResponse(token, user.getRole()));
	        } catch (IllegalArgumentException e) {
	            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
	        }
	    }

	    private static class AuthResponse {
	        private final String token;
	        private final String role;

	        public AuthResponse(String token, String role) {
	            this.token = token;
	            this.role = role;
	        }

	        public String getToken() {
	            return token;
	        }

	        public String getRole() {
	            return role;
	        }
	    }
}