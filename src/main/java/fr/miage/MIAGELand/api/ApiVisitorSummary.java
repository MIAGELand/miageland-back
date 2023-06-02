package fr.miage.MIAGELand.api;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * This class is used to represent a visitor in the API.
 */
@AllArgsConstructor
@Getter
@Setter
public class ApiVisitorSummary {
    private Long id;
    private String name;
    private String surname;
    private String email;
    private long nbTicket;
}
