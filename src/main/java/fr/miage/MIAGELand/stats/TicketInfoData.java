package fr.miage.MIAGELand.stats;

import fr.miage.MIAGELand.ticket.Ticket;
import lombok.Getter;
import lombok.Setter;

/**
 * Ticket info data
 * Used to store ticket info
 * @see Ticket
 */
@Getter
@Setter
public class TicketInfoData {
    private Double totalPrice = 0D;
    private Long ticketCount = 0L;
    public void update(Ticket ticket) {
        totalPrice += ticket.getPrice();
        ticketCount++;
    }
}
