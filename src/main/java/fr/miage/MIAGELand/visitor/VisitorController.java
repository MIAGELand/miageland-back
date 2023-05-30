package fr.miage.MIAGELand.visitor;

import fr.miage.MIAGELand.api.ApiTicket;
import fr.miage.MIAGELand.api.ApiVisitor;
import fr.miage.MIAGELand.ticket.Ticket;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/visitors")

public class VisitorController {

    private final VisitorRepository visitorRepository;
    private final VisitorService visitorService;


    @GetMapping("/{email}")
    public ApiVisitor getVisitor(@PathVariable String email) {
        Visitor visitor = visitorRepository.findByEmail(email);
        if (visitor == null) {
            throw new IllegalArgumentException("Visitor not found");
        } else {
            List<ApiTicket> apiTickets = null;
            List<Ticket> ticketList = visitor.getTicketList();
            if (ticketList != null) {
                apiTickets = ticketList.stream().map(
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
            return new ApiVisitor(
                    visitor.getId(),
                    visitor.getName(),
                    visitor.getSurname(),
                    visitor.getEmail(),
                    apiTickets
            );
        }
    }
    @PostMapping
    public ApiVisitor createVisitor(@RequestBody Visitor body) {
        if (!visitorService.isVisitorFieldValid(body)) {
            throw new IllegalArgumentException("Missing parameters");
        } else {
            Visitor visitor = new Visitor(
                    body.getName(),
                    body.getSurname(),
                    body.getEmail()
            );
            visitorRepository.save(visitor);
            return new ApiVisitor(
                    visitor.getId(),
                    visitor.getName(),
                    visitor.getSurname(),
                    visitor.getEmail()
            );
        }
    }

    @GetMapping("/{id}/tickets")
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
     * Delete visitor by id
     * TODO : check state of tickets before deleting
     * @param id
     */
    @DeleteMapping("/{id}")
    public void deleteVisitor(@PathVariable Long id) {
        Visitor visitor = visitorRepository.findById(id).orElseThrow();
        visitorRepository.delete(visitor);
    }
}
