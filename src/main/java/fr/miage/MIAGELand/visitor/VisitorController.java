package fr.miage.MIAGELand.visitor;

import fr.miage.MIAGELand.api.ApiTicket;
import fr.miage.MIAGELand.api.ApiVisitor;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@AllArgsConstructor
@RequestMapping("/api/visitors")

public class VisitorController {

    private final VisitorRepository visitorRepository;

    @GetMapping("/{email}")
    public ApiVisitor getVisitor(@PathVariable String email) {
        Visitor visitor = visitorRepository.findByEmail(email);
        if (visitor == null) {
            throw new IllegalArgumentException("Visitor not found");
        } else {
            return new ApiVisitor(
                    visitor.getId(),
                    visitor.getName(),
                    visitor.getSurname(),
                    visitor.getEmail(),
                    visitor.getTicketList().stream().map(
                            ticket -> new ApiTicket(
                                    ticket.getId(),
                                    ticket.getState(),
                                    ticket.getPrice(),
                                    ticket.getDate(),
                                    ticket.getVisitor().getName(),
                                    ticket.getVisitor().getId()
                            )
                    ).toList()
            );
        }
    }
    @PostMapping
    public ApiVisitor createVisitor(@RequestBody Map<String, String> body) {
        if (!body.containsKey("name")
            || !body.containsKey("surname")
            || !body.containsKey("email")) {
            throw new IllegalArgumentException("Missing parameters");
        } else {
            Visitor visitor = new Visitor(
                    body.get("name"),
                    body.get("surname"),
                    body.get("email")
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

    @DeleteMapping("/{id}")
    public void deleteVisitor(@PathVariable Long id) {
        Visitor visitor = visitorRepository.findById(id).orElseThrow();
        visitorRepository.delete(visitor);
    }
}
