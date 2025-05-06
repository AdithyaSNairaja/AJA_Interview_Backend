package com.AJA.Interview.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
}
