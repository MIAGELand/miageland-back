package fr.miage.MIAGELand.employee;

import fr.miage.MIAGELand.api.ApiEmployee;
import fr.miage.MIAGELand.api.stats.ApiStatsEmployee;
import fr.miage.MIAGELand.park.Park;
import fr.miage.MIAGELand.park.ParkRepository;
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
    private final ParkRepository parkRepository;
    private final EmployeeService employeeService;
    private final SecurityService securityService;

    /**
     * Get employee by email
     * @param email Email of the employee
     * @return ApiEmployee
     */
    @GetMapping("/{email}")
    public ApiEmployee getEmployee(@PathVariable String email) throws Exception {
        Employee employee = employeeRepository.findByEmail(email);
        if (employee == null && employeeRepository.findAll().isEmpty()) {
            Employee newEmployee = new Employee(
                   "Admin",
                    "MiageLand",
                    "admin",
                    EmployeeRole.MANAGER
            );
            Park park = new Park(
                    1L,
                    10L,
                    10L,
                    java.time.LocalDateTime.now()
            );
            employeeRepository.save(newEmployee);
            parkRepository.save(park);
            return new ApiEmployee(
                    newEmployee.getId(),
                    newEmployee.getName(),
                    newEmployee.getSurname(),
                    newEmployee.getEmail(),
                    newEmployee.getRole()
            );
        }

        if (employee == null) {
            throw new Exception("Employee not found");
        }

        return new ApiEmployee(
                employee.getId(),
                employee.getName(),
                employee.getSurname(),
                employee.getEmail(),
                employee.getRole()
        );
    }

    /**
     * Create an employee in database
     * @param body Map<String, Employee>
     * @return List of ApiEmployee
     */
    @PostMapping
    public List<ApiEmployee> createEmployee(@RequestBody Map<String, Employee> body,
                                         @RequestHeader("Authorization") String authorizationHeader) throws NotAllowedException {
        if (!securityService.isManager(authorizationHeader)) {
            throw new NotAllowedException();
        }
        // Check all fields are present
        List<Employee> employees = new ArrayList<>();
        for (Employee employee : body.values()) {
            // Check all fields are present
            if (!employeeService.isEmployeeFieldValid(employee)) {
                throw new IllegalArgumentException("Missing field");
            }
            Employee current = new Employee(
                    employee.getName(),
                    employee.getSurname(),
                    employee.getEmail(),
                    employee.getRole()
            );
            employees.add(current);
        }
        return employeeRepository.saveAll(employees)
                .stream()
                .map(employee -> new ApiEmployee(
                        employee.getId(),
                        employee.getName(),
                        employee.getSurname(),
                        employee.getEmail(),
                        employee.getRole()
                )
        ).toList();
    }

    /**
     * Get all employees
     * @param authorizationHeader Authorization header
     * @return List of ApiEmployee
     * @throws NotAllowedException If the user is not an employee
     */
    @GetMapping
    public List<ApiEmployee> getAllEmployees(@RequestHeader("Authorization") String authorizationHeader) throws NotAllowedException {
        if (!securityService.isEmployee(authorizationHeader)) {
            throw new NotAllowedException();
        }
        return employeeRepository.findAll().stream().map(
                employee -> new ApiEmployee(
                        employee.getId(),
                        employee.getName(),
                        employee.getSurname(),
                        employee.getEmail(),
                        employee.getRole()
                )
        ).toList();
    }

    /**
     * Delete an employee
     * @param id id of the employee to delete
     * @param authorizationHeader Authorization header
     * @throws NotAllowedException If the user is not the manager
     */
    @DeleteMapping("/{id}")
    @Transactional
    public void removeEmployee(@PathVariable Long id, @RequestHeader("Authorization") String authorizationHeader) throws NotAllowedException {
        if (!securityService.isManager(authorizationHeader)) {
            throw new NotAllowedException();
        }
        employeeRepository.deleteById(id);
    }

    /**
     * Update an employee role
     * @param id id of the employee to update
     * @param body Map<String, String>
     * @param authorizationHeader Authorization header
     * @return ApiEmployee
     * @throws EmployeeRoleNotValidException If the role is not valid
     * @throws NotAllowedException If the user is not the manager
     */
    @PatchMapping("/{id}")
    public ApiEmployee updateEmployee(@PathVariable Long id,
                                   @RequestBody Map<String, String> body,
                                   @RequestHeader("Authorization") String authorizationHeader) throws EmployeeRoleNotValidException, NotAllowedException {
        if (!securityService.isManager(authorizationHeader)) {
            throw new NotAllowedException();
        }

        Employee employee = employeeRepository.findById(id).orElseThrow();
        switch (Enum.valueOf(EmployeeRole.class, body.get("role"))) {
            case ADMIN -> employeeService.upgradeEmployeeRole(employee);
            case CLASSIC -> employeeService.downgradeEmployeeRole(employee);
            default -> throw new EmployeeRoleNotValidException("Employee role is not valid.");
        }
        return new ApiEmployee(
                employee.getId(),
                employee.getName(),
                employee.getSurname(),
                employee.getEmail(),
                employee.getRole()
        );
    }

    /**
     * Get employee stats (number of employees, number of admins, number of classic employees)
     * @param authorizationHeader Authorization header
     * @return ApiStatsEmployee
     * @throws NotAllowedException If the user is not the manager
     */
    @GetMapping("/stats")
    public ApiStatsEmployee getEmployeeStats(@RequestHeader("Authorization") String authorizationHeader) throws NotAllowedException {
        if (!securityService.isManager(authorizationHeader)) {
            throw new NotAllowedException();
        }
        return new ApiStatsEmployee(
                employeeRepository.count(),
                employeeRepository.countByRole(EmployeeRole.ADMIN),
                employeeRepository.countByRole(EmployeeRole.CLASSIC)
        );
    }
}
