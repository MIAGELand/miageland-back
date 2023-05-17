package fr.miage.MIAGELand.attraction;

import fr.miage.MIAGELand.employee.Employee;
import fr.miage.MIAGELand.employee.EmployeeRepository;
import fr.miage.MIAGELand.employee.EmployeeRole;
import fr.miage.MIAGELand.security.Headers;
import fr.miage.MIAGELand.security.NotAllowedException;
import fr.miage.MIAGELand.security.SecurityService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import fr.miage.MIAGELand.security.Headers.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@AllArgsConstructor
@RequestMapping("/api/attractions")
public class AttractionController {

    private final AttractionRepository attractionRepository;
    private final AttractionService attractionService;
    private final EmployeeRepository employeeRepository;
    private final SecurityService securityService;

    /**
     * Get attraction by id
     * @param id
     * @return Attraction
     */
    @GetMapping("/{id}")
    public Attraction getAttraction(@PathVariable Long id) throws AttractionNotFoundException {
        return attractionRepository.findById(id).orElseThrow(() -> new AttractionNotFoundException(id));
    }

    @GetMapping("")
    public List<Attraction> getAllAttractions() {
        return attractionRepository.findAll();
    }

    @PostMapping()
    public List<Attraction> createAttraction(@RequestBody Map<String, Map<String, String>> body) {
        List<Attraction> attractions = new ArrayList<>();
        for (Map.Entry<String, Map<String, String>> entry : body.entrySet()) {
            Map<String, String> value = entry.getValue();
            Attraction attraction = new Attraction(
                    value.get("name"),
                    Boolean.parseBoolean(value.get("opened"))
            );
            attractions.add(attraction);
        }
        return attractionRepository.saveAll(attractions);
    }

    @DeleteMapping("/{id}")
    @Transactional
    public void removeAttraction(@PathVariable Long id, @RequestHeader("Authorization") String authorizationHeader) throws NotAllowedException {
        try {
            securityService.isAdmin(authorizationHeader);
        } catch (NotAllowedException e) {
            throw new NotAllowedException();
        }
        attractionRepository.deleteById(id);
    }

    @PatchMapping("/{id}")
    public Attraction updateAttraction(@PathVariable Long id, @RequestBody Map<String, String> body, @RequestHeader("Authorization") String authorizationHeader) throws AttractionNotFoundException, AttractionStateException, NotAllowedException {
        try {
            securityService.isAdmin(authorizationHeader);
            Attraction attraction = attractionRepository.findById(id).orElseThrow(() -> new AttractionNotFoundException(id));
            switch (body.get("opened")) {
                case "true" -> attractionService.updateState(attraction, true);
                case "false" -> attractionService.updateState(attraction, false);
                default -> throw new AttractionStateException("Invalid state.");
            }
            return attractionRepository.save(attraction);
        } catch (NotAllowedException e) {
            throw new NotAllowedException();
        }
    }
}
