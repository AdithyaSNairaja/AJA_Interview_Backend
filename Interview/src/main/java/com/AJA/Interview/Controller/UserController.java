package com.AJA.Interview.Controller;

import java.util.HashMap; 
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.AJA.Interview.Entity.User;
import com.AJA.Interview.Service.UserService;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;

@RestController
@RequestMapping("/user")
public class UserController {

	@Autowired
	private UserService userService;
	
	@GetMapping("/get-all-user")
	public MappingJacksonValue getall(){
		 List<User> user=userService.getALL();
		 MappingJacksonValue jacksonValue=new MappingJacksonValue(user);
		 SimpleBeanPropertyFilter filter=SimpleBeanPropertyFilter.filterOutAllExcept("id","name","empid","email","batch","technology");
		 FilterProvider filters=new SimpleFilterProvider().addFilter("User", filter);
		 jacksonValue.setFilters(filters);
		 return jacksonValue;
	}
	
	@GetMapping("/get-by-id/{id}")
	public MappingJacksonValue getbyid(@PathVariable Long id) {
		User user=userService.getbyId(id);
		MappingJacksonValue jacksonValue=new MappingJacksonValue(user);
		 SimpleBeanPropertyFilter filter=SimpleBeanPropertyFilter.filterOutAllExcept("id","name","empid","email","batch","technology");
		 FilterProvider filters=new SimpleFilterProvider().addFilter("User", filter);
		 jacksonValue.setFilters(filters);
		return jacksonValue;
	}
	
	@GetMapping("/get-by-technonlogy")
	public MappingJacksonValue getbytechnology(@RequestParam String tec){
		List<User> user= userService.gettech(tec);
		MappingJacksonValue jacksonValue=new MappingJacksonValue(user);
		 SimpleBeanPropertyFilter filter=SimpleBeanPropertyFilter.filterOutAllExcept("id","name","empid","email","batch","technology");
		 FilterProvider filters=new SimpleFilterProvider().addFilter("User", filter);
		 jacksonValue.setFilters(filters);
		 return jacksonValue;
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
			return ResponseEntity.ok("User Updated successfully");
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
	
	@PostMapping("/{userId}/upload")
	public ResponseEntity<String> uploadSingleFile(@PathVariable Long userId,
	                                               @RequestParam("file") MultipartFile[] file) {
	    if (file == null || file.length == 0) {
	        return ResponseEntity.badRequest().body("No file provided.");
	    }

	    if (file.length > 1) {
	        return ResponseEntity.badRequest().body("Only one file can be uploaded per request.");
	    }

	    return userService.uploadFile(userId, file[0]);
	}
	
	@DeleteMapping("/{userId}/delete-file")
	public ResponseEntity<?> deleteFile(@PathVariable Long userId,
	                                        @RequestParam("fileNumber") int fileNumber) {
	return userService.deleteFile(userId, fileNumber);
	}

	@PutMapping("/{userId}/update-file")
	public ResponseEntity<String> updateFile(@PathVariable Long userId,
			 								@RequestParam("fileNumber") int fileNumber,
	                                         @RequestParam("file") MultipartFile[] file) {
	    if (file == null || file.length == 0) {
	        return ResponseEntity.badRequest().body("No file provided.");
	    }

	    if (file.length > 1) {
	        return ResponseEntity.badRequest().body("Only one file can be updated per request.");
	    }

	    return userService.replaceFile(userId, fileNumber, file[0]);
	}

	@GetMapping("/{userId}/file/download")
	public ResponseEntity<?> downloadFile(@PathVariable Long userId,
	                                      @RequestParam("fileNumber") int fileNumber) {
	    return userService.downloadFileByNumber(userId, fileNumber);
	}

}
