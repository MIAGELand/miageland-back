package fr.miage.MIAGELand.ticket;

import fr.miage.MIAGELand.api.ApiTicket;
import fr.miage.MIAGELand.api.stats.ApiStatsTicket;
import fr.miage.MIAGELand.stats.MonthlyTicketInfoRepository;
import fr.miage.MIAGELand.stats.MonthlyTicketInfoService;
import fr.miage.MIAGELand.utils.DateConverter;
import fr.miage.MIAGELand.visitor.Visitor;
import fr.miage.MIAGELand.visitor.VisitorRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
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

    /**
     * Get ticket by id
     * @param id
     * @return Ticket
     */
    @GetMapping("/{id}")
    public Ticket getTicket(@PathVariable Long id) {
        return ticketRepository.findById(id).orElseThrow();
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
    public Ticket updateTicket(@PathVariable Long id, @RequestBody Map<String, String> body) throws TicketNotValidException {
        if (!body.containsKey("state")) {
            throw new IllegalArgumentException("State is required");
        } else {
            Ticket ticket = ticketRepository.findById(id).orElseThrow();
            switch (Enum.valueOf(TicketState.class, body.get("state"))) {
                case USED -> ticketService.validateTicket(ticket);
                case CANCELLED -> ticketService.cancelTicket(ticket);
                default -> throw new IllegalArgumentException("State is not valid");
            }
            return ticketRepository.save(ticket);
        }
    }

    /**
     * Create the tickets in database
     * @param body
     * @return Ticket
     */
    @PostMapping("")
    public List<Ticket> createTickets(@RequestBody Map<String, Map<String, String>> ticketsData) {
        List<Ticket> tickets = new ArrayList<>();
        List<Visitor> newVisitors = new ArrayList<>();

        for (Map<String, String> ticketData : ticketsData.values()) {
            String name = ticketData.get("name");
            String surname = ticketData.get("surname");
            LocalDateTime date = DateConverter.convertFakerDate(ticketData.get("date"));
            float price = Float.parseFloat(ticketData.get("price"));

            Visitor visitor = visitorRepository.findByNameAndSurname(name, surname);
            Visitor newVisitor;
            if (visitor == null) {
                newVisitor = new Visitor(name, surname);
                newVisitors.add(newVisitor);
                Ticket ticket = new Ticket(newVisitor, date, price, TicketState.PAID);
                tickets.add(ticket);
            } else {
                Ticket ticket = new Ticket(visitor, date, price, TicketState.PAID);
                tickets.add(ticket);
            }
        }

        if (!newVisitors.isEmpty()) {
            visitorRepository.saveAll(newVisitors);
        }

        ticketRepository.saveAll(tickets);

        monthlyTicketInfoService.updateTicketListInfo(tickets);

        return tickets;
    }

    @GetMapping("/stats")
    public ApiStatsTicket getStats() {
        return new ApiStatsTicket(
                monthlyTicketInfoService.getGlobalStatsTicket(),
                monthlyTicketInfoService.getMonthlyTicketInfos()
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
