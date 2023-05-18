package fr.miage.MIAGELand.api.stats;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class ApiStatsAttraction {

    private long nbTotal;
    private long nbOpened;
    private long nbClosed;
}
