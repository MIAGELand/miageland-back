package fr.miage.MIAGELand.employee;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "employee_generator")
    @SequenceGenerator(name = "employee_generator", sequenceName = "employee_seq", allocationSize = 1)
    private Long id;
    private String name;
    private String surname;
    private String email;
    private EmployeeRole role;

    public Employee(String name, String surname, String email, EmployeeRole role) {
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.role = role;
    }
}
