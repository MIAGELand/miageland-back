package fr.miage.MIAGELand.ticket;

import fr.miage.MIAGELand.visitor.Visitor;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Setter
@Getter
@NoArgsConstructor
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long nbTicket;
    @ManyToOne(targetEntity = Visitor.class)
    private Visitor visitor;
    private LocalDateTime date;
    private float price;
    private TicketState state;

    public Ticket(Visitor visitor, LocalDateTime date, float price, TicketState reserved) {
        this.visitor = visitor;
        this.date = date;
        this.price = price;
        this.state = reserved;
    }
}
