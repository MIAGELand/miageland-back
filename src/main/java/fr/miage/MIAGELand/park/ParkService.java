package fr.miage.MIAGELand.park;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ParkService {

    private final ParkRepository parkRepository;

    public Park setGauge(long gauge) throws IllegalGaugeException {
        Park park = parkRepository.findById(1L).orElseThrow();
        long minParkGauge = park.getMinTicketGauge();
        // long calculateMinGauge = ticketService.calculateMinGauge();
        if (gauge < minParkGauge) {
            throw new IllegalGaugeException("Gauge must be at least " + minParkGauge + " for the upcoming days.");
        } else {
            park.setGauge(gauge);
            park.setModifiedAt(java.time.LocalDateTime.now());
            return parkRepository.save(park);
        }
    }

}
