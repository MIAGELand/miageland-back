package fr.miage.MIAGELand.stats.monthly_ticket_info;

import fr.miage.MIAGELand.api.stats.MonthlyTicketInfos;
import fr.miage.MIAGELand.api.stats.NumberStatsTicket;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Monthly ticket info service
 * Handle all monthly ticket info related business logic
 */
@Service
@AllArgsConstructor
public class MonthlyTicketInfoService {
    private final MonthlyTicketInfoRepository monthlyTicketInfoRepository;

    /**
     * Get the global stats ticket
     * @return List of MonthlyTicketInfos
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
     * Get the global stats ticket between two dates
     * @param start Start date
     * @param end End date
     * @return NumberStatsTicket
     */
    public NumberStatsTicket getGlobalStatsTicket(LocalDate start, LocalDate end) {
        if (monthlyTicketInfoRepository.count() == 0) {
            return new NumberStatsTicket(0L, 0L, 0L, 0L, 0L);
        }
        YearMonth startYearMonth = YearMonth.from(start);
        YearMonth endYearMonth = YearMonth.from(end);
        return new NumberStatsTicket(
                monthlyTicketInfoRepository.countAllByMonthYearBetween(startYearMonth, endYearMonth),
                monthlyTicketInfoRepository.countAllReservedByMonthYearBetween(startYearMonth, endYearMonth),
                monthlyTicketInfoRepository.countAllPaidByMonthYearBetween(startYearMonth, endYearMonth),
                monthlyTicketInfoRepository.countAllUsedByMonthYearBetween(startYearMonth, endYearMonth),
                monthlyTicketInfoRepository.countAllCancelledByMonthYearBetween(startYearMonth, endYearMonth)
        );
    }

    /**
     * Get the monthly stats ticket
     * @return List of MonthlyTicketInfos
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

    /**
     * Get the monthly stats ticket between two dates
     * @param start Start date
     * @param end End date
     * @return List of MonthlyTicketInfos
     */
    public List<MonthlyTicketInfos> getMonthlyTicketInfos(LocalDate start, LocalDate end) {
        if (monthlyTicketInfoRepository.count() == 0) {
            return new ArrayList<>();
        }
        YearMonth startYearMonth = YearMonth.from(start);
        YearMonth endYearMonth = YearMonth.from(end);
        List<MonthlyTicketInfo> monthlyTicketInfos = monthlyTicketInfoRepository.findAllByMonthYearBetween(startYearMonth, endYearMonth);

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

