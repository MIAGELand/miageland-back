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

/**
 * Attraction controller
 * Handle all attraction related requests
 */
@RestController
@AllArgsConstructor
@RequestMapping("/api/attractions")
public class AttractionController {

    private final AttractionRepository attractionRepository;
    private final AttractionService attractionService;
    private final SecurityService securityService;

    /**
     * Get attraction by id
     * @param id id of the attraction
     * @return ApiAttraction
     */
    @GetMapping("/{id}")
    public ApiAttraction getAttraction(@PathVariable Long id) {
        Attraction attraction = attractionRepository.findById(id).orElseThrow();
        return new ApiAttraction(attraction.getId(), attraction.getName(), attraction.isOpened());
    }

    /**
     * Get all attractions
     * @return List of ApiAttraction
     */
    @GetMapping
    public List<ApiAttraction> getAllAttractions() {
        return attractionRepository.findAll().stream().map(
                attraction -> new ApiAttraction(
                        attraction.getId(),
                        attraction.getName(),
                        attraction.isOpened()
                )
        ).toList();
    }

    /**
     * Get all attractions with stats
     * @param body Map of attractions
     * @param authorizationHeader Authorization header
     * @return List of ApiStatsAttraction
     * @throws NotAllowedException If the user is not allowed to access this resource
     * @throws AttractionStateException If the attraction is in an invalid state
     */
    @PostMapping
    public List<ApiAttraction> createAttraction(@RequestBody Map<String, Attraction> body,
                                             @RequestHeader("Authorization") String authorizationHeader) throws NotAllowedException, AttractionStateException {
        if (!securityService.isAdminOrManager(authorizationHeader)) {
            throw new NotAllowedException();
        }
        List<Attraction> attractions = new ArrayList<>();
        for (Attraction attraction : body.values()) {
            if (!attractionService.isValidAttractionField(attraction)) {
                throw new AttractionStateException("Invalid state.");
            }
            Attraction current = new Attraction(
                    attraction.getName(),
                    attraction.isOpened()
            );
            attractions.add(current);
        }
        return attractionRepository.saveAll(attractions).stream().map(
                attraction -> new ApiAttraction(
                        attraction.getId(),
                        attraction.getName(),
                        attraction.isOpened()
                )
        ).toList();
    }

    /**
     * Delete attraction by id (only for admin or manager)
     * @param id id of the attraction
     * @param authorizationHeader Authorization header
     * @throws NotAllowedException If the user is not allowed to access this resource
     */
    @DeleteMapping("/{id}")
    @Transactional
    public void removeAttraction(@PathVariable Long id, @RequestHeader("Authorization") String authorizationHeader) throws NotAllowedException {
        if (!securityService.isAdminOrManager(authorizationHeader)) {
            throw new NotAllowedException();
        }
        attractionRepository.deleteById(id);
    }

    /**
     * Update attraction by id (only for admin or manager)
     * Set the attraction to opened or closed
     * @param id id of the attraction
     * @param body Map of the attraction
     * @param authorizationHeader Authorization header
     * @return ApiAttraction updated
     * @throws AttractionStateException If the attraction is in an invalid state
     * @throws NotAllowedException If the user is not allowed to access this resource
     */
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

    /**
     * Get stats of the attractions
     * @param authorizationHeader Authorization header
     * @return ApiStatsAttraction
     * @throws NotAllowedException If the user is not allowed to access this resource
     */
    @GetMapping("/stats")
    public ApiStatsAttraction getAttractionStats(@RequestHeader("Authorization") String authorizationHeader) throws NotAllowedException {
        if (!securityService.isAdminOrManager(authorizationHeader)) {
            throw new NotAllowedException();
        }
        return new ApiStatsAttraction(
                attractionRepository.count(),
                attractionRepository.countByOpened(true),
                attractionRepository.countByOpened(false)
        );
    }
}
