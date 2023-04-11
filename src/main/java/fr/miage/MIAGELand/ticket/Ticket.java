package fr.miage.MIAGELand.ticket;

import fr.miage.MIAGELand.visitor.Visitor;
import jakarta.persistence.*;
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
    @Id
    @ManyToOne(targetEntity = Visitor.class)
    private Visitor visitor;
    private LocalDateTime date;
    private float price;
    private TicketState state;
}
