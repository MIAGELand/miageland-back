package fr.miage.MIAGELand.api.stats;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class DailyTicketInfos {

    private String dayMonthYear;
    private NumberStatsTicket numberStatsTicket;
    private double totalAmount;
    private double benefits;
}
