package fr.miage.MIAGELand.api.stats;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

/**
 * This class is used to represent the daily stats of the tickets in the API.
 */
@AllArgsConstructor
@Getter
@Setter
public class DailyTicketInfos {

    private LocalDate dayMonthYear;
    private NumberStatsTicket numberStatsTicket;
    private double totalAmount;
    private double benefits;
}
