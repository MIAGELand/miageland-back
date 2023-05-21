package fr.miage.MIAGELand.api.stats;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class NumberStatsTicket {
    private long nbTotal;
    private long nbReserved;
    private long nbPaid;
    private long nbUsed;
    private long nbCancelled;
}
