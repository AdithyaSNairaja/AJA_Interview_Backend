//package com.example.DeliveryTeamDashboard.Controller;
//
//import java.time.LocalDate;
//import java.time.LocalTime;
//
//import org.springframework.format.annotation.DateTimeFormat;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.security.core.Authentication;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
//import com.example.DeliveryTeamDashboard.Service.EmployeeService;
//
//@RestController
//@RequestMapping("/api/interviews")
//public class InterviewController {
//
//    private final EmployeeService employeeService;
//
//    public InterviewController(EmployeeService employeeService) {
//        this.employeeService = employeeService;
//    }
//
//    @PostMapping("/schedule")
//    @PreAuthorize("hasRole('DELIVERY_TEAM')")
//    public ResponseEntity<?> scheduleInterview(
//            Authentication authentication,
//            @RequestParam String empId,
//            @RequestParam String interviewType,
//            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
//            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime time,
//            @RequestParam(required = false) String client,
//            @RequestParam(required = false) String interviewer,
//            @RequestParam(required = false) Integer level,
//            @RequestParam(required = false) String jdTitle,
//            @RequestParam(required = false) String meetingLink) {
//        if (authentication == null || !authentication.isAuthenticated()) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
//        }
//
//        try {
//            Object interview = employeeService.scheduleInterview(empId, interviewType, date, time, client, interviewer, level, jdTitle, meetingLink);
//            return ResponseEntity.status(HttpStatus.CREATED).body(interview);
//        } catch (IllegalArgumentException e) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
//        }
//    }
//}
