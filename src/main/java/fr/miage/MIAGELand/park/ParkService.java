package fr.miage.MIAGELand.park;

import fr.miage.MIAGELand.ticket.Ticket;
import fr.miage.MIAGELand.ticket.TicketService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ParkService {

    private final ParkRepository parkRepository;
    private final TicketService ticketService;

    public Park setGauge(int gauge) throws IllegalGaugeException {
        Park park = parkRepository.findById(1L).orElseThrow();

        // Get the minimum gauge = nb minimum of visitors for the upcoming days
        List<Ticket> ticketList = ticketService.getAllTicketsNextDays();

        // Calculate the minimum gauge needed based on the number of tickets per date
        Map<LocalDateTime, Long> ticketCountByDate = ticketList.stream()
                .collect(Collectors.groupingBy(ticket -> ticket.getDate().truncatedTo(ChronoUnit.DAYS), Collectors.counting()));
        int minGauge = ticketCountByDate.values().stream().mapToInt(Long::intValue).max().orElseThrow();
        if (gauge < minGauge) {
            throw new IllegalGaugeException("Gauge must be at least " + minGauge + " for the upcoming days.");
        } else {
            park.setGauge(gauge);
            park.setModifiedAt(java.time.LocalDateTime.now());
            return parkRepository.save(park);
        }
    }

}
