package com.example.DeliveryTeamDashboard.Service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.DeliveryTeamDashboard.Entity.Employee;
import com.example.DeliveryTeamDashboard.Entity.User;
import com.example.DeliveryTeamDashboard.Repository.EmployeeRepository;
import com.example.DeliveryTeamDashboard.Repository.UserRepository;
import com.example.DeliveryTeamDashboard.config.JwtUtil;

@Service
public class AuthService {

//    private final UserRepository userRepository;
//    private final PasswordEncoder passwordEncoder;
//    private final JwtUtil jwtUtil;
//    private final AuthenticationManager authenticationManager;
//
//    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder,
//                       JwtUtil jwtUtil, AuthenticationManager authenticationManager) {
//        this.userRepository = userRepository;
//        this.passwordEncoder = passwordEncoder;
//        this.jwtUtil = jwtUtil;
//        this.authenticationManager = authenticationManager;
//    }
//
//    public User register(String name, String email, String password, String role) {
//        if (userRepository.findByEmail(email).isPresent()) {
//            throw new RuntimeException("Email already exists");
//        }
//
//        User user = new User();
//        user.setName(name);
//        user.setEmail(email);
//        user.setPassword(passwordEncoder.encode(password));
//        user.setRole(role);
//        return userRepository.save(user);
//    }
//
//    public String login(String email, String password) {
//        try {
//            Authentication authentication = authenticationManager.authenticate(
//                    new UsernamePasswordAuthenticationToken(email, password)
//            );
//            User user = userRepository.findByEmail(email)
//                    .orElseThrow(() -> new RuntimeException("User not found"));
//            return jwtUtil.generateToken(user.getEmail(), user.getRole());
//        } catch (AuthenticationException e) {
//            throw new RuntimeException("Invalid credentials");
//        }
//    }
	
	
	private final UserRepository userRepository;
    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    public AuthService(UserRepository userRepository, EmployeeRepository employeeRepository,
                       PasswordEncoder passwordEncoder, JwtUtil jwtUtil,
                       AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.employeeRepository = employeeRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
    }

    public User register(String name, String email, String password, String role) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("Email already exists: " + email);
        }

        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(role.toLowerCase());
        User savedUser = userRepository.save(user);

        if ("employee".equalsIgnoreCase(role)) {
            Employee employee = new Employee();
            employee.setUser(savedUser);
            employee.setTechnology("Unknown");
            employee.setResourceType("Unknown");
            employee.setLevel("Unknown");
            employee.setStatus("Active");
            employeeRepository.save(employee);
        }

        return savedUser;
    }

    public String login(String email, String password) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password)
            );
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new IllegalArgumentException("User not found: " + email));
            return jwtUtil.generateToken(user.getEmail(), user.getRole());
        } catch (AuthenticationException e) {
            throw new IllegalArgumentException("Invalid email or password");
        }
    }
}
