package fr.miage.MIAGELand.api.stats;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@Getter
@Setter
public class ApiStatsTicket {
    private NumberStatsTicket numberStatsTicket;
    private List<MonthlyTicketInfos> monthlyTicketInfos;
}