package fr.miage.MIAGELand.visitor;

import fr.miage.MIAGELand.ticket.Ticket;
import fr.miage.MIAGELand.ticket.TicketRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;


import java.time.LocalDate;
import java.util.List;

import static fr.miage.MIAGELand.ticket.TicketState.PAID;

/**
 * Visitor service
 * Handle all visitor related business logic
 * @see Visitor
 */
@Service
@AllArgsConstructor
public class VisitorService {

    private final VisitorRepository visitorRepository;
    private final TicketRepository ticketRepository;
    private static final int DEFAULT_PAGE_SIZE = 100;

    /**
     * Check if a visitor is valid
     * @param visitor Visitor
     * @return True if the visitor is valid, false otherwise
     */
    public boolean isVisitorFieldValid(Visitor visitor) {
        return visitor.getName() != null && visitor.getSurname() != null && visitor.getEmail() != null;
    }

    public boolean checkStateTickets(@PathVariable long id) {
        Visitor visitor = visitorRepository.findById(id).orElseThrow();
        List<Ticket> ticketList = visitor.getTicketList();
        if (ticketList == null || ticketList.isEmpty()) {
            return true;
        }
        for (Ticket ticket : ticketList) {
            if (ticket.getState() == PAID && ticket.getDate().isBefore(LocalDate.now())) {
                return false;
            }
        }
        ticketRepository.deleteAll(visitor.getTicketList());
        return true;
    }

    /**
     * Get visitors with pagination (100 visitors per page by default)
     * @param pageNumber Page number
     * @return Visitors
     * @see Page
     * @see Pageable
     * @see PageRequest
     */
    public Page<Visitor> getVisitors(int pageNumber) {
        Pageable pageable = PageRequest.of(pageNumber, DEFAULT_PAGE_SIZE);
        return visitorRepository.findAll(pageable);
    }
}
