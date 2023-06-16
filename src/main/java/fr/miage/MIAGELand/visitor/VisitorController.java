package fr.miage.MIAGELand.visitor;

import fr.miage.MIAGELand.api.ApiTicket;
import fr.miage.MIAGELand.api.ApiVisitor;
import fr.miage.MIAGELand.api.ApiVisitorSummary;
import fr.miage.MIAGELand.api.stats.ApiStatsVisitor;
import fr.miage.MIAGELand.security.NotAllowedException;
import fr.miage.MIAGELand.security.SecurityService;
import fr.miage.MIAGELand.ticket.Ticket;
import fr.miage.MIAGELand.ticket.TicketRepository;
import fr.miage.MIAGELand.utils.QueryUtils;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static fr.miage.MIAGELand.ticket.TicketState.PAID;

/**
 * Visitor controller
 * Handle all visitor related requests
 * @see Visitor
 */
@RestController
@AllArgsConstructor
@RequestMapping("/api/visitors")

public class VisitorController {

    private final VisitorRepository visitorRepository;
    private final TicketRepository ticketRepository;
    private final VisitorService visitorService;
    private final SecurityService securityService;

    /**
     * Get all visitors
     * @param page Page number
     * @param authorizationHeader Authorization header
     * @return List of ApiVisitorSummary
     * @throws NotAllowedException If the user is not an employee
     */
    @GetMapping
    public List<ApiVisitorSummary> getVisitors(
            @RequestParam(name="page", defaultValue = "0") int page,
            @RequestHeader("Authorization") String authorizationHeader) throws NotAllowedException
    {
        if (!securityService.isEmployee(authorizationHeader)) {
            throw new NotAllowedException();
        }
        Page<Visitor> visitors = visitorService.getVisitors(page);
        return visitors.stream().map(
                visitor -> new ApiVisitorSummary(
                        visitor.getId(),
                        visitor.getName(),
                        visitor.getSurname(),
                        visitor.getEmail(),
                        visitor.getTicketList().size()
                )
        ).toList();
    }

    /**
     * Get visitor by email
     * @param email Visitor email
     * @return ApiVisitor
     */
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

    /**
     * Create a visitor
     * @param body Is a map of visitors, in order to insert multiple visitor at once
     * @return List of ApiVisitor
     */
    @PostMapping
    public List<ApiVisitor> createVisitor(@RequestBody Map<String, Visitor> body) {
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
                if (visitorRepository.existsByEmail(visitor.getEmail())) {
                    throw new IllegalArgumentException("Visitor already exists");
                }
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

    /**
     * Get visitor stats
     * @param authorizationHeader Authorization header
     * @return ApiStatsVisitor
     * @throws NotAllowedException If the user is not an employee
     * <br>
     * Needs to be an employee since the VisitorPage on the Admin panel
     * is only accessible by employees (not only managers or admins)
     */
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
     * @param id Visitor id
     */

    public boolean checkStateTickets(@PathVariable long id) {
        Visitor visitor = visitorRepository.findById(id).orElseThrow();
        if (visitor == null) {
            throw new IllegalArgumentException("Visitor not found");
        } else {
            List<Ticket> ticketList = visitor.getTicketList();
            if (ticketList == null) {
                return true;
            }
            for (Ticket ticket : ticketList) {
                if (ticket.getState() == PAID) {
                    return false;
                }
            }
            ticketRepository.deleteAll(visitor.getTicketList());
            return true;
        }
    }

    @DeleteMapping("/{id}")
    public void deleteVisitor(@PathVariable Long id) throws Exception {
        if (!checkStateTickets(id)){
            throw new Exception("Error, there are paid tickets on your account");
        }
        Visitor visitor = visitorRepository.findById(id).orElseThrow();
        visitorRepository.delete(visitor);
    }

    /**
     * Get all visitors matching the given filters in the query parameters
     * @param params Query parameters
     * @param authorizationHeader Authorization header
     * @return List of ApiVisitorSummary
     * @throws NotAllowedException If the user is not an employee
     */
    @GetMapping("/search")
    public List<ApiVisitorSummary> getFilteredVisitorList(@RequestParam MultiValueMap<String, String> params,
                                                   @RequestHeader("Authorization") String authorizationHeader) throws NotAllowedException {
        if (!securityService.isEmployee(authorizationHeader)) {
            throw new NotAllowedException();
        }

        Specification<Visitor> spec = QueryUtils.buildSpecification(params, "ticket");

        return visitorRepository.findAll(spec).stream().map(
                visitor -> new ApiVisitorSummary(
                        visitor.getId(),
                        visitor.getName(),
                        visitor.getSurname(),
                        visitor.getEmail(),
                        visitor.getTicketList().size()
                )
        ).toList();

    }
}
