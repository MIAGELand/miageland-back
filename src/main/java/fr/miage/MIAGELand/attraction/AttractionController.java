package fr.miage.MIAGELand.attraction;

import fr.miage.MIAGELand.api.ApiAttraction;
import fr.miage.MIAGELand.api.stats.ApiStatsAttraction;
import fr.miage.MIAGELand.security.NotAllowedException;
import fr.miage.MIAGELand.security.SecurityService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@AllArgsConstructor
@RequestMapping("/api/attractions")
public class AttractionController {

    private final AttractionRepository attractionRepository;
    private final AttractionService attractionService;
    private final SecurityService securityService;

    /**
     * Get attraction by id
     * @param id
     * @return Attraction
     */
    @GetMapping("/{id}")
    public ApiAttraction getAttraction(@PathVariable Long id) {
        Attraction attraction = attractionRepository.findById(id).orElseThrow();
        return new ApiAttraction(attraction.getId(), attraction.getName(), attraction.isOpened());
    }

    @GetMapping("")
    public List<ApiAttraction> getAllAttractions() {
        return attractionRepository.findAll().stream().map(
                attraction -> new ApiAttraction(
                        attraction.getId(),
                        attraction.getName(),
                        attraction.isOpened()
                )
        ).toList();
    }

    @PostMapping()
    public List<ApiAttraction> createAttraction(@RequestBody Map<String, Map<String, String>> body,
                                             @RequestHeader("Authorization") String authorizationHeader) throws NotAllowedException {
        if (!securityService.isAdminOrManager(authorizationHeader)) {
            throw new NotAllowedException();
        }
        List<Attraction> attractions = new ArrayList<>();
        for (Map.Entry<String, Map<String, String>> entry : body.entrySet()) {
            Map<String, String> value = entry.getValue();
            Attraction attraction = new Attraction(
                    value.get("name"),
                    Boolean.parseBoolean(value.get("opened"))
            );
            attractions.add(attraction);
        }
        return attractionRepository.saveAll(attractions).stream().map(
                attraction -> new ApiAttraction(
                        attraction.getId(),
                        attraction.getName(),
                        attraction.isOpened()
                )
        ).toList();
    }

    @DeleteMapping("/{id}")
    @Transactional
    public void removeAttraction(@PathVariable Long id, @RequestHeader("Authorization") String authorizationHeader) throws NotAllowedException {
        if (!securityService.isAdminOrManager(authorizationHeader)) {
            throw new NotAllowedException();
        }
        attractionRepository.deleteById(id);
    }

    @PatchMapping("/{id}")
    public ApiAttraction updateAttraction(@PathVariable Long id, @RequestBody Map<String, String> body, @RequestHeader("Authorization")
    String authorizationHeader) throws AttractionStateException, NotAllowedException {
        if (!securityService.isAdminOrManager(authorizationHeader)) {
            throw new NotAllowedException();
        }
        Attraction attraction = attractionRepository.findById(id).orElseThrow();
        switch (body.get("opened")) {
            case "true" -> attractionService.updateState(attraction, true);
            case "false" -> attractionService.updateState(attraction, false);
            default -> throw new AttractionStateException("Invalid state.");
        }
        return new ApiAttraction(attraction.getId(), attraction.getName(), attraction.isOpened());
    }

    @GetMapping("/stats")
    public ApiStatsAttraction getAttractionStats() {
        return new ApiStatsAttraction(
                attractionRepository.count(),
                attractionRepository.countByOpened(true),
                attractionRepository.countByOpened(false)
        );
    }
}
