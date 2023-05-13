package fr.miage.MIAGELand.employee;

import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    Employee findByEmail(String email);

    Employee findById(long id);
    void deleteById(long id);
}
