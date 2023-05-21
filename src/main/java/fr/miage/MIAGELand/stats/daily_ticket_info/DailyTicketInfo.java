package fr.miage.MIAGELand.stats.daily_ticket_info;

import fr.miage.MIAGELand.stats.TicketInfoData;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Setter
@Getter
@NoArgsConstructor
public class DailyTicketInfo {

    @Id
    private String dayMonthYear;
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
