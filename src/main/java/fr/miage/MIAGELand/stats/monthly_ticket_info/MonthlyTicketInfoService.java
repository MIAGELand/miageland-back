package fr.miage.MIAGELand.stats.monthly_ticket_info;

import fr.miage.MIAGELand.api.stats.MonthlyTicketInfos;
import fr.miage.MIAGELand.api.stats.NumberStatsTicket;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class MonthlyTicketInfoService {
    private final MonthlyTicketInfoRepository monthlyTicketInfoRepository;

    /**
     * Get the global stats ticket
     * @return
     */
    public NumberStatsTicket getGlobalStatsTicket() {
        if (monthlyTicketInfoRepository.count() == 0) {
            return new NumberStatsTicket(0L, 0L, 0L, 0L, 0L);
        }
        return new NumberStatsTicket(
                monthlyTicketInfoRepository.getAllTickets(),
                monthlyTicketInfoRepository.getAllReservedTickets(),
                monthlyTicketInfoRepository.getAllPaidTickets(),
                monthlyTicketInfoRepository.getAllUsedTickets(),
                monthlyTicketInfoRepository.getAllCancelledTickets()
        );
    }

    /**
     * Get the monthly stats ticket
     * @return
     */
    public List<MonthlyTicketInfos> getMonthlyTicketInfos() {
        if (monthlyTicketInfoRepository.count() == 0) {
            return new ArrayList<>();
        }
        List<MonthlyTicketInfo> monthlyTicketInfos = monthlyTicketInfoRepository.findAll();

        return monthlyTicketInfos.stream()
                .map(monthlyTicketInfo -> new MonthlyTicketInfos(
                        monthlyTicketInfo.getMonthYear(),
                        new NumberStatsTicket(
                                monthlyTicketInfo.getTicketCount(),
                                monthlyTicketInfo.getTicketReservedCount(),
                                monthlyTicketInfo.getTicketPaidCount(),
                                monthlyTicketInfo.getTicketUsedCount(),
                                monthlyTicketInfo.getTicketCancelledCount()
                        ),
                        monthlyTicketInfo.getTotalPrice(),
                        monthlyTicketInfo.getBenefits()
                ))
                .collect(Collectors.toList());
    }
}

