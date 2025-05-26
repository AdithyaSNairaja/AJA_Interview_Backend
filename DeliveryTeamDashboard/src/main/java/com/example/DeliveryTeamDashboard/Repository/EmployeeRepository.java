package com.example.DeliveryTeamDashboard.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.DeliveryTeamDashboard.Entity.Employee;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
	Optional<Employee> findByUserEmail(String email);
    List<Employee> findByTechnology(String technology);
    List<Employee> findByResourceType(String resourceType);
}
