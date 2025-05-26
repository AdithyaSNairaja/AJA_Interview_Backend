package com.example.DeliveryTeamDashboard.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.DeliveryTeamDashboard.Entity.ClientInterview;
import com.example.DeliveryTeamDashboard.Entity.Employee;
import com.example.DeliveryTeamDashboard.Entity.InterviewQuestion;
import com.example.DeliveryTeamDashboard.Entity.JobDescription;
import com.example.DeliveryTeamDashboard.Entity.MockInterview;
import com.example.DeliveryTeamDashboard.Entity.Resume;
import com.example.DeliveryTeamDashboard.Repository.ClientInterviewRepository;
import com.example.DeliveryTeamDashboard.Repository.EmployeeRepository;
import com.example.DeliveryTeamDashboard.Repository.InterviewQuestionRepository;
import com.example.DeliveryTeamDashboard.Repository.JobDescriptionRepository;
import com.example.DeliveryTeamDashboard.Repository.MockInterviewRepository;
import com.example.DeliveryTeamDashboard.Repository.ResumeRepository;

@Service
public class EmployeeService {
	
	
//    private final EmployeeRepository employeeRepository;
//    private final JobDescriptionRepository jobDescriptionRepository;
//    private final ResumeRepository resumeRepository;
//    private final MockInterviewRepository mockInterviewRepository;
//    private final ClientInterviewRepository clientInterviewRepository;
//    private final InterviewQuestionRepository interviewQuestionRepository;
//    private final S3Service s3Service;
//
//    /**
//     * Constructor for dependency injection.
//     */
//    public EmployeeService(EmployeeRepository employeeRepository,
//                           JobDescriptionRepository jobDescriptionRepository,
//                           ResumeRepository resumeRepository,
//                           MockInterviewRepository mockInterviewRepository,
//                           ClientInterviewRepository clientInterviewRepository,
//                           InterviewQuestionRepository interviewQuestionRepository,
//                           S3Service s3Service) {
//        this.employeeRepository = employeeRepository;
//        this.jobDescriptionRepository = jobDescriptionRepository;
//        this.resumeRepository = resumeRepository;
//        this.mockInterviewRepository = mockInterviewRepository;
//        this.clientInterviewRepository = clientInterviewRepository;
//        this.interviewQuestionRepository = interviewQuestionRepository;
//        this.s3Service = s3Service;
//    }
//
//    /**
//     * Retrieves job descriptions based on search criteria, technology, or resource type.
//     * @param search Optional search term for title or client.
//     * @param technology Optional technology filter (e.g., "Java"). Use "all" for no filter.
//     * @param resourceType Optional resource type filter (e.g., "Full-Time"). Use "all" for no filter.
//     * @return List of matching job descriptions.
//     */
//    public List<JobDescription> getJobDescriptions(String search, String technology, String resourceType) {
//        if (search != null && !search.trim().isEmpty()) {
//            return jobDescriptionRepository.findByTitleContainingIgnoreCaseOrClientContainingIgnoreCase(search.trim(), search.trim());
//        }
//        if (technology != null && !"all".equalsIgnoreCase(technology.trim())) {
//            return jobDescriptionRepository.findByTechnology(technology.trim());
//        }
//        if (resourceType != null && !"all".equalsIgnoreCase(resourceType.trim())) {
//            return jobDescriptionRepository.findByResourceType(resourceType.trim());
//        }
//        return jobDescriptionRepository.findAll();
//    }
//
//    /**
//     * Uploads a resume for an employee and job description, storing the file in S3.
//     * @param employeeId ID of the employee.
//     * @param jdId ID of the job description.
//     * @param file Resume file (e.g., PDF).
//     * @return Saved Resume entity.
//     * @throws IOException If file upload fails.
//     * @throws IllegalArgumentException If inputs are invalid.
//     */
//    public Resume uploadResume(Long employeeId, Long jdId, MultipartFile file) throws IOException {
//        if (employeeId == null) {
//            throw new IllegalArgumentException("Employee ID cannot be null");
//        }
//        if (jdId == null) {
//            throw new IllegalArgumentException("Job Description ID cannot be null");
//        }
//        if (file == null || file.isEmpty()) {
//            throw new IllegalArgumentException("Resume file cannot be null or empty");
//        }
//
//        Employee employee = employeeRepository.findById(employeeId)
//                .orElseThrow(() -> new IllegalArgumentException("Employee not found with ID: " + employeeId));
//        JobDescription jd = jobDescriptionRepository.findById(jdId)
//                .orElseThrow(() -> new IllegalArgumentException("Job Description not found with ID: " + jdId));
//
//        String s3Key = s3Service.uploadFile(file, "resumes");
//        Resume resume = new Resume();
//        resume.setEmployee(employee);
//        resume.setJobDescription(jd);
//        resume.setS3Key(s3Key);
//        resume.setStatus("pending");
//        return resumeRepository.save(resume);
//    }
//
//    /**
//     * Downloads a resume file from S3.
//     * @param resumeId ID of the resume.
//     * @return File content as byte array.
//     * @throws IOException If file download fails.
//     * @throws IllegalArgumentException If resume not found.
//     */
//    public byte[] downloadResume(Long resumeId) throws IOException {
//        if (resumeId == null) {
//            throw new IllegalArgumentException("Resume ID cannot be null");
//        }
//        Resume resume = resumeRepository.findById(resumeId)
//                .orElseThrow(() -> new IllegalArgumentException("Resume not found with ID: " + resumeId));
//        return s3Service.downloadFile(resume.getS3Key());
//    }
//
//    /**
//     * Deletes a resume and its S3 file.
//     * @param resumeId ID of the resume.
//     * @throws IllegalArgumentException If resume not found.
//     */
//    public void deleteResume(Long resumeId) {
//        if (resumeId == null) {
//            throw new IllegalArgumentException("Resume ID cannot be null");
//        }
//        Resume resume = resumeRepository.findById(resumeId)
//                .orElseThrow(() -> new IllegalArgumentException("Resume not found with ID: " + resumeId));
//        s3Service.deleteFile(resume.getS3Key());
//        resumeRepository.delete(resume);
//    }
//
//    /**
//     * Retrieves mock interviews for an employee or all interviews, filtered by technology or resource type.
//     * @param employeeId Optional employee ID.
//     * @param technology Optional technology filter. Use "all" for no filter.
//     * @param resourceType Optional resource type filter. Use "all" for no filter.
//     * @return List of matching mock interviews.
//     */
//    public List<MockInterview> getMockInterviews(Long employeeId, String technology, String resourceType) {
//        List<MockInterview> interviews = (employeeId != null)
//                ? mockInterviewRepository.findByEmployeeId(employeeId)
//                : mockInterviewRepository.findAll();
//
//        return interviews.stream()
//                .filter(i -> technology == null || "all".equalsIgnoreCase(technology.trim()) ||
//                        Objects.equals(i.getEmployee().getTechnology().toLowerCase(), technology.trim().toLowerCase()))
//                .filter(i -> resourceType == null || "all".equalsIgnoreCase(resourceType.trim()) ||
//                        Objects.equals(i.getEmployee().getResourceType().toLowerCase(), resourceType.trim().toLowerCase()))
//                .toList();
//    }
//
//    /**
//     * Retrieves client interviews for an employee or all interviews, filtered by technology or resource type.
//     * @param employeeId Optional employee ID.
//     * @param technology Optional technology filter. Use "all" for no filter.
//     * @param resourceType Optional resource type filter. Use "all" for no filter.
//     * @return List of matching client interviews.
//     */
//    public List<ClientInterview> getClientInterviews(Long employeeId, String technology, String resourceType) {
//        List<ClientInterview> interviews = (employeeId != null)
//                ? clientInterviewRepository.findByEmployeeId(employeeId)
//                : clientInterviewRepository.findAll();
//
//        return interviews.stream()
//                .filter(i -> technology == null || "all".equalsIgnoreCase(technology.trim()) ||
//                        Objects.equals(i.getEmployee().getTechnology().toLowerCase(), technology.trim().toLowerCase()))
//                .filter(i -> resourceType == null || "all".equalsIgnoreCase(resourceType.trim()) ||
//                        Objects.equals(i.getEmployee().getResourceType().toLowerCase(), resourceType.trim().toLowerCase()))
//                .toList();
//    }
//
//    /**
//     * Adds an interview question.
//     * @param technology Technology category (e.g., "Java").
//     * @param question The question text.
//     * @param user The user submitting the question.
//     * @return Saved InterviewQuestion entity.
//     * @throws IllegalArgumentException If inputs are invalid.
//     */
//    public InterviewQuestion addInterviewQuestion(String technology, String question, String user) {
//        if (technology == null || technology.trim().isEmpty()) {
//            throw new IllegalArgumentException("Technology cannot be null or empty");
//        }
//        if (question == null || question.trim().isEmpty()) {
//            throw new IllegalArgumentException("Question cannot be null or empty");
//        }
//        if (user == null || user.trim().isEmpty()) {
//            throw new IllegalArgumentException("User cannot be null or empty");
//        }
//
//        InterviewQuestion interviewQuestion = new InterviewQuestion();
//        interviewQuestion.setTechnology(technology.trim());
//        interviewQuestion.setQuestion(question.trim());
//        interviewQuestion.setUser(user.trim());
//        interviewQuestion.setDate(LocalDate.now());
//        return interviewQuestionRepository.save(interviewQuestion);
//    }
//
//    /**
//     * Retrieves interview questions, optionally filtered by technology.
//     * @param technology Optional technology filter. Use "all" for no filter.
//     * @return List of matching interview questions.
//     */
//    public List<InterviewQuestion> getInterviewQuestions(String technology) {
//        if (technology != null && !"all".equalsIgnoreCase(technology.trim())) {
//            return interviewQuestionRepository.findByTechnology(technology.trim());
//        }
//        return interviewQuestionRepository.findAll();
//    }
	
	
	 private final EmployeeRepository employeeRepository;
	    private final JobDescriptionRepository jobDescriptionRepository;
	    private final ResumeRepository resumeRepository;
	    private final MockInterviewRepository mockInterviewRepository;
	    private final ClientInterviewRepository clientInterviewRepository;
	    private final InterviewQuestionRepository interviewQuestionRepository;
	    private final S3Service s3Service;

	    public EmployeeService(EmployeeRepository employeeRepository,
	                           JobDescriptionRepository jobDescriptionRepository,
	                           ResumeRepository resumeRepository,
	                           MockInterviewRepository mockInterviewRepository,
	                           ClientInterviewRepository clientInterviewRepository,
	                           InterviewQuestionRepository interviewQuestionRepository,
	                           S3Service s3Service) {
	        this.employeeRepository = employeeRepository;
	        this.jobDescriptionRepository = jobDescriptionRepository;
	        this.resumeRepository = resumeRepository;
	        this.mockInterviewRepository = mockInterviewRepository;
	        this.clientInterviewRepository = clientInterviewRepository;
	        this.interviewQuestionRepository = interviewQuestionRepository;
	        this.s3Service = s3Service;
	    }

	    /**
	     * Updates the technology for an employee.
	     * @param employeeId ID of the employee.
	     * @param technology New technology value (e.g., "Java").
	     * @return Updated Employee entity.
	     * @throws IllegalArgumentException If employeeId or technology is invalid.
	     */
	    public Employee updateTechnology(Long employeeId, String technology) {
	        if (employeeId == null) {
	            throw new IllegalArgumentException("Employee ID cannot be null");
	        }
	        if (technology == null || technology.trim().isEmpty()) {
	            throw new IllegalArgumentException("Technology cannot be null or empty");
	        }

	        Employee employee = employeeRepository.findById(employeeId)
	                .orElseThrow(() -> new IllegalArgumentException("Employee not found with ID: " + employeeId));
	        employee.setTechnology(technology.trim());
	        return employeeRepository.save(employee);
	    }

	    public List<JobDescription> getJobDescriptions(String search, String technology, String resourceType) {
	        if (search != null && !search.trim().isEmpty()) {
	            return jobDescriptionRepository.findByTitleContainingIgnoreCaseOrClientContainingIgnoreCase(search.trim(), search.trim());
	        }
	        if (technology != null && !"all".equalsIgnoreCase(technology.trim())) {
	            return jobDescriptionRepository.findByTechnology(technology.trim());
	        }
	        if (resourceType != null && !"all".equalsIgnoreCase(resourceType.trim())) {
	            return jobDescriptionRepository.findByResourceType(resourceType.trim());
	        }
	        return jobDescriptionRepository.findAll();
	    }

	    public Resume uploadResume(Long employeeId, Long jdId, MultipartFile file) throws IOException {
	        if (employeeId == null) {
	            throw new IllegalArgumentException("Employee ID cannot be null");
	        }
	        if (jdId == null) {
	            throw new IllegalArgumentException("Job Description ID cannot be null");
	        }
	        if (file == null || file.isEmpty()) {
	            throw new IllegalArgumentException("Resume file cannot be null or empty");
	        }

	        Employee employee = employeeRepository.findById(employeeId)
	                .orElseThrow(() -> new IllegalArgumentException("Employee not found with ID: " + employeeId));
	        JobDescription jd = jobDescriptionRepository.findById(jdId)
	                .orElseThrow(() -> new IllegalArgumentException("Job Description not found with ID: " + jdId));

	        String s3Key = s3Service.uploadFile(file, "resumes");
	        Resume resume = new Resume();
	        resume.setEmployee(employee);
	        resume.setJobDescription(jd);
	        resume.setS3Key(s3Key);
	        resume.setStatus("pending");
	        return resumeRepository.save(resume);
	    }

	    public byte[] downloadResume(Long resumeId) throws IOException {
	        if (resumeId == null) {
	            throw new IllegalArgumentException("Resume ID cannot be null");
	        }
	        Resume resume = resumeRepository.findById(resumeId)
	                .orElseThrow(() -> new IllegalArgumentException("Resume not found with ID: " + resumeId));
	        return s3Service.downloadFile(resume.getS3Key());
	    }

	    public void deleteResume(Long resumeId) {
	        if (resumeId == null) {
	            throw new IllegalArgumentException("Resume ID cannot be null");
	        }
	        Resume resume = resumeRepository.findById(resumeId)
	                .orElseThrow(() -> new IllegalArgumentException("Resume not found with ID: " + resumeId));
	        s3Service.deleteFile(resume.getS3Key());
	        resumeRepository.delete(resume);
	    }

	    public List<MockInterview> getMockInterviews(Long employeeId, String technology, String resourceType) {
	        List<MockInterview> interviews = (employeeId != null)
	                ? mockInterviewRepository.findByEmployeeId(employeeId)
	                : mockInterviewRepository.findAll();

	        return interviews.stream()
	                .filter(i -> technology == null || "all".equalsIgnoreCase(technology.trim()) ||
	                        Objects.equals(i.getEmployee().getTechnology().toLowerCase(), technology.trim().toLowerCase()))
	                .filter(i -> resourceType == null || "all".equalsIgnoreCase(resourceType.trim()) ||
	                        Objects.equals(i.getEmployee().getResourceType().toLowerCase(), resourceType.trim().toLowerCase()))
	                .toList();
	    }

	    public List<ClientInterview> getClientInterviews(Long employeeId, String technology, String resourceType) {
	        List<ClientInterview> interviews = (employeeId != null)
	                ? clientInterviewRepository.findByEmployeeId(employeeId)
	                : clientInterviewRepository.findAll();

	        return interviews.stream()
	                .filter(i -> technology == null || "all".equalsIgnoreCase(technology.trim()) ||
	                        Objects.equals(i.getEmployee().getTechnology().toLowerCase(), technology.trim().toLowerCase()))
	                .filter(i -> resourceType == null || "all".equalsIgnoreCase(resourceType.trim()) ||
	                        Objects.equals(i.getEmployee().getResourceType().toLowerCase(), resourceType.trim().toLowerCase()))
	                .toList();
	    }

	    public InterviewQuestion addInterviewQuestion(String technology, String question, String user) {
	        if (technology == null || technology.trim().isEmpty()) {
	            throw new IllegalArgumentException("Technology cannot be null or empty");
	        }
	        if (question == null || question.trim().isEmpty()) {
	            throw new IllegalArgumentException("Question cannot be null or empty");
	        }
	        if (user == null || user.trim().isEmpty()) {
	            throw new IllegalArgumentException("User cannot be null or empty");
	        }

	        InterviewQuestion interviewQuestion = new InterviewQuestion();
	        interviewQuestion.setTechnology(technology.trim());
	        interviewQuestion.setQuestion(question.trim());
	        interviewQuestion.setUser(user.trim());
	        interviewQuestion.setDate(LocalDate.now());
	        return interviewQuestionRepository.save(interviewQuestion);
	    }

	    public List<InterviewQuestion> getInterviewQuestions(String technology) {
	        if (technology != null && !"all".equalsIgnoreCase(technology.trim())) {
	            return interviewQuestionRepository.findByTechnology(technology.trim());
	        }
	        return interviewQuestionRepository.findAll();
	    }
}