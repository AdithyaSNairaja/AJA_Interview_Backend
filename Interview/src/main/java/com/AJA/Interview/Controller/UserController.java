package com.AJA.Interview.Controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.AJA.Interview.Entity.User;
import com.AJA.Interview.Service.UserService;

@RestController
@RequestMapping("/user")
public class UserController {

	@Autowired
	private UserService userService;
	
	@GetMapping("/get-all-user")
	public List<User> getall(){
		return userService.getALL();
	}
	
	@GetMapping("/get-by-id/{id}")
	public User getbyid(@PathVariable Long id) {
		return userService.getbyId(id);
	}
	
	 @PostMapping("/create-user")
	    public ResponseEntity<Map<String, String>> registerUser(@RequestBody User user) {
	        try {
	            Map<String, String> response = userService.createUserAndReturnToken(user);
	            return ResponseEntity.ok(response);
	        } catch (Exception e) {
	            Map<String, String> error = new HashMap<>();
	            error.put("error", "Registration failed: " + e.getMessage());
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
	        }
	    }

	@PutMapping("/update-user")
	public ResponseEntity<String> updateUser(@PathVariable Long id,@RequestBody User user){
		try {
			userService.updateUser(user,id);
			return ResponseEntity.ok("User Registered successfully");
		} catch (Exception e) {
			
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error updating user: " + e.getMessage());
		}
	}
	
	@DeleteMapping("/delete-user")
	public void deleteUser(@PathVariable Long id) {
		userService.deleteUser(id);
	}
	
	@PostMapping("/login")
	public Map<String, String> login(@RequestBody User user) {
		return userService.verify(user);
	}

}
