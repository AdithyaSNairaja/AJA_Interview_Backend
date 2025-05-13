package com.AJA.Interview.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
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

		
		public ResponseEntity<String> uploadFile(Long altUserId, MultipartFile file) {
			if (file == null || file.isEmpty()) {
	            return ResponseEntity.badRequest().body("Please upload a valid file.");
	        }

			Optional<ALTUser> optionalUser = altUserRepository.findById(altUserId);
			if (optionalUser.isEmpty()) {
				return ResponseEntity.badRequest().body("User not found.");
			}

			ALTUser altUser = optionalUser.get();

			boolean hasFile1 = altUser.getJD() != null && !altUser.getJD().isEmpty();


			String uploadedUrl = storageService.uploadFile(file);
		
			altUser.setJD(uploadedUrl);
			

			altUserRepository.save(altUser);
			return ResponseEntity.ok("File uploaded successfully: " + uploadedUrl);
		}
}
