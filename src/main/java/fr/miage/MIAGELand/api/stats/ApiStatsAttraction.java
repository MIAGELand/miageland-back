package fr.miage.MIAGELand.api.stats;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * This class is used to represent the stats of the attraction in the API.
 */
@AllArgsConstructor
@Getter
@Setter
public class ApiStatsAttraction {

    private long nbTotal;
    private long nbOpened;
    private long nbClosed;
}
