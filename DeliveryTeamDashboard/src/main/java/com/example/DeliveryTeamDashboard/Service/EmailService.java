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
    
    public void sendClientInterviewNotification(String employeeEmail, String employeeName, 
            String client, LocalDate date, LocalTime time, 
            Integer level, String jobDescriptionTitle, 
            String meetingLink) throws MessagingException {
			String subject = "Client Interview Scheduled - " + date.toString();
			String employeeBody = String.format(
			"<h3>Client Interview Scheduled</h3>" +
			"<p>Dear %s,</p>" +
			"<p>You have a client interview scheduled with %s on %s at %s.</p>" +
			"<p><strong>Details:</strong></p>" +
			"<ul>" +
			"<li><strong>Level:</strong> %d</li>" +
			"<li><strong>Job Description:</strong> %s</li>" +
			"<li><strong>Meeting Link:</strong> <a href='%s'>Join Meeting</a></li>" +
			"</ul>" +
			"<p>Please prepare thoroughly and ensure you are available at the scheduled time.</p>" +
			"<p>Best regards,<br>Sales Team</p>",
			employeeName, client, date.toString(), time.toString(), level, jobDescriptionTitle, meetingLink
			);
			sendEmail(employeeEmail, subject, employeeBody, true);
			}
    
    public void sendMockInterviewFeedbackNotification(String employeeEmail, String employeeName,
            LocalDate date, LocalTime time, String technicalFeedback,
            String communicationFeedback, Integer technicalScore,
            Integer communicationScore) throws MessagingException {
			String subject = "Mock Interview Feedback - " + date.toString();
			String employeeBody = String.format(
			"<h3>Mock Interview Feedback</h3>" +
			"<p>Dear %s,</p>" +
			"<p>Your mock interview on %s at %s has been completed. Below is the feedback:</p>" +
			"<p><strong>Technical Feedback:</strong> %s</p>" +
			"<p><strong>Technical Score:</strong> %d</p>" +
			"<p><strong>Communication Feedback:</strong> %s</p>" +
			"<p><strong>Communication Score:</strong> %d</p>" +
			"<p>Please review the feedback and reach out if you have any questions.</p>" +
			"<p>Best regards,<br>Delivery Team</p>",
			employeeName, date.toString(), time.toString(),
			technicalFeedback, technicalScore, communicationFeedback, communicationScore
			);
			
			sendEmail(employeeEmail, subject, employeeBody, true);
			}
    public void sendClientInterviewFeedbackNotification(String employeeEmail, String employeeName,
            String client, LocalDate date, LocalTime time,
            String result, String feedback,
            Integer technicalScore, Integer communicationScore,
            Integer level) throws MessagingException {
			String subject = "Client Interview Feedback - " + date.toString();
			String employeeBody = String.format(
			"<h3>Client Interview Feedback</h3>" +
			"<p>Dear %s,</p>" +
			"<p>Your client interview with %s on %s at %s has been completed. Below is the feedback:</p>" +
			"<p><strong>Result:</strong> %s</p>" +
			"<p><strong>Feedback:</strong> %s</p>" +
			"<p><strong>Technical Score:</strong> %d</p>" +
			"<p><strong>Communication Score:</strong> %d</p>" +
			"<p><strong>Level:</strong> %d</p>" +
			"<p>Please review the feedback and reach out if you have any questions.</p>" +
			"<p>Best regards,<br>Sales Team</p>",
			employeeName, client, date.toString(), time.toString(),
			result, feedback, technicalScore, communicationScore, level
			);
			
			sendEmail(employeeEmail, subject, employeeBody, true);
			}
}