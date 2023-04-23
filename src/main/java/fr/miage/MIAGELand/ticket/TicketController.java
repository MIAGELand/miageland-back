package fr.miage.MIAGELand.ticket;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
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

    @GetMapping("")
    public List<Ticket> getAllTickets() {
        return ticketRepository.findAll();
    }

    /**
     * Update ticket state to used
     * @param nbTicket
     * @return Ticket
     */
    @PatchMapping("/{nbTicket}")
    public Ticket updateTicket(@PathVariable Long nbTicket) throws TicketNotValidException {
        Ticket ticket = ticketRepository.findByNbTicket(nbTicket);
        ticketService.validateTicket(ticket);
        return ticketRepository.save(ticket);
    }

    /**
     * Create a ticket
     * @param body
     * @return Ticket
     */
    @PostMapping("")
    public Ticket createTicket (@RequestBody Map<String, String> body) {
        return ticketService.generateTicket(body.get("name"), body.get("surname"), LocalDateTime.parse(body.get("date")), Float.parseFloat(body.get("price")));
    }
}
