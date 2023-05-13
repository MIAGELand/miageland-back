package fr.miage.MIAGELand.attraction;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AttractionService {

    private AttractionRepository attractionRepository;

    public void updateState(Attraction attraction, boolean state) throws AttractionStateException {
        boolean isOpened = attraction.isOpened();
        if (isOpened == state) {
            throw new AttractionStateException("Attraction is already " + (isOpened ? "opened" : "closed"));
        }
        attraction.setOpened(state);
        attractionRepository.save(attraction);
    }
}
