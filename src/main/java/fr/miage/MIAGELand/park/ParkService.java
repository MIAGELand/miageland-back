package fr.miage.MIAGELand.park;

import fr.miage.MIAGELand.ticket.Ticket;
import fr.miage.MIAGELand.ticket.TicketService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ParkService {

    private final ParkRepository parkRepository;

    // UPDATE THE MIN TICKET GAUGE DEPENDING
    // ON THE NUMBER OF TICKETS MAX BOUGHT FOR THE UPCOMING DAYS
    public Park setGauge(long gauge) throws IllegalGaugeException {
        Park park = parkRepository.findById(1L).orElseThrow();
        long minParkGauge = park.getMinTicketGauge();
        // long calculateMinGauge = ticketService.calculateMinGauge();
        if (gauge < minParkGauge) {
            throw new IllegalGaugeException("Gauge must be at least " + minParkGauge + " for the upcoming days.");
        } else {
            park.setMinTicketGauge(gauge);
            park.setGauge(gauge);
            park.setModifiedAt(java.time.LocalDateTime.now());
            return parkRepository.save(park);
        }
    }

}
