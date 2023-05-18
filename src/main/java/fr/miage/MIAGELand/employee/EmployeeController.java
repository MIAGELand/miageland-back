package fr.miage.MIAGELand.employee;

import fr.miage.MIAGELand.api.stats.ApiStatsEmployee;
import fr.miage.MIAGELand.security.NotAllowedException;
import fr.miage.MIAGELand.security.SecurityService;
import jakarta.transaction.Transactional;
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
    private final EmployeeService employeeService;
    private final SecurityService securityService;

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

    @DeleteMapping("/{id}")
    @Transactional
    public void removeEmployee(@PathVariable Long id, @RequestHeader("Authorization") String authorizationHeader) throws NotAllowedException {
        if (!securityService.isAdmin(authorizationHeader)) {
            throw new NotAllowedException();
        }
        employeeRepository.deleteById(id);
    }

    @PatchMapping("/{id}")
    public Employee updateEmployee(@PathVariable Long id,
                                   @RequestBody Map<String, String> body,
                                   @RequestHeader("Authorization") String authorizationHeader) throws EmployeeRoleNotValidException, NotAllowedException {
        if (!securityService.isAdmin(authorizationHeader)) {
            throw new NotAllowedException();
        }

        Employee employee = employeeRepository.findById(id).orElseThrow();
        switch (Enum.valueOf(EmployeeRole.class, body.get("role"))) {
            case ADMIN -> employeeService.upgradeEmployeeRole(employee);
            case CLASSIC -> employeeService.downgradeEmployeeRole(employee);
            default -> throw new EmployeeRoleNotValidException("Employee role is not valid.");
        }
        return employeeRepository.save(employee);
    }

    @GetMapping("/stats")
    public ApiStatsEmployee getEmployeeStats() {
        return new ApiStatsEmployee(
                employeeRepository.count(),
                employeeRepository.countByRole(EmployeeRole.ADMIN),
                employeeRepository.countByRole(EmployeeRole.CLASSIC)
        );
    }
}
