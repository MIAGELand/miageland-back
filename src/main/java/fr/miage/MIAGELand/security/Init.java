package fr.miage.MIAGELand.security;

import fr.miage.MIAGELand.employee.Employee;
import fr.miage.MIAGELand.employee.EmployeeRole;
import fr.miage.MIAGELand.park.Park;
import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class Init {

    public static List<Employee> createManagers() {
        Employee ptorguet = new Employee(
                "Patrice",
                "Torguet",
                "ptorguet",
                EmployeeRole.MANAGER
        );
        Employee cteyssie = new Employee(
                "Cedric",
                "Teyssie",
                "cteyssie",
                EmployeeRole.MANAGER
        );
        Employee jdetrier = new Employee(
                "Jonathan",
                "Detrier",
                "jdetrier",
                EmployeeRole.MANAGER
        );
        return List.of(ptorguet, cteyssie, jdetrier);
    }

    public static Park createPark() {
        return new Park(
                1L,
                10L,
                10L,
                java.time.LocalDateTime.now()
        );
    }
}
