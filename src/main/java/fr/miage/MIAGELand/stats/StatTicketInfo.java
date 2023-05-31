package fr.miage.MIAGELand.stats;

import fr.miage.MIAGELand.stats.daily_ticket_info.DailyTicketInfo;
import fr.miage.MIAGELand.stats.monthly_ticket_info.MonthlyTicketInfo;
import lombok.Getter;
import lombok.Setter;

/**
 * StatTicketInfo class
 * This is used to store the statistics of the tickets and to send them to the front.
 * This class is used for monthly and daily statistics.
 * @see DailyTicketInfo
 * @see MonthlyTicketInfo
 */
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
