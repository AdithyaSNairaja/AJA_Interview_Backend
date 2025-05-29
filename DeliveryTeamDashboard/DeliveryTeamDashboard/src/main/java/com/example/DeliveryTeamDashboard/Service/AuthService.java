package com.example.DeliveryTeamDashboard.Service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

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

    public User register(String fullName, String empId, String email, String password, String role, String technology, String resourceType) {
        // Validate mandatory fields
        if (fullName == null || fullName.trim().isEmpty()) {
            throw new IllegalArgumentException("Full name cannot be null or empty");
        }
        if (empId == null || empId.trim().isEmpty()) {
            throw new IllegalArgumentException("Employee ID cannot be null or empty");
        }
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
        if (role == null || role.trim().isEmpty()) {
            throw new IllegalArgumentException("Role cannot be null or empty");
        }

        // Validate role
        String normalizedRole = role.trim().toLowerCase();
        String roleEnum;
        switch (normalizedRole) {
            case "employee":
                roleEnum = "ROLE_EMPLOYEE";
                break;
            case "sales-team":
                roleEnum = "ROLE_SALES_TEAM";
                break;
            case "delivery_team":
                roleEnum = "ROLE_DELIVERY_TEAM";
                break;
            default:
                throw new IllegalArgumentException("Invalid role. Must be 'employee', 'sales-team', or 'delivery_team'");
        }

        // Check uniqueness
        if (userRepository.existsByEmail(email.trim())) {
            throw new IllegalArgumentException("Email '" + email + "' is already in use");
        }
        if (employeeRepository.existsByEmpId(empId.trim())) {
            throw new IllegalArgumentException("Employee ID '" + empId + "' is already in use");
        }

        // Validate employee-specific fields
        if (normalizedRole.equals("employee")) {
            if (technology == null || technology.trim().isEmpty()) {
                throw new IllegalArgumentException("Technology is required for employee role");
            }
            if (resourceType == null || resourceType.trim().isEmpty()) {
                throw new IllegalArgumentException("Resource type is required for employee role");
            }
            Set<String> validResourceTypes = new HashSet<>(Arrays.asList("OM", "TCT1", "TCT2"));
            if (!validResourceTypes.contains(resourceType.trim())) {
                throw new IllegalArgumentException("Resource type must be 'OM', 'TCT1', or 'TCT2'");
            }
        }

        // Create User
        User user = new User();
        user.setFullName(fullName.trim());
        user.setEmail(email.trim());
        user.setPassword(passwordEncoder.encode(password.trim()));
        user.setRole(roleEnum);
        User savedUser = userRepository.save(user);

        // Create Employee for employee role
        if (normalizedRole.equals("employee")) {
            Employee employee = new Employee();
            employee.setUser(savedUser);
            employee.setEmpId(empId.trim());
            employee.setTechnology(technology.trim());
            employee.setResourceType(resourceType.trim());
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
