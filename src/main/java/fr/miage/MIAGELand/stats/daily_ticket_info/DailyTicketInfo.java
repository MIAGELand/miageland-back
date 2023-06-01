package fr.miage.MIAGELand.stats.daily_ticket_info;

import fr.miage.MIAGELand.stats.StatTicketInfo;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/**
 * DailyTicketInfo class
 * This is used to store the statistics of the tickets and to send them to the front.
 * This class is used for daily statistics.
 * @see StatTicketInfo
 * @see DailyTicketInfoData
 */
@Entity
@Setter
@Getter
@NoArgsConstructor
public class DailyTicketInfo extends StatTicketInfo {

    @Id
    private LocalDate dayMonthYear;
    private Long ticketCount;
    private Long ticketReservedCount;
    private Long ticketPaidCount;
    private Long ticketUsedCount;
    private Long ticketCancelledCount;
    private Double totalPrice;
    private Double benefits;

    public void updateData(DailyTicketInfoData dailyTicketInfoData) {
        ticketCount += dailyTicketInfoData.getTicketCount();
        ticketReservedCount += dailyTicketInfoData.getTicketCount();
        totalPrice += dailyTicketInfoData.getTotalPrice();
    }
}
