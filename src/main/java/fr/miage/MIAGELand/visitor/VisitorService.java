package fr.miage.MIAGELand.visitor;

import org.springframework.stereotype.Service;

@Service
public class VisitorService {

    public boolean isVisitorFieldValid(Visitor visitor) {
        return visitor.getName() != null && visitor.getSurname() != null && visitor.getEmail() != null;
    }
}
