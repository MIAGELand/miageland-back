package fr.miage.MIAGELand.api;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * This class is used to represent an attraction in the API.
 */
@AllArgsConstructor
@Getter
@Setter
public class ApiAttraction {
    private Long id;
    private String name;
    private boolean opened;
}
