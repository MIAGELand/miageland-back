package fr.miage.MIAGELand.api.stats;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * This class is used to represent the stats of the employee in the API.
 */
@AllArgsConstructor
@Getter
@Setter
public class ApiStatsEmployee {

    private long nbTotal;
    private long nbAdmin;
    private long nbClassic;
}
