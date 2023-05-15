package fr.miage.MIAGELand.ticket;

import fr.miage.MIAGELand.visitor.Visitor;
import fr.miage.MIAGELand.visitor.VisitorRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;
    private final VisitorRepository visitorRepository;
    public Ticket generateTicket(String name, String surname,
                                 LocalDateTime date,
                                 float price) {

        Visitor visitor = visitorRepository.findByNameAndSurname(name, surname);
        if (visitor == null) {
            Visitor newVisitor = new Visitor(name, surname);
            visitorRepository.save(newVisitor);
            Ticket ticket = new Ticket(newVisitor, date, price, TicketState.PAID);
            return ticketRepository.save(ticket);
        } else {
            Ticket ticket = new Ticket(visitor, date, price, TicketState.PAID);
            return ticketRepository.save(ticket);
        }
    }

    public void validateTicket(Ticket ticket) throws TicketNotValidException {
        switch (ticket.getState()) {
            case PAID -> ticket.setState(TicketState.USED);
            case RESERVED -> throw new TicketNotValidException("Ticket not paid.");
            case USED -> throw new TicketNotValidException("Ticket already used.");
            case CANCELLED -> throw new TicketNotValidException("Ticket cancelled.");
        }
    }

    public void cancelTicket(Ticket ticket) throws TicketNotValidException {
        boolean isDateValid = ticket.getDate().isAfter(LocalDateTime.now().plusDays(7));
        if (isDateValid) {
            switch (ticket.getState()) {
                case PAID, RESERVED -> ticket.setState(TicketState.CANCELLED);
                case USED -> throw new TicketNotValidException("Ticket already used.");
                case CANCELLED -> throw new TicketNotValidException("Ticket cancelled.");
            }
        } else {
            throw new TicketNotValidException("Ticket cannot be cancelled. Date invalid.");
        }
    }

    public List<Ticket> getAllTicketsNextDays() {
        return ticketRepository.findAllByDateAfter(LocalDateTime.now());
    }
}
