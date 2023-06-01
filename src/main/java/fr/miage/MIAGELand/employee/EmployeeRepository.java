package fr.miage.MIAGELand.employee;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Employee repository
 * Handle all employee related database requests
 * @see Employee
 */
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    Employee findByEmail(String email);
    long countByRole(EmployeeRole employeeRole);
}
