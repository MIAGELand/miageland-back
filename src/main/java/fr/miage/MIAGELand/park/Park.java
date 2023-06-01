package fr.miage.MIAGELand.park;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Park entity
 */
@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Park {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "park_generator")
    @SequenceGenerator(name = "park_generator", sequenceName = "park_seq", allocationSize = 1)
    private Long id;
    private Long minTicketGauge;
    private Long gauge;
    private LocalDateTime modifiedAt;
}
