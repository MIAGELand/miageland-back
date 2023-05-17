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
        return parkRepository.findById(1L).orElseThrow();
    }
    @PatchMapping("/gauge")
    public Park updateGauge(@RequestBody Map<String, String> body) throws IllegalGaugeException {
        if (!body.containsKey("gauge")) {
            throw new IllegalArgumentException("Gauge is required");
        } else {
            return parkService.setGauge(Integer.parseInt(body.get("gauge")));
        }
    }
}
