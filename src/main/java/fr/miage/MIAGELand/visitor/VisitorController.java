package fr.miage.MIAGELand.visitor;

import fr.miage.MIAGELand.api.ApiTicket;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@AllArgsConstructor
@RequestMapping("/api/visitors")

public class VisitorController {

    private final VisitorRepository visitorRepository;

    @GetMapping()
    public Visitor getVisitor(@RequestParam String name, @RequestParam String surname) {
        Visitor visitor = visitorRepository.findByNameAndSurname(name, surname);
        if (visitor == null) {
            throw new IllegalArgumentException("Visitor not found");
        } else {
            return visitor;
        }
    }
    @PostMapping
    public Visitor createVisitor(@RequestBody Map<String, String> body) {
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
            return visitor;
        }
    }

    @GetMapping("/{id}/tickets")
    public List<ApiTicket> getVisitorTickets(@PathVariable Long id) {
        Visitor visitor = visitorRepository.findById(id).orElseThrow();
        System.out.println(visitor.getTicketList());
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
}
