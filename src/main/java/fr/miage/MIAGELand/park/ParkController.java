package fr.miage.MIAGELand.park;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@AllArgsConstructor
@RequestMapping("/api/park")
public class ParkController {

    private final ParkService parkService;
    private final ParkRepository parkRepository;
    @GetMapping
    public Park getPark() {
        if (parkRepository.count() == 0) {
            Park initPark = new Park(1L, 0L, 10L, java.time.LocalDateTime.now());
            parkRepository.save(initPark);
        }
        return parkRepository.findById(1L).orElseThrow();
    }
    @PatchMapping("/gauge")
    public Park updateGauge(@RequestBody Map<String, String> body) throws IllegalGaugeException {
        if (!body.containsKey("gauge")) {
            throw new IllegalArgumentException("Gauge is required");
        } else {
            return parkService.setGauge(Long.parseLong(body.get("gauge")));
        }
    }
}
