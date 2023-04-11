package fr.miage.MIAGELand.ticket;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

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

    /**
     * Update ticket state to used
     * @param nbTicket
     * @return Ticket
     */
    @PutMapping("/{nbTicket}")
    public Ticket updateTicket(@PathVariable Long nbTicket) {
        Ticket ticket = ticketRepository.findByNbTicket(nbTicket);
        ticketService.validateTicket(ticket);
        return ticketRepository.save(ticket);
    }

    /**
     * Create a ticket
     * @param name
     * @param surname
     * @param date
     * @param price
     * @return Ticket
     */
    @PostMapping()
    public Ticket createTicket(@RequestBody String name,
                               @RequestBody String surname,
                               @RequestBody LocalDateTime date,
                               @RequestBody float price
                               ) {
        return ticketService.generateTicket(name, surname, date, price);
    }
}
