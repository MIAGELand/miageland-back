package fr.miage.MIAGELand.ticket;

import fr.miage.MIAGELand.park.ParkRepository;
import fr.miage.MIAGELand.security.NotAllowedException;
import fr.miage.MIAGELand.security.SecurityService;
import fr.miage.MIAGELand.stats.StatTicketInfoService;
import fr.miage.MIAGELand.stats.daily_ticket_info.DailyTicketInfo;
import fr.miage.MIAGELand.stats.daily_ticket_info.DailyTicketInfoRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

/**
 * Ticket service
 * Handle all ticket related business logic
 */
@Service
@AllArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;
    private final StatTicketInfoService statTicketInfoService;
    private final SecurityService securityService;
    private final DailyTicketInfoRepository dailyTicketInfoRepository;
    private final ParkRepository parkRepository;
    private static final int DEFAULT_PAGE_SIZE = 100;

    /**
     * Update ticket state to PAID
     * @param ticket Ticket to pay
     * @throws TicketNotValidException If ticket is not valid (date before today, already paid, already used, cancelled)
     */
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
                statTicketInfoService.updateTicketInfoOnAction(ticket, previousState);
            }
            case PAID -> throw new TicketNotValidException("Ticket already paid.");
            case USED -> throw new TicketNotValidException("Ticket already used.");
            case CANCELLED -> throw new TicketNotValidException("Ticket cancelled.");
        }
    }

    /**
     * Update ticket state to USED
     * @param ticket Ticket to validate
     * @param authorization Authorization header
     * @throws TicketNotValidException If ticket is not valid (date before today, not paid, already used, cancelled)
     * @throws NotAllowedException If the user is not an employee
     */
    public void validateTicket(Ticket ticket, String authorization) throws TicketNotValidException, NotAllowedException {
        if (!securityService.isEmployee(authorization)) {
            throw new NotAllowedException();
        }
        TicketState previousState = ticket.getState();
        switch (previousState) {
            case PAID -> {
                ticket.setState(TicketState.USED);
                statTicketInfoService.updateTicketInfoOnAction(ticket, previousState);
            }
            case RESERVED -> throw new TicketNotValidException("Ticket not paid.");
            case USED -> throw new TicketNotValidException("Ticket already used.");
            case CANCELLED -> throw new TicketNotValidException("Ticket cancelled.");
        }
    }

    /**
     * Update ticket state to CANCELLED
     * @param ticket Ticket to cancel
     * @throws TicketNotValidException If ticket is not valid (date before today + 7 days, not paid, already used, cancelled)
     */
    public void cancelTicket(Ticket ticket) throws TicketNotValidException {
        boolean isDateValid = ticket.getDate().isAfter(LocalDate.now().plusDays(7));
        TicketState previousState = ticket.getState();
        if (isDateValid) {
            switch (previousState) {
                case PAID, RESERVED -> {
                    ticket.setState(TicketState.CANCELLED);
                    statTicketInfoService.updateTicketInfoOnAction(ticket, previousState);
                }
                case USED -> throw new TicketNotValidException("Ticket already used.");
                case CANCELLED -> throw new TicketNotValidException("Ticket cancelled.");
            }
        } else {
            throw new TicketNotValidException("Ticket cannot be cancelled. Date invalid.");
        }
    }

    /**
     * Get all tickets with pagination
     * @param pageNumber Page number
     * @return Page of tickets
     */
    public Page<Ticket> getTickets(int pageNumber) {
        Pageable pageable = PageRequest.of(pageNumber, DEFAULT_PAGE_SIZE);
        return ticketRepository.findAll(pageable);
    }

    public Page<Ticket> getTickets(int pageNumber, Specification<Ticket> specification) {
        Pageable pageable = PageRequest.of(pageNumber, DEFAULT_PAGE_SIZE);
        return ticketRepository.findAll(pageable, specification);
    }

    /**
     * Check if date is valid
     * @param date Date to check
     */
    public void validateDate(LocalDate date) {
        if (date.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Date is not valid");
        }
    }

    /**
     * Check if gauge is not exceeded
     * @param dailyTicketCount Daily ticket count
     */
    public void validateGauge(long dailyTicketCount) {
        long currentGauge = parkRepository.findById(1L).get().getGauge();
        if (dailyTicketCount + 1 > currentGauge) {
            throw new IllegalArgumentException("Gauge is exceeded");
        }
    }

    /**
     * Get daily ticket count for a specific date
     * @param date Date to check
     * @return Daily ticket count
     */
    public long getDailyTicketCount(LocalDate date) {
        DailyTicketInfo dailyTicketInfo = dailyTicketInfoRepository.findByDayMonthYear(date);
        if (dailyTicketInfo != null) {
            return dailyTicketInfo.getTicketCount();
        }
        return 0;
    }

}
