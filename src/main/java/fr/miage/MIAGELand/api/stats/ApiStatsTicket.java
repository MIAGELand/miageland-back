package fr.miage.MIAGELand.api.stats;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * This class is used to represent the stats of the ticket in the API.
 */
@AllArgsConstructor
@Getter
@Setter
public class ApiStatsTicket {
    private NumberStatsTicket numberStatsTicket;
    private List<MonthlyTicketInfos> monthlyTicketInfos;
    private List<DailyTicketInfos> dailyTicketInfos;
}
