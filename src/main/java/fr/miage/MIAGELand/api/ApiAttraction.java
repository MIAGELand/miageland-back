package fr.miage.MIAGELand.api;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class ApiAttraction {
    private Long id;
    private String name;
    private boolean opened;
}
