package fr.miage.MIAGELand.attraction;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AttractionService {

    private AttractionRepository attractionRepository;

    /**
     * Check if the attraction fields are valid.
     * @param attraction The attraction to check
     * @return true if the attraction fields are valid, false otherwise
     */
    public boolean isValidAttractionField(Attraction attraction) {
        return attraction.getName() != null;
    }

    /**
     * Update the state of an attraction.
     * @param attraction The attraction to update
     * @param state The state to set
     * @throws AttractionStateException If the attraction needs to be set to the same state
     */
    public void updateState(Attraction attraction, boolean state) throws AttractionStateException {
        boolean isOpened = attraction.isOpened();
        if (isOpened == state) {
            throw new AttractionStateException("Attraction is already " + (isOpened ? "opened" : "closed"));
        }
        attraction.setOpened(state);
        attractionRepository.save(attraction);
    }
}
