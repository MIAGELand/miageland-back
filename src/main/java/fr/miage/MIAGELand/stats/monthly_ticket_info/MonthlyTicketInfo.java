package fr.miage.MIAGELand.stats.monthly_ticket_info;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Setter
@Getter
@NoArgsConstructor
public class MonthlyTicketInfo {

    @Id
    private String monthYear;
    private Long ticketCount;
    private Long ticketPaidCount;
    private Long ticketUsedCount;
    private Long ticketCancelledCount;
    private Double totalPrice;
    private Double benefits;

    public void updateData(MonthlyTicketInfoData monthlyTicketInfoData) {
        ticketCount += monthlyTicketInfoData.getTicketCount();
        ticketPaidCount += monthlyTicketInfoData.getTicketCount();
        totalPrice += monthlyTicketInfoData.getTotalPrice();
        benefits += monthlyTicketInfoData.getBenefits();
    }
}