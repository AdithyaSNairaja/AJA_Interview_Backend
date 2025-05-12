package com.AJA.Interview.Entity;

import com.fasterxml.jackson.annotation.JsonFilter;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonFilter("User")
public class User {
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;
@Column(nullable = false)
private String empid;
@Column(nullable = false)
private String name;
@Email
private String email;
@Column(nullable = false)
private String password;

private String fileUrl1;

private String fileUrl2;

private Batch batch;

private String technology;


public enum Batch{
	TCT1,TCT2,TCT3,OM
}
}
