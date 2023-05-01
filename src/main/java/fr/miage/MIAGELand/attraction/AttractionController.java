package fr.miage.MIAGELand.attraction;

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
    public List<Attraction> createEmployee(@RequestBody  Map<String, Map<String, String>> body) {
        System.out.println(body);
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
}
