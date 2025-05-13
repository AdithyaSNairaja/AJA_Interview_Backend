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

import com.AJA.Interview.Entity.ALTUser;
import com.AJA.Interview.Service.ALTUserService;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;

@RestController
@RequestMapping("/altUser")
public class ALTUserController {

	@Autowired
	private ALTUserService altUserService;
	
	@GetMapping("/get-all-altuser")
	public MappingJacksonValue getall(){
		 List<ALTUser> user=altUserService.getALL();
		 MappingJacksonValue jacksonValue=new MappingJacksonValue(user);
		 SimpleBeanPropertyFilter filter=SimpleBeanPropertyFilter.filterOutAllExcept("id","name","email","phone","role");
		 FilterProvider filters=new SimpleFilterProvider().addFilter("ALTUser", filter);
		 jacksonValue.setFilters(filters);
		 return jacksonValue;
	}
	
	@GetMapping("/get-by-altid/{id}")
	public MappingJacksonValue getbyid(@PathVariable Long id) {
		ALTUser altUser=altUserService.getbyId(id);
		MappingJacksonValue jacksonValue=new MappingJacksonValue(altUser);
		 SimpleBeanPropertyFilter filter=SimpleBeanPropertyFilter.filterOutAllExcept("id","name","email","phone","role");
		 FilterProvider filters=new SimpleFilterProvider().addFilter("ALTUser", filter);
		 jacksonValue.setFilters(filters);
		return jacksonValue;
	}
		
	 @PostMapping("/create-altUser")
	    public ResponseEntity<Map<String, String>> registerUser(@RequestBody ALTUser altUser) {
	        try {
	            Map<String, String> response = altUserService.createUserAndReturnToken(altUser);
	            return ResponseEntity.ok(response);
	        } catch (Exception e) {
	            Map<String, String> error = new HashMap<>();
	            error.put("error", "Registration failed: " + e.getMessage());
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
	        }
	    }

	@PutMapping("/update-altUser/{id}")
	public ResponseEntity<String> updateUser(@PathVariable Long id,@RequestBody ALTUser altUser){
		try {
			altUserService.updateAltUser(altUser,id);
			return ResponseEntity.ok("ALTUser Updated successfully");
		} catch (Exception e) {
			
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error updating user: " + e.getMessage());
		}
	}
	
	@DeleteMapping("/delete-altUser")
	public void deleteUser(@PathVariable Long id) {
		altUserService.deleteAltUser(id);
	}
	
	@PostMapping("/login")
	public Map<String, String> login(@RequestBody ALTUser altUser) {
		return altUserService.verify(altUser);
	}
	
	@PostMapping("/{altUserId}/upload")
	public ResponseEntity<String> uploadSingleFile(@PathVariable Long altUserId,
	                                               @RequestParam("file") MultipartFile[] file) {
	    if (file == null || file.length == 0) {
	        return ResponseEntity.badRequest().body("No file provided.");
	    }

	    if (file.length > 1) {
	        return ResponseEntity.badRequest().body("Only one file can be uploaded per request.");
	    }

	    return altUserService.uploadFile(altUserId, file[0]);
	}
}
