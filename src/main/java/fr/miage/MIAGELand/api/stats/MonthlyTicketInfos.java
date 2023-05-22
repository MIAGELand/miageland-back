package fr.miage.MIAGELand.api.stats;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.YearMonth;

@AllArgsConstructor
@Getter
@Setter
public class MonthlyTicketInfos {

    private YearMonth monthYear;
    private NumberStatsTicket numberStatsTicket;
    private double totalAmount;
    private double benefits;
}
