package fr.miage.MIAGELand.employee;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
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

    /**
     * Create an employee in database
     * @param body
     * @return Employee
     */
    @PostMapping()
    public List<Employee> createEmployee(@RequestBody  Map<String, Map<String, String>> body) {
        List<Employee> employees = new ArrayList<>();
        for (Map.Entry<String, Map<String, String>> entry : body.entrySet()) {
            Map<String, String> value = entry.getValue();
            Employee employee = new Employee(
                    value.get("name"),
                    value.get("surname"),
                    value.get("email"),
                    EmployeeRole.valueOf(value.get("role"))
            );
            employees.add(employee);
        }
        return employeeRepository.saveAll(employees);
    }

    @GetMapping("")
    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }


}
