package fr.miage.MIAGELand.visitor;

import fr.miage.MIAGELand.ticket.Ticket;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Visitor entity
 * @see Ticket
 */
@Entity
@Setter
@Getter
@NoArgsConstructor
@Table(uniqueConstraints = { @UniqueConstraint(name = "UniqueEmail", columnNames = { "email" }) })
public class Visitor {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "visitor_generator")
    @SequenceGenerator(name = "visitor_generator", sequenceName = "visitor_seq", allocationSize = 1)
    private Long id;
    private String name;
    private String surname;
    private String email;
    @OneToMany(targetEntity = Ticket.class, mappedBy = "visitor")
    private List<Ticket> ticketList;

    public Visitor(String name, String surname, String email) {
        this.name = name;
        this.surname = surname;
        this.email = email;
    }
}
