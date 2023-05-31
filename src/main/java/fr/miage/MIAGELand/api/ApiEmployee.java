package fr.miage.MIAGELand.api;

import fr.miage.MIAGELand.employee.EmployeeRole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * This class is used to represent an employee in the API.
 */
@Getter
@Setter
@AllArgsConstructor
public class ApiEmployee {

    private Long id;
    private String name;
    private String surname;
    private String email;
    private EmployeeRole role;
}
