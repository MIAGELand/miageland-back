package fr.miage.MIAGELand.api;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * This class is used to represent a visitor in the API.
 */
@AllArgsConstructor
@Getter
@Setter
public class ApiVisitor {
    private Long id;
    private String name;
    private String surname;
    private String email;
    private List<ApiTicket> ticketList;
}
