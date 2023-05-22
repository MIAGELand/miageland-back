package fr.miage.MIAGELand.park;

import fr.miage.MIAGELand.api.ApiPark;
import fr.miage.MIAGELand.security.NotAllowedException;
import fr.miage.MIAGELand.security.SecurityService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@AllArgsConstructor
@RequestMapping("/api/park")
public class ParkController {

    private final ParkService parkService;
    private final ParkRepository parkRepository;
    private final SecurityService securityService;
    @GetMapping
    public ApiPark getPark() {
        if (parkRepository.count() == 0) {
            Park initPark = new Park(1L, 0L, 10L, java.time.LocalDateTime.now());
            parkRepository.save(initPark);
        }
        Park park = parkRepository.findAll().get(0);
        return new ApiPark(park.getId(), park.getMinTicketGauge(), park.getGauge(), park.getModifiedAt());
    }
    @PatchMapping("/gauge")
    public ApiPark updateGauge(@RequestBody Map<String, String> body,
                            @RequestHeader("Authorization") String authorizationHeader)
            throws IllegalGaugeException, NotAllowedException {
        if (!securityService.isManager(authorizationHeader)) {
            throw new NotAllowedException();
        }
        if (!body.containsKey("gauge")) {
            throw new IllegalArgumentException("Gauge is required");
        } else {
            parkService.setGauge(Long.parseLong(body.get("gauge")));
            Park park = parkRepository.findById(1L).orElseThrow();
            return new ApiPark(park.getId(), park.getMinTicketGauge(), park.getGauge(), park.getModifiedAt());
        }
    }
}
