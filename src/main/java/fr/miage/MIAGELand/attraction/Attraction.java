package fr.miage.MIAGELand.attraction;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Attraction entity
 */
@Entity
@Setter
@Getter
@NoArgsConstructor
public class Attraction {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "attraction_generator")
    @SequenceGenerator(name = "attraction_generator", sequenceName = "attraction_seq", allocationSize = 1)
    private Long id;
    private String name;
    private boolean opened;

    public Attraction(String name, boolean opened) {
        this.name = name;
        this.opened = opened;
    }
}
