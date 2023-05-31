package fr.miage.MIAGELand.park;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ParkService {

    private final ParkRepository parkRepository;

    /**
     * Set the gauge of the park for the upcoming days.
     * @param gauge The gauge to set
     * @throws IllegalGaugeException If the gauge is not valid
     */
    public void setGauge(long gauge) throws IllegalGaugeException {
        Park park = parkRepository.findById(1L).orElseThrow();
        long minParkGauge = park.getMinTicketGauge();
        if (gauge < minParkGauge) {
            throw new IllegalGaugeException("Gauge must be at least " + minParkGauge + " for the upcoming days.");
        } else {
            park.setGauge(gauge);
            park.setModifiedAt(java.time.LocalDateTime.now());
            parkRepository.save(park);
        }
    }

}
