package com.AJA.Interview.Entity;

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

private Batch batch;

private String technology;


public enum Batch{
	TCT1,TCT2,TCT3,OM
}
}
