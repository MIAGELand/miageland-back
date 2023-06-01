package fr.miage.MIAGELand.stats.monthly_ticket_info;

import fr.miage.MIAGELand.stats.StatTicketInfo;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.YearMonth;

/**
 * MonthlyTicketInfo class
 * This is used to store the statistics of the tickets and to send them to the front.
 * This class is used for monthly statistics.
 * @see StatTicketInfo
 * @see MonthlyTicketInfoData
 */
@Entity
@Setter
@Getter
@NoArgsConstructor
public class MonthlyTicketInfo extends StatTicketInfo {

    @Id
    private YearMonth monthYear;
    private Long ticketCount;
    private Long ticketReservedCount;
    private Long ticketPaidCount;
    private Long ticketUsedCount;
    private Long ticketCancelledCount;
    private Double totalPrice;
    private Double benefits;

    public void updateData(MonthlyTicketInfoData monthlyTicketInfoData) {
        ticketCount += monthlyTicketInfoData.getTicketCount();
        ticketReservedCount += monthlyTicketInfoData.getTicketCount();
        totalPrice += monthlyTicketInfoData.getTotalPrice();
    }
}
