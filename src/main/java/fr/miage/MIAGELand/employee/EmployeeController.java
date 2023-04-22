package fr.miage.MIAGELand.employee;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

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
    @GetMapping("/{email}")
    public Employee getEmployee(@PathVariable String email) {
        return employeeRepository.findByEmail(email);
    }

    @PostMapping()
    public Employee createEmployee(@RequestBody Map body) {
        String name = (String) body.get("name");
        String surname = (String) body.get("surname");
        String email = (String) body.get("email");
        EmployeeRole role = EmployeeRole.valueOf((String) body.get("role"));
        Employee employee = new Employee(name, surname, email, role);
        employeeRepository.save(employee);
        return employee;
    }

}
