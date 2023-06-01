package fr.miage.MIAGELand.stats.daily_ticket_info;

import fr.miage.MIAGELand.api.stats.DailyTicketInfos;
import fr.miage.MIAGELand.api.stats.NumberStatsTicket;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Daily ticket info service
 * Handle all daily ticket info related business logic
 */
@Service
@AllArgsConstructor
public class DailyTicketInfoService {

    private final DailyTicketInfoRepository dailyTicketInfoRepository;

    /**
     * Get the daily stats ticket
     * @return List of DailyTicketInfos
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

    /**
     * Get the daily stats ticket between two dates
     * @param start Start date
     * @param end End date
     * @return List of DailyTicketInfos
     */
    public List<DailyTicketInfos> getDailyTicketInfos(LocalDate start, LocalDate end) {
        if (dailyTicketInfoRepository.count() == 0) {
            return null;
        }
        List<DailyTicketInfo> dailyTicketInfos = dailyTicketInfoRepository.findAllByDayMonthYearBetween(start, end);

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
