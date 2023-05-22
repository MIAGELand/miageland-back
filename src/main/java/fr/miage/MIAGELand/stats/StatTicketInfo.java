package fr.miage.MIAGELand.stats;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StatTicketInfo {

    private Long ticketCount;
    private Long ticketReservedCount;
    private Long ticketPaidCount;
    private Long ticketUsedCount;
    private Long ticketCancelledCount;
    private Double totalPrice;
    private Double benefits;
}
