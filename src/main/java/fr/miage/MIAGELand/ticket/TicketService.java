package fr.miage.MIAGELand.ticket;

import fr.miage.MIAGELand.stats.MonthlyTicketInfoService;
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
    private final MonthlyTicketInfoService monthlyTicketInfoService;

    // TODO : check if gauge is not exceeded
    public Ticket generateTicket(String name, String surname, LocalDateTime date, float price) {
        Visitor visitor = visitorRepository.findByNameAndSurname(name, surname);
        Visitor newVisitor;
        Ticket ticket;

        if (visitor == null) {
            newVisitor = new Visitor(name, surname);
            visitorRepository.save(newVisitor);
            ticket = new Ticket(newVisitor, date, price, TicketState.PAID);
            ticketRepository.save(ticket);
        } else {
            ticket = new Ticket(visitor, date, price, TicketState.PAID);
            ticketRepository.save(ticket);
        }

        monthlyTicketInfoService.updateTicketInfo(ticket,true);
        return ticket;
    }

    public void validateTicket(Ticket ticket) throws TicketNotValidException {
        TicketState previousState = ticket.getState();
        switch (previousState) {
            case PAID -> {
                ticket.setState(TicketState.USED);
                monthlyTicketInfoService.updateTicketInfo(ticket,false);
            }
            case RESERVED -> throw new TicketNotValidException("Ticket not paid.");
            case USED -> throw new TicketNotValidException("Ticket already used.");
            case CANCELLED -> throw new TicketNotValidException("Ticket cancelled.");
        }
    }

    public void cancelTicket(Ticket ticket) throws TicketNotValidException {
        boolean isDateValid = ticket.getDate().isAfter(LocalDateTime.now().plusDays(7));
        TicketState previousState = ticket.getState();
        if (isDateValid) {
            switch (previousState) {
                case PAID, RESERVED -> {
                    ticket.setState(TicketState.CANCELLED);
                    monthlyTicketInfoService.updateTicketInfo(ticket,false);
                }
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
