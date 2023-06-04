package fr.miage.MIAGELand.api;

import fr.miage.MIAGELand.ticket.TicketState;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

/**
 * This class is used to represent a ticket in the API.
 */
@AllArgsConstructor
@Getter
@Setter
public class ApiTicket {
    private Long id;
    private TicketState state;
    private float price;
    private LocalDate date;
    private String visitorName;
    private long visitorId;
}
