package fr.miage.MIAGELand.employee;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Service for the employee.
 * Handle all employee related business logic
 * @see Employee
 */
@Service
@AllArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    /**
     * Check if the employee fields are valid.
     * @param employee The employee to check
     * @return true if the employee fields are valid, false otherwise
     */
    public boolean isEmployeeFieldValid(Employee employee) {
        return employee.getName() != null && employee.getSurname() != null && employee.getEmail() != null && employee.getRole() != null;
    }

    /**
     * Upgrade an employee from classic to admin.
     * @param employee The employee to upgrade
     * @throws EmployeeRoleNotValidException If the employee is already admin
     * @see EmployeeRole
     */
    public void upgradeEmployeeRole(Employee employee) throws EmployeeRoleNotValidException {
        EmployeeRole currentRole = employee.getRole();
        switch (currentRole) {
            case CLASSIC -> employee.setRole(EmployeeRole.ADMIN);
            case ADMIN -> throw new EmployeeRoleNotValidException("Employee is already admin.");
        }
        employeeRepository.save(employee);
    }

    /**
     * Downgrade an employee from admin to classic.
     * @param employee The employee to downgrade
     * @throws EmployeeRoleNotValidException If the employee is already classic
     * @see EmployeeRole
     */
    public void downgradeEmployeeRole(Employee employee) throws EmployeeRoleNotValidException {
        EmployeeRole currentRole = employee.getRole();
        switch (currentRole) {
            case ADMIN -> employee.setRole(EmployeeRole.CLASSIC);
            case CLASSIC -> throw new EmployeeRoleNotValidException("Employee is already classic.");
        }
        employeeRepository.save(employee);
    }

}
