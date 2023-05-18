package fr.miage.MIAGELand.api.stats;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class ApiStatsEmployee {

    private long nbTotal;
    private long nbAdmin;
    private long nbClassic;
}
