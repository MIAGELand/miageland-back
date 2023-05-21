package fr.miage.MIAGELand.api;

import fr.miage.MIAGELand.ticket.TicketState;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
@Setter
public class ApiTicket {
    private Long id;
    private TicketState state;
    private float price;
    private LocalDate date;
    private String nameVisitor;
    private long idVisitor;
}
