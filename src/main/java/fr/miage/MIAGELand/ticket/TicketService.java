package fr.miage.MIAGELand.ticket;

import fr.miage.MIAGELand.stats.monthly_ticket_info.MonthlyTicketInfoService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@AllArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;
    private final MonthlyTicketInfoService monthlyTicketInfoService;
    private static final int DEFAULT_PAGE_SIZE = 100;

    public void payTicket(Ticket ticket) throws TicketNotValidException {
        // Check if date is today or after
        boolean isDateValid = ticket.getDate().isAfter(LocalDate.now().minusDays(1));
        if (!isDateValid) {
            throw new TicketNotValidException("Ticket date is not valid.");
        }
        TicketState previousState = ticket.getState();
        switch (previousState) {
            case RESERVED -> {
                ticket.setState(TicketState.PAID);
                monthlyTicketInfoService.updateTicketInfo(ticket,false, previousState);
            }
            case PAID -> throw new TicketNotValidException("Ticket already paid.");
            case USED -> throw new TicketNotValidException("Ticket already used.");
            case CANCELLED -> throw new TicketNotValidException("Ticket cancelled.");
        }
    }

    public void validateTicket(Ticket ticket) throws TicketNotValidException {
        TicketState previousState = ticket.getState();
        switch (previousState) {
            case PAID -> {
                ticket.setState(TicketState.USED);
                monthlyTicketInfoService.updateTicketInfo(ticket,false, previousState);
            }
            case RESERVED -> throw new TicketNotValidException("Ticket not paid.");
            case USED -> throw new TicketNotValidException("Ticket already used.");
            case CANCELLED -> throw new TicketNotValidException("Ticket cancelled.");
        }
    }

    public void cancelTicket(Ticket ticket) throws TicketNotValidException {
        boolean isDateValid = ticket.getDate().isAfter(LocalDate.now().plusDays(7));
        TicketState previousState = ticket.getState();
        if (isDateValid) {
            switch (previousState) {
                case PAID, RESERVED -> {
                    ticket.setState(TicketState.CANCELLED);
                    monthlyTicketInfoService.updateTicketInfo(ticket,false, previousState);
                }
                case USED -> throw new TicketNotValidException("Ticket already used.");
                case CANCELLED -> throw new TicketNotValidException("Ticket cancelled.");
            }
        } else {
            throw new TicketNotValidException("Ticket cannot be cancelled. Date invalid.");
        }
    }

    public List<Ticket> getAllTicketsNextDays() {
        return ticketRepository.findAllByDateAfter(LocalDate.now());
    }

    public Page<Ticket> getTickets(int pageNumber) {
        Pageable pageable = PageRequest.of(pageNumber, DEFAULT_PAGE_SIZE);
        return ticketRepository.findAll(pageable);
    }


}
