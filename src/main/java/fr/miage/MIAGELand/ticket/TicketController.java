package fr.miage.MIAGELand.ticket;

import fr.miage.MIAGELand.api.ApiTicket;
import fr.miage.MIAGELand.api.stats.ApiStatsTicket;
import fr.miage.MIAGELand.park.ParkRepository;
import fr.miage.MIAGELand.stats.StatTicketInfoService;
import fr.miage.MIAGELand.stats.daily_ticket_info.DailyTicketInfoRepository;
import fr.miage.MIAGELand.stats.daily_ticket_info.DailyTicketInfoService;
import fr.miage.MIAGELand.stats.monthly_ticket_info.MonthlyTicketInfoRepository;
import fr.miage.MIAGELand.stats.monthly_ticket_info.MonthlyTicketInfoService;
import fr.miage.MIAGELand.utils.DateConverter;
import fr.miage.MIAGELand.visitor.Visitor;
import fr.miage.MIAGELand.visitor.VisitorRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@AllArgsConstructor
@RequestMapping("/api/tickets")
public class TicketController {

    private final TicketRepository ticketRepository;
    private final TicketService ticketService;
    private final MonthlyTicketInfoService monthlyTicketInfoService;
    private final VisitorRepository visitorRepository;
    private final MonthlyTicketInfoRepository monthlyTicketInfoRepository;
    private final StatTicketInfoService statTicketInfoService;
    private final DailyTicketInfoService dailyTicketInfoService;
    private final DailyTicketInfoRepository dailyTicketInfoRepository;
    private final ParkRepository parkRepository;

    /**
     * Get ticket by id
     * @param id
     * @return Ticket
     */
    @GetMapping("/{id}")
    public ApiTicket getTicket(@PathVariable Long id) {
        Ticket ticket = ticketRepository.findById(id).orElseThrow();
        return new ApiTicket(
                ticket.getId(),
                ticket.getState(),
                ticket.getPrice(),
                ticket.getDate(),
                ticket.getVisitor().getName(),
                ticket.getVisitor().getId()
        );
    }

    @GetMapping("/all")
    public List<ApiTicket> getAllTickets() {
        return ticketRepository.findAll().stream().map(
                ticket -> new ApiTicket(
                        ticket.getId(),
                        ticket.getState(),
                        ticket.getPrice(),
                        ticket.getDate(),
                        ticket.getVisitor().getName(),
                        ticket.getVisitor().getId()
                )
        ).toList();
    }

    /**
     * Update ticket state to used
     * @param id
     * @return Ticket
     */
    @PatchMapping("/{id}")
    public ApiTicket updateTicket(@PathVariable Long id, @RequestBody Map<String, String> body) throws TicketNotValidException {
        if (!body.containsKey("state")) {
            throw new IllegalArgumentException("State is required");
        } else {
            Ticket ticket = ticketRepository.findById(id).orElseThrow();
            switch (Enum.valueOf(TicketState.class, body.get("state"))) {
                case PAID -> ticketService.payTicket(ticket);
                case USED -> ticketService.validateTicket(ticket);
                case CANCELLED -> ticketService.cancelTicket(ticket);
                default -> throw new IllegalArgumentException("State is not valid");
            }
            ticketRepository.save(ticket);
            return new ApiTicket(
                    ticket.getId(),
                    ticket.getState(),
                    ticket.getPrice(),
                    ticket.getDate(),
                    ticket.getVisitor().getName(),
                    ticket.getVisitor().getId()
            );
        }
    }

    /**
     * Create the tickets in database
     * TODO : check if gauge is not exceeded
     * @param body
     * @return Ticket
     */
    @PostMapping("")
    public List<ApiTicket> createTickets(@RequestBody Map<String, Map<String, String>> ticketsData) {
        List<Ticket> tickets = new ArrayList<>();
        List<Visitor> newVisitors = new ArrayList<>();

        for (Map<String, String> ticketData : ticketsData.values()) {
            String name = ticketData.get("name");
            String surname = ticketData.get("surname");
            String email = ticketData.get("email");
            LocalDate date = DateConverter.convertLocalDate(ticketData.get("date"));
            if (date.isBefore(LocalDate.now())) {
                throw new IllegalArgumentException("Date is not valid");
            }

            long dailyTicketCount;
            if (dailyTicketInfoRepository.findByDayMonthYear(date) != null) {
                dailyTicketCount = dailyTicketInfoRepository.findByDayMonthYear(date).getTicketCount();
                long currentGauge = parkRepository.findById(1L).get().getGauge();
                if (dailyTicketCount + 1 > currentGauge) {
                    throw new IllegalArgumentException("Gauge is exceeded");
                }
            }

            float price = Float.parseFloat(ticketData.get("price"));

            Visitor visitor = visitorRepository.findByEmail(email);
            Visitor newVisitor;
            if (visitor == null) {
                newVisitor = new Visitor(name, surname, email);
                newVisitors.add(newVisitor);
                Ticket ticket = new Ticket(newVisitor, date, price, TicketState.RESERVED);
                tickets.add(ticket);
            } else {
                Ticket ticket = new Ticket(visitor, date, price, TicketState.RESERVED);
                tickets.add(ticket);
            }
        }

        if (!newVisitors.isEmpty()) {
            visitorRepository.saveAll(newVisitors);
        }

        ticketRepository.saveAll(tickets);

        statTicketInfoService.updateTicketListInfo(tickets);

        return tickets.stream().map(
                ticket -> new ApiTicket(
                        ticket.getId(),
                        ticket.getState(),
                        ticket.getPrice(),
                        ticket.getDate(),
                        ticket.getVisitor().getName(),
                        ticket.getVisitor().getId()
                )
        ).toList();
    }

    @GetMapping("/stats")
    public ApiStatsTicket getStats(
            @RequestParam(required = false) String start,
            @RequestParam(required = false) String end) {
        // Check if there are no request params
        if (start == null && end == null) {
            return new ApiStatsTicket(
                    monthlyTicketInfoService.getGlobalStatsTicket(),
                    monthlyTicketInfoService.getMonthlyTicketInfos(),
                    dailyTicketInfoService.getDailyTicketInfos()
            );
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate startDate = LocalDate.parse(start, formatter);
        LocalDate endDate = LocalDate.parse(end, formatter);
        return new ApiStatsTicket(
                monthlyTicketInfoService.getGlobalStatsTicket(startDate, endDate),
                monthlyTicketInfoService.getMonthlyTicketInfos(startDate, endDate),
                dailyTicketInfoService.getDailyTicketInfos(startDate, endDate)
        );
    }

    @DeleteMapping("")
    public void deleteAllTickets() {
        monthlyTicketInfoRepository.deleteAll();
        ticketRepository.deleteAll();
    }

    @GetMapping("")
    public List<ApiTicket> getTickets(
            @RequestParam(name="page", defaultValue = "0") int page
    ) {
        Page<Ticket> tickets = ticketService.getTickets(page);
        return tickets.stream().map(
                ticket -> new ApiTicket(
                        ticket.getId(),
                        ticket.getState(),
                        ticket.getPrice(),
                        ticket.getDate(),
                        ticket.getVisitor().getName(),
                        ticket.getVisitor().getId()
                )
        ).toList();
    }

}
