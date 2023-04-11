package fr.miage.MIAGELand.employee;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/api/employees")
public class EmployeeController {

    private final EmployeeRepository employeeRepository;

    /**
     * Get employee by email
     * @param email
     * @return Employee
     */
    @GetMapping()
    public Employee getEmployee(@RequestBody String email) {
        return employeeRepository.findByEmail(email);
    }
}
