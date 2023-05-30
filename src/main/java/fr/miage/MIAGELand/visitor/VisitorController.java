package fr.miage.MIAGELand.visitor;

import fr.miage.MIAGELand.api.ApiTicket;
import fr.miage.MIAGELand.api.ApiVisitor;
import fr.miage.MIAGELand.api.stats.ApiStatsTicket;
import fr.miage.MIAGELand.api.stats.ApiStatsVisitor;
import fr.miage.MIAGELand.security.NotAllowedException;
import fr.miage.MIAGELand.security.SecurityService;
import fr.miage.MIAGELand.ticket.Ticket;
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
@RequestMapping("/api/visitors")

public class VisitorController {

    private final VisitorRepository visitorRepository;
    private final VisitorService visitorService;
    private final SecurityService securityService;

    @GetMapping("")
    public List<ApiVisitor> getVisitors(
            @RequestParam(name="page", defaultValue = "0") int page,
            @RequestHeader("Authorization") String authorizationHeader) throws NotAllowedException
    {
        if (!securityService.isEmployee(authorizationHeader)) {
            throw new NotAllowedException();
        }
        Page<Visitor> visitors = visitorService.getVisitors(page);
        return visitors.stream().map(
                visitor -> new ApiVisitor(
                        visitor.getId(),
                        visitor.getName(),
                        visitor.getSurname(),
                        visitor.getEmail()
                )
        ).toList();
    }

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
    public List<ApiVisitor> createVisitor(@RequestBody Map<String, Visitor> body) {
        System.out.println(body);
        List<Visitor> visitors = new ArrayList<>();
        for (Visitor visitor : body.values()) {
            if (!visitorService.isVisitorFieldValid(visitor)) {
                throw new IllegalArgumentException("Missing parameters");
            } else {
                Visitor current = new Visitor(
                        visitor.getName(),
                        visitor.getSurname(),
                        visitor.getEmail()
                );
                visitors.add(current);
            }
        }
        visitorRepository.saveAll(visitors);
        return visitors.stream().map(
                visitor -> new ApiVisitor(
                        visitor.getId(),
                        visitor.getName(),
                        visitor.getSurname(),
                        visitor.getEmail(),
                        null
                )
        ).toList();
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

    @GetMapping("/stats")
    public ApiStatsVisitor getStats(@RequestHeader("Authorization") String authorizationHeader) throws NotAllowedException {
        if (!securityService.isEmployee(authorizationHeader)) {
            throw new NotAllowedException();
        }
        return new ApiStatsVisitor(visitorRepository.count());
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
