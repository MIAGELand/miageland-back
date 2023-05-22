package fr.miage.MIAGELand.stats.daily_ticket_info;

import fr.miage.MIAGELand.api.stats.DailyTicketInfos;
import fr.miage.MIAGELand.api.stats.NumberStatsTicket;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class DailyTicketInfoService {

    private final DailyTicketInfoRepository dailyTicketInfoRepository;

    /**
     * Get the global stats ticket
     * @return
     */
    public NumberStatsTicket getGlobalStatsTicket() {
        if (dailyTicketInfoRepository.count() == 0) {
            return new NumberStatsTicket(0L, 0L, 0L, 0L, 0L);
        }
        return new NumberStatsTicket(
                dailyTicketInfoRepository.getAllTickets(),
                dailyTicketInfoRepository.getAllReservedTickets(),
                dailyTicketInfoRepository.getAllPaidTickets(),
                dailyTicketInfoRepository.getAllUsedTickets(),
                dailyTicketInfoRepository.getAllCancelledTickets()
        );
    }

    /**
     * Get the daily stats ticket
     * @return
     */
    public List<DailyTicketInfos> getDailyTicketInfos() {
        if (dailyTicketInfoRepository.count() == 0) {
            return null;
        }
        List<DailyTicketInfo> dailyTicketInfos = dailyTicketInfoRepository.findAll();

        return dailyTicketInfos.stream()
                .map(dailyTicketInfo -> new DailyTicketInfos(
                        dailyTicketInfo.getDayMonthYear(),
                        new NumberStatsTicket(
                                dailyTicketInfo.getTicketCount(),
                                dailyTicketInfo.getTicketReservedCount(),
                                dailyTicketInfo.getTicketPaidCount(),
                                dailyTicketInfo.getTicketUsedCount(),
                                dailyTicketInfo.getTicketCancelledCount()
                        ),
                        dailyTicketInfo.getTotalPrice(),
                        dailyTicketInfo.getBenefits()
                ))
                .collect(Collectors.toList());
    }
}
