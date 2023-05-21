package fr.miage.MIAGELand.api;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@Getter
@Setter
public class ApiVisitor {
    private Long id;
    private String name;
    private String surname;
    private List<ApiTicket> ticketList;

    public ApiVisitor(Long id, String name, String surname) {
        this.id = id;
        this.name = name;
        this.surname = surname;
    }
}
