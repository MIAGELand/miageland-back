package fr.miage.MIAGELand.visitor;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Setter
@Getter
@NoArgsConstructor
@Table(uniqueConstraints = { @UniqueConstraint(name = "UniqueNameAndSurname", columnNames = { "name", "surname" }) })
public class Visitor {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "visitor_generator")
    @SequenceGenerator(name = "visitor_generator", sequenceName = "visitor_seq", allocationSize = 1)
    private Long id;
    private String name;
    private String surname;
    private String email;

    public Visitor(String name, String surname) {
        this.name = name;
        this.surname = surname;
    }

    public Visitor(String name, String surname, String email) {
        this.name = name;
        this.surname = surname;
        this.email = email;
    }
}
