package fr.miage.MIAGELand.stats.monthly_ticket_info;

import fr.miage.MIAGELand.api.stats.MonthlyTicketInfos;
import fr.miage.MIAGELand.api.stats.NumberStatsTicket;
import fr.miage.MIAGELand.ticket.Ticket;
import fr.miage.MIAGELand.ticket.TicketState;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static fr.miage.MIAGELand.ticket.TicketState.*;

@Service
@AllArgsConstructor
public class MonthlyTicketInfoService {
    private final MonthlyTicketInfoRepository monthlyTicketInfoRepository;

    /**
     * Update ticket info on validate or cancel action
     * @param ticket
     * @param newTicket
     * @param previousState
     */
    public void updateTicketInfo(Ticket ticket, boolean newTicket, TicketState previousState) {
        LocalDate date = ticket.getDate();
        YearMonth monthYear = YearMonth.from(date);
        MonthlyTicketInfo monthlyTicketInfo = monthlyTicketInfoRepository.findByMonthYear(monthYear.format(DateTimeFormatter.ofPattern("MM/yy")));
        MonthlyTicketInfo newMonthlyTicketInfo;

        /* This first 'if' should not be entered since all tickets should already have a date
          registered when it's created
        */
        if (monthlyTicketInfo == null) {
            newMonthlyTicketInfo = new MonthlyTicketInfo();
            newMonthlyTicketInfo.setMonthYear(monthYear.format(DateTimeFormatter.ofPattern("MM/yy")));
            setInitialData(ticket, newMonthlyTicketInfo);
            monthlyTicketInfoRepository.save(newMonthlyTicketInfo);
        } else {
            updateData(ticket, monthlyTicketInfo, newTicket, previousState);
            monthlyTicketInfoRepository.save(monthlyTicketInfo);
        }
    }

    /**
     * Add ticket info on the creation of the tickets
     * @param tickets
     */
    public void updateTicketListInfo(List<Ticket> tickets) {
        Map<String, MonthlyTicketInfoData> monthlyTicketInfoDataMap = new HashMap<>();

        for (Ticket ticket : tickets) {
            LocalDate date = ticket.getDate();
            YearMonth monthYear = YearMonth.from(date);
            String monthYearStr = monthYear.format(DateTimeFormatter.ofPattern("MM/yy"));

            MonthlyTicketInfoData monthlyTicketInfoData = monthlyTicketInfoDataMap.get(monthYearStr);
            MonthlyTicketInfoData newMonthlyTicketInfoData;
            if (monthlyTicketInfoData == null) {
                newMonthlyTicketInfoData = new MonthlyTicketInfoData();
                monthlyTicketInfoDataMap.put(monthYearStr, newMonthlyTicketInfoData);
                newMonthlyTicketInfoData.update(ticket);
            } else {
                monthlyTicketInfoData.update(ticket);
            }
        }

        for (Map.Entry<String, MonthlyTicketInfoData> entry : monthlyTicketInfoDataMap.entrySet()) {
            String monthYearStr = entry.getKey();
            MonthlyTicketInfoData monthlyTicketInfoData = entry.getValue();

            MonthlyTicketInfo monthlyTicketInfo = monthlyTicketInfoRepository.findByMonthYear(monthYearStr);
            MonthlyTicketInfo newMonthlyTicketInfo;
            if (monthlyTicketInfo == null) {
                newMonthlyTicketInfo = new MonthlyTicketInfo();
                newMonthlyTicketInfo.setMonthYear(monthYearStr);
                setInitialDataByMonthlyData(monthlyTicketInfoData, newMonthlyTicketInfo);
                monthlyTicketInfoRepository.save(newMonthlyTicketInfo);
            } else {
                monthlyTicketInfo.updateData(monthlyTicketInfoData);
                monthlyTicketInfoRepository.save(monthlyTicketInfo);
            }
        }
    }

    /**
     * Set initial data if the ticket is not found in the database on update
     * (should not happen?)
     * @param ticket
     * @param monthlyTicketInfo
     */
    public void setInitialData(Ticket ticket, MonthlyTicketInfo monthlyTicketInfo) {
        monthlyTicketInfo.setTotalPrice((double) ticket.getPrice());
        monthlyTicketInfo.setTicketCount(1L);
        monthlyTicketInfo.setTicketReservedCount(1L);
        monthlyTicketInfo.setTicketPaidCount(0L);
        monthlyTicketInfo.setTicketUsedCount(0L);
        monthlyTicketInfo.setTicketCancelledCount(0L);
        monthlyTicketInfo.setBenefits(0D);
    }

    /**
     * Set initial data if the ticket is not found in the database on the creation
     * @param ticket
     * @param monthlyTicketInfo
     */
    public void setInitialDataByMonthlyData(MonthlyTicketInfoData ticket, MonthlyTicketInfo monthlyTicketInfo) {
        monthlyTicketInfo.setTotalPrice(ticket.getTotalPrice());
        monthlyTicketInfo.setTicketCount(ticket.getTicketCount());
        monthlyTicketInfo.setTicketReservedCount(ticket.getTicketCount());
        monthlyTicketInfo.setTicketPaidCount(0L);
        monthlyTicketInfo.setTicketUsedCount(0L);
        monthlyTicketInfo.setTicketCancelledCount(0L);
        monthlyTicketInfo.setBenefits(0D);
    }

    /**
     * Update data on validate or cancel action
     * @param ticket
     * @param monthlyTicketInfo
     * @param newTicket
     * @param previousState
     */
    public void updateData(Ticket ticket,
                           MonthlyTicketInfo monthlyTicketInfo,
                           boolean newTicket,
                           TicketState previousState) {
        if (newTicket) {
            monthlyTicketInfo.setTicketCount(monthlyTicketInfo.getTicketCount() + 1);
            monthlyTicketInfo.setTotalPrice(monthlyTicketInfo.getTotalPrice() + ticket.getPrice());
            monthlyTicketInfo.setTicketReservedCount(monthlyTicketInfo.getTicketReservedCount() + 1);
        } else {
            if (previousState == PAID) {
                if (ticket.getState() == USED) {
                    monthlyTicketInfo.setTicketPaidCount(monthlyTicketInfo.getTicketPaidCount() - 1);
                    monthlyTicketInfo.setTicketUsedCount(monthlyTicketInfo.getTicketUsedCount() + 1);
                } else if (ticket.getState() == CANCELLED) {
                    monthlyTicketInfo.setTicketPaidCount(monthlyTicketInfo.getTicketPaidCount() - 1);
                    monthlyTicketInfo.setTicketCancelledCount(monthlyTicketInfo.getTicketCancelledCount() + 1);
                    monthlyTicketInfo.setBenefits(monthlyTicketInfo.getBenefits() - ticket.getPrice());
                }
            } else if (previousState == RESERVED) {
                if (ticket.getState() == PAID) {
                    monthlyTicketInfo.setTicketPaidCount(monthlyTicketInfo.getTicketPaidCount() + 1);
                    monthlyTicketInfo.setBenefits(monthlyTicketInfo.getBenefits() + ticket.getPrice());
                } else if (ticket.getState() == CANCELLED) {
                    monthlyTicketInfo.setTicketReservedCount(monthlyTicketInfo.getTicketReservedCount() - 1);
                    monthlyTicketInfo.setTicketCancelledCount(monthlyTicketInfo.getTicketCancelledCount() + 1);
                }
            }
        }
    }

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

