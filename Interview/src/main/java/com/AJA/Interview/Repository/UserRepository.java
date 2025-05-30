package com.AJA.Interview.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.AJA.Interview.Entity.User;
@Repository
public interface UserRepository extends JpaRepository<User, Long>{

	User findByEmail(String email);
	List<User> findByTechnology(String technology);

}
