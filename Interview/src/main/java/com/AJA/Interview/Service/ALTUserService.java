package com.AJA.Interview.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.AJA.Interview.Entity.ALTUser;
import com.AJA.Interview.Repository.ALTUserRepository;

@Service
public class ALTUserService {

	@Autowired
	private ALTUserRepository altUserRepository;
	
	 @Autowired
	    private JwtService jwtService;

	    @Autowired
	    private PasswordEncoder passwordEncoder;

	    @Autowired
	    private AuthenticationManager authenticationManager;
	 
	    @Autowired
	    private StorageService storageService;
	    
	public List<ALTUser> getALL() {
		return altUserRepository.findAll();
		}

		public ALTUser getbyId(Long id) {
			ALTUser user=altUserRepository.findById(id).orElseThrow(()->new RuntimeException("No such User"));
			return user;
		}
		
		
//		}

		public Map<String, String> createUserAndReturnToken(ALTUser altUser) {
	        // Check if email or phone already exists
	        if (altUserRepository.existsByEmail(altUser.getEmail()) || altUserRepository.existsByPhone(altUser.getPhone())) {
	            throw new RuntimeException("Email or phone already registered");
	        }

	        // Save encoded password
	        String rawPassword = altUser.getPassword();
	        altUser.setPassword(passwordEncoder.encode(rawPassword));
	        altUserRepository.save(altUser);

	        // Manually verify password and issue token
	        ALTUser savedUser = altUserRepository.findByEmail(altUser.getEmail());
	        if (savedUser != null && passwordEncoder.matches(rawPassword, savedUser.getPassword())) {
	            String token = jwtService.generateToken(savedUser.getEmail());
	            Map<String, String> response = new HashMap<>();
	            response.put("token", token);
	            response.put("username", savedUser.getEmail());
	            return response;
	        } else {
	            throw new RuntimeException("Authentication failed");
	        }
	    }		
		public void updateAltUser(ALTUser altUser,Long id) {
			ALTUser altUsers=altUserRepository.findById(id).orElseThrow(()->new RuntimeException("No such User"));
			altUsers.setName(altUser.getName());
			altUsers.setEmail(altUser.getEmail());
			altUsers.setPassword(altUser.getPassword());
			altUsers.setPhone(altUser.getPhone());
			altUsers.setRole(altUser.getRole());
			altUserRepository.save(altUsers);
		}
		
		
		
		public void deleteAltUser(Long id) {
			altUserRepository.deleteById(id);
		}
		
		public Map<String, String> verify(ALTUser altUser) {
		    Map<String, String> res = new HashMap<>();

		    try {
		        ALTUser verifiedUser = altUserRepository.findByEmail(altUser.getEmail());

		        if (verifiedUser == null) {
		            res.put("error", "Invalid username or password");
		            return res;
		        }

		        // Manually verify password
		        if (passwordEncoder.matches(altUser.getPassword(), verifiedUser.getPassword())) {
		            String token = jwtService.generateToken(verifiedUser.getEmail());
		            res.put("token", token);
		            res.put("username", verifiedUser.getEmail());
		        } else {
		            res.put("error", "Invalid username or password");
		        }

		    } catch (Exception e) {
		        res.put("error", "Authentication failed due to an internal error");
		    }

		    return res;
		}

		
		public ResponseEntity<String> uploadFiles(Long altUserId, MultipartFile[] files) {
		    if (files == null || files.length == 0) {
		        return ResponseEntity.badRequest().body("No files provided.");
		    }

		    Optional<ALTUser> optionalUser = altUserRepository.findById(altUserId);
		    if (optionalUser.isEmpty()) {
		        return ResponseEntity.badRequest().body("User not found.");
		    }

		    ALTUser altUser = optionalUser.get();

		    List<String> uploadedUrls = new ArrayList<>();
		    for (MultipartFile file : files) {
		        if (file != null && !file.isEmpty()) {
		            String uploadedUrl = storageService.uploadFile(file);
		            uploadedUrls.add(uploadedUrl);
		        }
		    }

		    // Add to existing list
		    altUser.getJdFiles().addAll(uploadedUrls);
		    altUserRepository.save(altUser);

		    return ResponseEntity.ok("Files uploaded successfully: " + uploadedUrls);
		}
		
		public ResponseEntity<?> downloadFilesByUserId(Long userId) {
		    Optional<ALTUser> optionalUser = altUserRepository.findById(userId);
		    if (optionalUser.isEmpty()) {
		        return ResponseEntity.badRequest().body("User not found.");
		    }

		    ALTUser user = optionalUser.get();
		    List<String> fileKeys = user.getJdFiles();

		    if (fileKeys == null || fileKeys.isEmpty()) {
		        return ResponseEntity.status(404).body("No files found for this user.");
		    }

		    return ResponseEntity.ok(fileKeys); // or stream/download all if required
		}
	
		public ResponseEntity<?> downloadFileByIndex(Long userId, int index) {
		    Optional<ALTUser> optionalUser = altUserRepository.findById(userId);
		    if (optionalUser.isEmpty()) {
		        return ResponseEntity.badRequest().body("User not found.");
		    }

		    ALTUser user = optionalUser.get();
		    
		    List<String> fileKeys = user.getJdFiles();

		    if (fileKeys == null || fileKeys.isEmpty()) {
		        return ResponseEntity.status(404).body("No files found.");
		    }

		    if (index < 0 || index >= fileKeys.size()) {
		        return ResponseEntity.status(400).body("Invalid file index.");
		    }
		    
		    String fileKey = fileKeys.get(index);
		    System.out.println("Downloading file with key: " + fileKey);
		    return storageService.downloadFiles(fileKey);  // This should return ResponseEntity with file stream
		}

		public ResponseEntity<String> deleteFileByIndex(Long userId, int index) {
		    Optional<ALTUser> optionalUser = altUserRepository.findById(userId);
		    if (optionalUser.isEmpty()) {
		        return ResponseEntity.badRequest().body("User not found.");
		    }

		    ALTUser user = optionalUser.get();
		    List<String> fileKeys = user.getJdFiles();

		    if (fileKeys == null || fileKeys.isEmpty()) {
		        return ResponseEntity.status(404).body("No files found.");
		    }

		    if (index < 0 || index >= fileKeys.size()) {
		        return ResponseEntity.status(400).body("Invalid file index.");
		    }

		    String fileKey = fileKeys.get(index);

		    // Remove from S3 or other storage
		    try {
		        storageService.deleteFile(fileKey);  // Assumes you have this method in your service
		    } catch (Exception e) {
		        return ResponseEntity.status(500).body("Failed to delete file from storage: " + e.getMessage());
		    }

		    // Remove from list and save user
		    fileKeys.remove(index);
		    user.setJdFiles(fileKeys);
		    altUserRepository.save(user);

		    return ResponseEntity.ok("File deleted successfully.");
		}

}
