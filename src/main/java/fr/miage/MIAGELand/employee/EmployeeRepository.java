package fr.miage.MIAGELand.employee;

import fr.miage.MIAGELand.api.ApiEmployee;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Employee repository
 * Handle all employee related database requests
 * @see Employee
 */
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    Employee findByEmail(String email);
    long countByRole(EmployeeRole employeeRole);

    List<Employee> findAll(Specification<ApiEmployee> specs);
}
