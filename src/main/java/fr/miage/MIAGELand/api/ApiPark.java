package fr.miage.MIAGELand.api;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class ApiPark {
    private Long id;
    private Long minTicketGauge;
    private Long gauge;
    private LocalDateTime modifiedAt;
}
