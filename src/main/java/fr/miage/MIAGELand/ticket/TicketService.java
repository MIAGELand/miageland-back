package fr.miage.MIAGELand.ticket;

import fr.miage.MIAGELand.api.stats.MonthlyTicketInfos;
import fr.miage.MIAGELand.api.stats.NumberStatsTicket;
import fr.miage.MIAGELand.stats.MonthlyTicketInfoService;
import fr.miage.MIAGELand.visitor.Visitor;
import fr.miage.MIAGELand.visitor.VisitorRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    public List<MonthlyTicketInfos> getMonthlyTicketInfos() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/yy");

        List<Ticket> ticketList = ticketRepository.findAll();

        Map<YearMonth, Long> ticketCountByDate = new HashMap<>();
        Map<YearMonth, Double> ticketPriceByDate = new HashMap<>();
        Map<YearMonth, Double> ticketNotCancelledCountByDate = new HashMap<>();

        for (Ticket ticket : ticketList) {
            YearMonth yearMonth = YearMonth.from(ticket.getDate());

            ticketCountByDate.put(yearMonth, ticketCountByDate.getOrDefault(yearMonth, 0L) + 1);
            ticketPriceByDate.put(yearMonth, ticketPriceByDate.getOrDefault(yearMonth, 0.0) + ticket.getPrice());

            if (!ticket.getState().equals(TicketState.CANCELLED)) {
                ticketNotCancelledCountByDate.put(yearMonth, ticketNotCancelledCountByDate.getOrDefault(yearMonth, 0.0) + ticket.getPrice());
            }
        }

        return ticketCountByDate.entrySet().stream()
                .map(entry -> {
                    YearMonth monthDate = entry.getKey();
                    Long ticketCount = entry.getValue();
                    Double ticketPrice = ticketPriceByDate.get(monthDate);
                    Double ticketNotCancelledCount = ticketNotCancelledCountByDate.get(monthDate);

                    return new MonthlyTicketInfos(
                            monthDate.format(formatter),
                            new NumberStatsTicket(
                                    ticketCount,
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
                            ),
                            ticketPrice,
                            ticketNotCancelledCount
                    );
                })
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
