package fr.miage.MIAGELand.stats;

import fr.miage.MIAGELand.ticket.Ticket;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TicketInfoData {
    private Double totalPrice = 0D;
    private Long ticketCount = 0L;
    private Double benefits = 0D;
    public void update(Ticket ticket) {
        totalPrice += ticket.getPrice();
        benefits += ticket.getPrice();
        ticketCount++;
    }
}
