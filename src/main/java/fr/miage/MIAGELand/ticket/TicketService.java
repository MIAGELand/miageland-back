package fr.miage.MIAGELand.ticket;

import fr.miage.MIAGELand.visitor.Visitor;
import fr.miage.MIAGELand.visitor.VisitorRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;
    private final VisitorRepository visitorRepository;
    public Ticket generateTicket(String name, String surname,
                                 LocalDateTime date,
                                 float price) {

        // TODO : CHANGE BY SEARCHING IN DB AND RETURNING THE VISITOR
        Visitor visitor = new Visitor(name, surname);
        visitorRepository.save(visitor);

        Ticket ticket = new Ticket(visitor, date, price, TicketState.PAID);

        return ticketRepository.save(ticket);
    }

    public void validateTicket(Ticket ticket) {
        switch (ticket.getState()) {
            case PAID -> ticket.setState(TicketState.USED);
            case USED -> throw new IllegalStateException("Ticket already used");
            case CANCELLED -> throw new IllegalStateException("Ticket already cancelled");
        }
    }

}
