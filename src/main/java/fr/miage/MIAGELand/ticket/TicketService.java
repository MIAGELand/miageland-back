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
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

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
                statTicketInfoService.updateTicketInfo(ticket,false, previousState);
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
                statTicketInfoService.updateTicketInfo(ticket,false, previousState);
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
                    statTicketInfoService.updateTicketInfo(ticket,false, previousState);
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

    public void validateDate(LocalDate date) {
        if (date.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Date is not valid");
        }
    }

    public void validateGauge(long gauge) {
        long currentGauge = parkRepository.findById(1L).get().getGauge();
        if (currentGauge == 0) {
            throw new IllegalArgumentException("Gauge is exceeded");
        }
    }

    public long getDailyTicketCount(LocalDate date) {
        DailyTicketInfo dailyTicketInfo = dailyTicketInfoRepository.findByDayMonthYear(date);
        if (dailyTicketInfo != null) {
            long dailyTicketCount = dailyTicketInfo.getTicketCount();
            long currentGauge = parkRepository.findById(1L).get().getGauge();
            if (dailyTicketCount + 1 > currentGauge) {
                throw new IllegalArgumentException("Gauge is exceeded");
            }
            return dailyTicketCount;
        }
        return 0;
    }

}
