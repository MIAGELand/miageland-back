package fr.miage.MIAGELand.park;

import fr.miage.MIAGELand.ticket.Ticket;
import fr.miage.MIAGELand.ticket.TicketService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ParkService {

    private final ParkRepository parkRepository;
    private final TicketService ticketService;

    public Park setGauge(int gauge) {
        Park park = parkRepository.findById(1L).orElseThrow();

        // Get the minimum gauge = nb minimum of visitors for the upcoming days
        List<Ticket> ticketList = ticketService.getAllTicketsNextDays();

        // Calculate the minimum gauge needed based on the number of tickets per date
        Map<LocalDateTime, Long> ticketCountByDate = ticketList.stream()
                .collect(Collectors.groupingBy(Ticket::getDate, Collectors.counting()));
        int minGauge = ticketCountByDate.values().stream().mapToInt(Long::intValue).max().orElseThrow();
        if (gauge < minGauge) {
            throw new IllegalArgumentException("Gauge cannot be lower than " + minGauge);
        } else {
            park.setGauge(gauge);
            park.setModifiedAt(java.time.LocalDateTime.now());
            return parkRepository.save(park);
        }
    }

}
