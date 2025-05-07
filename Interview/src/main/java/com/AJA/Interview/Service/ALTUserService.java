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
	 
	
	public List<ALTUser> getALL() {
		return altUserRepository.findAll();
		}

		public ALTUser getbyId(Long id) {
			ALTUser user=altUserRepository.findById(id).orElseThrow(()->new RuntimeException("No such User"));
			return user;
		}
		
		
		public Map<String, String> createUserAndReturnToken(ALTUser altUser) {
		    String rawPassword = altUser.getPassword(); // hold raw password
		    
		    altUser.setPassword(passwordEncoder.encode(rawPassword));
		    altUserRepository.save(altUser);

		    Authentication authentication = authenticationManager.authenticate(
		        new UsernamePasswordAuthenticationToken(altUser.getEmail(), rawPassword) // use raw password
		    );

		    if (authentication.isAuthenticated()) {
		        String token = jwtService.generateToken(altUser.getEmail());
		        Map<String, String> response = new HashMap<>();
		        response.put("token", token);
		        response.put("username", altUser.getEmail());
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
	            Authentication authentication = authenticationManager.authenticate(
	                    new UsernamePasswordAuthenticationToken(altUser.getEmail(), altUser.getPassword()));
	            ALTUser verifiedUser = altUserRepository.findByEmail(altUser.getEmail());
	            if (authentication.isAuthenticated()) {
	                String token = jwtService.generateToken(altUser.getEmail());
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
