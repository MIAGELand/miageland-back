package fr.miage.MIAGELand.security;

import fr.miage.MIAGELand.employee.Employee;
import fr.miage.MIAGELand.employee.EmployeeRepository;
import fr.miage.MIAGELand.employee.EmployeeRole;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class SecurityService {
    private final EmployeeRepository employeeRepository;

    public boolean isEmployee(String authorizationHeader) throws NotAllowedException {
        String email = Headers.extractEmailFromAuthorizationHeader(authorizationHeader);

        Employee employee = employeeRepository.findByEmail(email);
        if (employee == null) {
            throw new NotAllowedException("You are not allowed to do this.");
        }
        return true;
    }

    public boolean isAdminOrManager(String authorizationHeader) throws NotAllowedException {
        String email = Headers.extractEmailFromAuthorizationHeader(authorizationHeader);

        Employee employee = employeeRepository.findByEmail(email);
        if (employee == null
                || EmployeeRole.MANAGER != employee.getRole()
                && EmployeeRole.ADMIN != employee.getRole()
        ) {
            throw new NotAllowedException("You are not allowed to do this.");
        }
        return true;
    }

    public boolean isManager(String authorizationHeader) throws NotAllowedException {
        String email = Headers.extractEmailFromAuthorizationHeader(authorizationHeader);

        Employee employee = employeeRepository.findByEmail(email);
        if (employee == null || EmployeeRole.MANAGER != employee.getRole()) {
            throw new NotAllowedException("You are not allowed to do this.");
        }
        return true;
    }
}
