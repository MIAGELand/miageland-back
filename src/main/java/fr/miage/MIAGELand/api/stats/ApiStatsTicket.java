package fr.miage.MIAGELand.api.stats;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class ApiStatsTicket {
    private long nbTotal;
    private long nbPaid;
    private long nbUsed;
    private long nbCancelled;
}
