package fr.miage.MIAGELand.ticket;

import fr.miage.MIAGELand.api.ApiTicket;
import fr.miage.MIAGELand.utils.DateConverter;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

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
     * Get ticket by nbTicket
     * @param nbTicket
     * @return Ticket
     */
    @GetMapping("/{nbTicket}")
    public Ticket getTicket(@PathVariable Long nbTicket) {
        return ticketRepository.findByNbTicket(nbTicket);
    }

    @GetMapping()
    public List<ApiTicket> getAllTickets() {
        List<ApiTicket> apiTickets = new ArrayList<>();
        for (Ticket ticket : ticketRepository.findAll()) {
            apiTickets.add(new ApiTicket(
                    ticket.getNbTicket(),
                    ticket.getState(),
                    ticket.getPrice(),
                    ticket.getDate(),
                    ticket.getVisitor().getName(),
                    ticket.getVisitor().getId()
            ));
        }
        return apiTickets;
    }

    /**
     * Update ticket state to used
     * @param nbTicket
     * @return Ticket
     */
    @PatchMapping("/{nbTicket}")
    public Ticket updateTicket(@PathVariable Long nbTicket, @RequestBody Map<String, String> body) throws TicketNotValidException {
        System.out.println(body);
        if (!body.containsKey("state")) {
            throw new IllegalArgumentException("State is required");
        } else {
            Ticket ticket = ticketRepository.findByNbTicket(nbTicket);
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
}
