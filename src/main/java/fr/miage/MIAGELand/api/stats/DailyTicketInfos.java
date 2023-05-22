package fr.miage.MIAGELand.api.stats;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@AllArgsConstructor
@Getter
@Setter
public class DailyTicketInfos {

    private LocalDate dayMonthYear;
    private NumberStatsTicket numberStatsTicket;
    private double totalAmount;
    private double benefits;
}
