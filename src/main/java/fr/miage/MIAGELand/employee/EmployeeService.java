package fr.miage.MIAGELand.employee;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    public boolean isEmployeeFieldValid(Employee employee) {
        return employee.getName() != null && employee.getSurname() != null && employee.getEmail() != null && employee.getRole() != null;
    }
    public void upgradeEmployeeRole(Employee employee) throws EmployeeRoleNotValidException {
        EmployeeRole currentRole = employee.getRole();
        switch (currentRole) {
            case CLASSIC -> employee.setRole(EmployeeRole.ADMIN);
            case ADMIN -> throw new EmployeeRoleNotValidException("Employee is already admin.");
        }
        employeeRepository.save(employee);
    }

    public void downgradeEmployeeRole(Employee employee) throws EmployeeRoleNotValidException {
        EmployeeRole currentRole = employee.getRole();
        switch (currentRole) {
            case ADMIN -> employee.setRole(EmployeeRole.CLASSIC);
            case CLASSIC -> throw new EmployeeRoleNotValidException("Employee is already classic.");
        }
        employeeRepository.save(employee);
    }

}
