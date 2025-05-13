package com.AJA.Interview.Entity;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFilter;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonFilter("ALTUser")
public class ALTUser {
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;

private String name;


@Column(unique = true, nullable = false)
private String email;

private String password;

@Column(unique = true, nullable = false)
private String phone;

private String role;

private String JD;

}
