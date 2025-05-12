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

import com.AJA.Interview.Entity.User;
import com.AJA.Interview.Repository.UserRepository;

@Service
public class UserService {
	
	   @Autowired
	    private UserRepository userRepository;

	    @Autowired
	    private JwtService jwtService;

	    @Autowired
	    private PasswordEncoder passwordEncoder;

	    @Autowired
	    private AuthenticationManager authenticationManager;
	    
	    @Autowired
		private StorageService storageService;

	 
	
	public List<User> getALL() {
	return userRepository.findAll();
	}

	public User getbyId(Long id) {
		User user=userRepository.findById(id).orElseThrow(()->new RuntimeException("No such User"));
		return user;
	}
	
	public void createUser(User user) { 
		userRepository.save(user);
	}
	
	public Map<String, String> createUserAndReturnToken(User user) {
	    String rawPassword = user.getPassword(); // hold raw password
	    
	    user.setPassword(passwordEncoder.encode(rawPassword));
	    userRepository.save(user);

	    Authentication authentication = authenticationManager.authenticate(
	        new UsernamePasswordAuthenticationToken(user.getEmail(), rawPassword) // use raw password
	    );

	    if (authentication.isAuthenticated()) {
	        String token = jwtService.generateToken(user.getEmail());
	        Map<String, String> response = new HashMap<>();
	        response.put("token", token);
	        response.put("username", user.getEmail());
	        return response;
	    } else {
	        throw new RuntimeException("Authentication failed");
	    }
	}

	
	public void updateUser(User user,Long id) {
		User users=userRepository.findById(id).orElseThrow(()->new RuntimeException("No such User"));
//		users.setEmpid(user.getEmpid());
		users.setName(user.getName());
		users.setEmail(user.getEmail());
		users.setBatch(user.getBatch());
		userRepository.save(users);
	}
	
	public List<User> gettech(String tec){
		return userRepository.findByTechnology(tec);
	}
	
	public void deleteUser(Long id) {
		userRepository.deleteById(id);
	}
	
	public Map<String, String> verify(User user) {
 
        Map<String, String> res = new HashMap<>();
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword()));
            User verifiedUser = userRepository.findByEmail(user.getEmail());
            if (authentication.isAuthenticated()) {
                String token = jwtService.generateToken(user.getEmail());
                res.put("token", token);
                res.put("username", verifiedUser.getEmail());
                return res;
            }
        } catch (BadCredentialsException e) {
          
            res.put("error", "Invalid username or password");
            
        } catch (Exception e) {
        	
            res.put("error", "Authentication failed due to an internal error");
            
        }
        return res;
    }
	public ResponseEntity<String> uploadFile(Long userId, MultipartFile file) {
		if (file == null || file.isEmpty()) {
            return ResponseEntity.badRequest().body("Please upload a valid file.");
        }

		Optional<User> optionalUser = userRepository.findById(userId);
		if (optionalUser.isEmpty()) {
			return ResponseEntity.badRequest().body("User not found.");
		}

		User user = optionalUser.get();

		boolean hasFile1 = user.getFileUrl1() != null && !user.getFileUrl1().isEmpty();
		boolean hasFile2 = user.getFileUrl2() != null && !user.getFileUrl2().isEmpty();

		if (hasFile1 && hasFile2) {
			return ResponseEntity.badRequest().body("File limit reached. Delete or replace a file first.");
		}

		String uploadedUrl = storageService.uploadFile(file);
		if (!hasFile1) {
			user.setFileUrl1(uploadedUrl);
		} else {
			user.setFileUrl2(uploadedUrl);
		}

		userRepository.save(user);
		return ResponseEntity.ok("File uploaded successfully: " + uploadedUrl);
	}

	public ResponseEntity<?> deleteFile(Long userId, int fileNumber) {
		Optional<User> optionalUser = userRepository.findById(userId);
		if (optionalUser.isEmpty()) {
			return ResponseEntity.badRequest().body("User not found.");
		}

		User user = optionalUser.get();
		String fileUrl;

		if (fileNumber == 1) {
			fileUrl = user.getFileUrl1();
			if (fileUrl != null) {
				storageService.deleteFile(fileUrl);
				user.setFileUrl1(null);
			} else {
				return ResponseEntity.badRequest().body("File 1 not found.");
			}
		} else if (fileNumber == 2) {
			fileUrl = user.getFileUrl2();
			if (fileUrl != null) {
				storageService.deleteFile(fileUrl);
				user.setFileUrl2(null);
			} else {
				return ResponseEntity.badRequest().body("File 2 not found.");
			}
		} else {
			return ResponseEntity.badRequest().body("Invalid file number. Use 1 or 2.");
		}

		userRepository.save(user);
		return ResponseEntity.ok("File deleted successfully.");
	}

	public ResponseEntity<String> replaceFile(Long userId, int fileNumber, MultipartFile newFile) {
		Optional<User> optionalUser = userRepository.findById(userId);
		if (optionalUser.isEmpty()) {
			return ResponseEntity.badRequest().body("User not found.");
		}

		User user = optionalUser.get();
		String oldFileUrl;

		if (fileNumber == 1) {
			oldFileUrl = user.getFileUrl1();
			if (oldFileUrl == null)
				return ResponseEntity.badRequest().body("No file found in slot 1.");
			storageService.deleteFile(oldFileUrl);
			String newUrl = storageService.uploadFile(newFile);
			user.setFileUrl1(newUrl);
		} else if (fileNumber == 2) {
			oldFileUrl = user.getFileUrl2();
			if (oldFileUrl == null)
				return ResponseEntity.badRequest().body("No file found in slot 2.");
			storageService.deleteFile(oldFileUrl);
			String newUrl = storageService.uploadFile(newFile);
			user.setFileUrl2(newUrl);
		} else {
			return ResponseEntity.badRequest().body("Invalid file number. Use 1 or 2.");
		}

		userRepository.save(user);
		return ResponseEntity.ok("File replaced successfully.");
	}

}
