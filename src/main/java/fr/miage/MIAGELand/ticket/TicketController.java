package fr.miage.MIAGELand.ticket;

import fr.miage.MIAGELand.api.ApiTicket;
import fr.miage.MIAGELand.api.stats.ApiStatsTicket;
import fr.miage.MIAGELand.security.NotAllowedException;
import fr.miage.MIAGELand.security.SecurityService;
import fr.miage.MIAGELand.stats.StatTicketInfoService;
import fr.miage.MIAGELand.stats.daily_ticket_info.DailyTicketInfoService;
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
@RequestMapping("/api")
public class TicketController {

    private final TicketRepository ticketRepository;
    private final TicketService ticketService;
    private final MonthlyTicketInfoService monthlyTicketInfoService;
    private final VisitorRepository visitorRepository;
    private final StatTicketInfoService statTicketInfoService;
    private final DailyTicketInfoService dailyTicketInfoService;
    private final SecurityService securityService;

    /**
     * Get ticket by id
     * @param id Ticket id
     * @return ApiTicket
     */
    @GetMapping("/tickets/{id}")
    public ApiTicket getTicket(@PathVariable Long id, @RequestHeader("Authorization") String authorizationHeader) throws NotAllowedException {
        if (!securityService.isEmployee(authorizationHeader)) {
            throw new NotAllowedException();
        }
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

    /**
     * Get all tickets
     * @param authorizationHeader Authorization header
     * @return List of ApiTicket
     * @throws NotAllowedException If the user is not an employee
     */
    @GetMapping("/tickets/all")
    public List<ApiTicket> getAllTickets(@RequestHeader("Authorization") String authorizationHeader) throws NotAllowedException {
        if (!securityService.isEmployee(authorizationHeader)) {
            throw new NotAllowedException();
        }
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
     * Update ticket state to used or paid
     * @param body Body of the request (state)
     * @param authorizationHeader Authorization header (for validation)
     * @return ApiTicket
     */
    @PatchMapping("/tickets/{id}")
    public ApiTicket updateTicket(@PathVariable Long id, @RequestBody Map<String, String> body,
                                  @RequestHeader(value = "Authorization", required = false) String authorizationHeader) throws TicketNotValidException, NotAllowedException {
        if (!body.containsKey("state")) {
            throw new IllegalArgumentException("State is required");
        } else {
            Ticket ticket = ticketRepository.findById(id).orElseThrow();
            switch (Enum.valueOf(TicketState.class, body.get("state"))) {
                case PAID -> ticketService.payTicket(ticket);
                case USED -> ticketService.validateTicket(ticket, authorizationHeader);
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
     * Create the tickets in database and return them
     * @param ticketsData Body of the request (tickets data)
     * @return List of ApiTicket
     */
    @PostMapping("/tickets")
    public List<ApiTicket> createTickets(@RequestBody Map<String, Map<String, String>> ticketsData) {
        List<Ticket> tickets = new ArrayList<>();
        List<Visitor> newVisitors = new ArrayList<>();
        for (Map<String, String> ticketData : ticketsData.values()) {

            String name = ticketData.get("name");
            String surname = ticketData.get("surname");
            String email = ticketData.get("email");
            LocalDate date = DateConverter.convertLocalDate(ticketData.get("date"));
            ticketService.validateDate(date);

            long dailyTicketCount = ticketService.getDailyTicketCount(date);
            ticketService.validateGauge(dailyTicketCount);

            float price = Float.parseFloat(ticketData.get("price"));

            Visitor visitor = visitorRepository.findByEmail(email);
            Ticket ticket;
            if (visitor == null) {
                Visitor newVisitor = new Visitor(name, surname, email);
                newVisitors.add(newVisitor);
                ticket = new Ticket(newVisitor, date, price, TicketState.RESERVED);
                tickets.add(ticket);
            } else {
                ticket = new Ticket(visitor, date, price, TicketState.RESERVED);
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

    /**
     * Get stats for tickets
     * @param start Start date
     * @param end End date
     * @return ApiStatsTicket
     */
    @GetMapping("/tickets/stats")
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

    /**
     * Get visitor tickets by visitor id
     * @param id Visitor id
     * @return List of ApiTicket
     */
    @GetMapping("/visitors/{id}/tickets")
    public List<ApiTicket> getVisitorTickets(@PathVariable Long id) {
        Visitor visitor = visitorRepository.findById(id).orElseThrow();
        if (visitor.getTicketList().isEmpty()) {
            return List.of();
        }
        return visitor.getTicketList().stream().map(
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
     * Get all tickets paginated
     * @param page Page number
     * @param authorizationHeader Authorization header
     * @return List of ApiTicket
     * @throws NotAllowedException If the user is not an employee
     */
    @GetMapping("/tickets")
    public List<ApiTicket> getTickets(
            @RequestParam(name="page", defaultValue = "0") int page,
            @RequestHeader("Authorization") String authorizationHeader) throws NotAllowedException
    {
        if (!securityService.isEmployee(authorizationHeader)) {
            throw new NotAllowedException();
        }
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
