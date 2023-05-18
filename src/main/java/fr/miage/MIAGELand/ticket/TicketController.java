package fr.miage.MIAGELand.ticket;

import fr.miage.MIAGELand.api.ApiTicket;
import fr.miage.MIAGELand.api.stats.ApiStatsTicket;
import fr.miage.MIAGELand.utils.DateConverter;
import lombok.AllArgsConstructor;
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

    /**
     * Get ticket by id
     * @param id
     * @return Ticket
     */
    @GetMapping("/{id}")
    public Ticket getTicket(@PathVariable Long id) {
        return ticketRepository.findById(id).orElseThrow();
    }

    @GetMapping()
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
    public List<Ticket> createTicket (@RequestBody Map<String, Map<String, String>> body) {
        List<Ticket> tickets = new ArrayList<>();
        for (Map.Entry<String, Map<String, String>> entry : body.entrySet()) {
            Map<String, String> value = entry.getValue();
            Ticket ticket = ticketService.generateTicket(
                    value.get("name"),
                    value.get("surname"),
                    DateConverter.convertFakerDate(value.get("date")),
                    Float.parseFloat(value.get("price"))
            );
            tickets.add(ticket);
        }
        return tickets;
    }

    @GetMapping("/stats")
    public ApiStatsTicket getStats() {
        return new ApiStatsTicket(
                ticketRepository.count(),
                ticketRepository.countByState(TicketState.PAID),
                ticketRepository.countByState(TicketState.USED),
                ticketRepository.countByState(TicketState.CANCELLED)
        );
    }

}
