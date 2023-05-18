package fr.miage.MIAGELand.ticket;

import fr.miage.MIAGELand.api.stats.MonthlyTicketInfos;
import fr.miage.MIAGELand.api.stats.NumberStatsTicket;
import fr.miage.MIAGELand.visitor.Visitor;
import fr.miage.MIAGELand.visitor.VisitorRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;
    private final VisitorRepository visitorRepository;

    // TODO : check if gauge is not exceeded
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

    public List<MonthlyTicketInfos> getMonthlyTicketInfos() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/yy");
        Map<YearMonth, Long> ticketCountByDate =
                ticketRepository.findAll().stream()
                        .collect(Collectors.groupingBy(
                        ticket -> YearMonth.from(ticket.getDate()),
                        Collectors.counting())
                );
        // Create the MonthlyTicketInfos grouped by MM/YY found before
        return ticketCountByDate.keySet().stream()
                .map(monthDate -> new MonthlyTicketInfos(
                        monthDate.format(formatter),
                        new NumberStatsTicket(
                                ticketRepository.countAllByDateBetween(
                                        monthDate.atDay(1).atStartOfDay(),
                                        monthDate.atEndOfMonth().atTime(23, 59, 59)
                                ),
                                ticketRepository.countAllByDateBetweenAndState(
                                        monthDate.atDay(1).atStartOfDay(),
                                        monthDate.atEndOfMonth().atTime(23, 59, 59),
                                        TicketState.PAID
                                ),
                                ticketRepository.countAllByDateBetweenAndState(
                                        monthDate.atDay(1).atStartOfDay(),
                                        monthDate.atEndOfMonth().atTime(23, 59, 59),
                                        TicketState.USED
                                ),
                                ticketRepository.countAllByDateBetweenAndState(
                                        monthDate.atDay(1).atStartOfDay(),
                                        monthDate.atEndOfMonth().atTime(23, 59, 59),
                                        TicketState.CANCELLED
                                )
                        )
                ))
                .collect(Collectors.toList());
    }

    public NumberStatsTicket getGlobalStatsTicket() {
        return new NumberStatsTicket(
                ticketRepository.count(),
                ticketRepository.countByState(TicketState.PAID),
                ticketRepository.countByState(TicketState.USED),
                ticketRepository.countByState(TicketState.CANCELLED)
        );
    }

}
