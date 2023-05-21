package fr.miage.MIAGELand.ticket;

import fr.miage.MIAGELand.visitor.Visitor;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Setter
@Getter
@NoArgsConstructor
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ticket_generator")
    @SequenceGenerator(name = "ticket_generator", sequenceName = "ticket_seq", allocationSize = 1)
    private Long id;
    @ManyToOne(targetEntity = Visitor.class)
    private Visitor visitor;
    private LocalDate date;
    private float price;
    private TicketState state;

    public Ticket(Visitor visitor, LocalDate date, float price, TicketState reserved) {
        this.visitor = visitor;
        this.date = date;
        this.price = price;
        this.state = reserved;
    }
}
