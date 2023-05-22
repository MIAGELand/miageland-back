package fr.miage.MIAGELand.stats.monthly_ticket_info;

import fr.miage.MIAGELand.stats.StatTicketInfo;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Setter
@Getter
@NoArgsConstructor
public class MonthlyTicketInfo extends StatTicketInfo {

    @Id
    private String monthYear;
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
