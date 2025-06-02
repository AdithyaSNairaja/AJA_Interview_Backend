package com.example.DeliveryTeamDashboard.Service;

import java.time.LocalDate;
import java.time.LocalTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
	private String senderEmail;

    
    @Autowired
    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }
    public void sendEmail(String to, String subject, String body, boolean isHtml) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(body, isHtml);
        helper.setFrom(senderEmail); // Configure this in application.properties

        mailSender.send(message);
    }

    public void sendMockInterviewNotification(String employeeEmail, String interviewerEmail, 
                                             String employeeName, String interviewerName, 
                                             LocalDate date, LocalTime time) throws MessagingException {
        String subject = "Mock Interview Scheduled - " + date.toString();
        String employeeBody = String.format(
            "<h3>Mock Interview Scheduled</h3>" +
            "<p>Dear %s,</p>" +
            "<p>You have a mock interview scheduled with %s on %s at %s.</p>" +
            "<p>Please prepare accordingly and ensure you are available at the scheduled time.</p>" +
            "<p>Best regards,<br>Delivery Team</p>",
            employeeName, interviewerName, date.toString(), time.toString()
        );
        String interviewerBody = String.format(
            "<h3>Mock Interview Assignment</h3>" +
            "<p>Dear %s,</p>" +
            "<p>You are scheduled to conduct a mock interview with %s on %s at %s.</p>" +
            "<p>Please ensure you are prepared to evaluate the candidate.</p>" +
            "<p>Best regards,<br>Delivery Team</p>",
            interviewerName, employeeName, date.toString(), time.toString()
        );

        // Send email to employee
        sendEmail(employeeEmail, subject, employeeBody, true);
        // Send email to interviewer
        sendEmail(interviewerEmail, subject, interviewerBody, true);
    }
}