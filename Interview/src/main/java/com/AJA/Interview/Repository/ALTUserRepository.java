package com.AJA.Interview.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.AJA.Interview.Entity.ALTUser;

@Repository
public interface ALTUserRepository extends JpaRepository<ALTUser, Long>{
	ALTUser findByEmail(String email);
	boolean existsByEmail(String email);
	boolean existsByPhone(String phone);

}
