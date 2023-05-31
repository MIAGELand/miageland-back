package fr.miage.MIAGELand.api.stats;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.YearMonth;

/**
 * This class is used to represent the monthly stats of the tickets in the API.
 */
@AllArgsConstructor
@Getter
@Setter
public class MonthlyTicketInfos {

    private YearMonth monthYear;
    private NumberStatsTicket numberStatsTicket;
    private double totalAmount;
    private double benefits;
}
