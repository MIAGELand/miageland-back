package fr.miage.MIAGELand.stats;

import fr.miage.MIAGELand.stats.daily_ticket_info.DailyTicketInfo;
import fr.miage.MIAGELand.stats.daily_ticket_info.DailyTicketInfoData;
import fr.miage.MIAGELand.stats.daily_ticket_info.DailyTicketInfoRepository;
import fr.miage.MIAGELand.stats.monthly_ticket_info.MonthlyTicketInfo;
import fr.miage.MIAGELand.stats.monthly_ticket_info.MonthlyTicketInfoData;
import fr.miage.MIAGELand.stats.monthly_ticket_info.MonthlyTicketInfoRepository;
import fr.miage.MIAGELand.ticket.Ticket;
import fr.miage.MIAGELand.ticket.TicketState;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static fr.miage.MIAGELand.ticket.TicketState.*;
import static fr.miage.MIAGELand.ticket.TicketState.CANCELLED;

@Service
@AllArgsConstructor
public class StatTicketInfoService {

    private final MonthlyTicketInfoRepository monthlyTicketInfoRepository;
    private final DailyTicketInfoRepository dailyTicketInfoRepository;

    public void updateTicketInfo(Ticket ticket, boolean newTicket, TicketState previousState) {
        LocalDate date = ticket.getDate();
        YearMonth monthYear = YearMonth.from(date);
        MonthlyTicketInfo monthlyTicketInfo = monthlyTicketInfoRepository.findByMonthYear(monthYear.format(DateTimeFormatter.ofPattern("MM/yy")));
        MonthlyTicketInfo newMonthlyTicketInfo;
        DailyTicketInfo dailyTicketInfo = dailyTicketInfoRepository.findByDayMonthYear(date.format(DateTimeFormatter.ofPattern("dd/MM/yy")));
        DailyTicketInfo newDailyTicketInfo;

        if (monthlyTicketInfo == null) {
            newMonthlyTicketInfo = new MonthlyTicketInfo();
            newMonthlyTicketInfo.setMonthYear(monthYear.format(DateTimeFormatter.ofPattern("MM/yy")));
            setInitialData(ticket, newMonthlyTicketInfo);
            monthlyTicketInfoRepository.save(newMonthlyTicketInfo);
        } else {
            updateData(ticket, monthlyTicketInfo, newTicket, previousState);
            monthlyTicketInfoRepository.save(monthlyTicketInfo);
        }

        if (dailyTicketInfo == null) {
            newDailyTicketInfo = new DailyTicketInfo();
            newDailyTicketInfo.setDayMonthYear(date.format(DateTimeFormatter.ofPattern("dd/MM/yy")));
            setInitialData(ticket, newDailyTicketInfo);
            dailyTicketInfoRepository.save(newDailyTicketInfo);
        } else {
            updateData(ticket, dailyTicketInfo, newTicket, previousState);
            dailyTicketInfoRepository.save(dailyTicketInfo);
        }
    }

    /**
     * Add ticket info on the creation of the tickets
     * @param tickets
     */
    public void updateTicketListInfo(List<Ticket> tickets) {
        Map<String, MonthlyTicketInfoData> monthlyTicketInfoDataMap = new HashMap<>();
        Map<String, DailyTicketInfoData> dailyTicketInfoDataMap = new HashMap<>();
        for (Ticket ticket : tickets) {
            LocalDate date = ticket.getDate();
            YearMonth monthYear = YearMonth.from(date);
            String monthYearStr = monthYear.format(DateTimeFormatter.ofPattern("MM/yy"));
            String dayMonthYearStr = date.format(DateTimeFormatter.ofPattern("dd/MM/yy"));

            MonthlyTicketInfoData monthlyTicketInfoData = monthlyTicketInfoDataMap.get(monthYearStr);
            MonthlyTicketInfoData newMonthlyTicketInfoData;

            DailyTicketInfoData dailyTicketInfoData = dailyTicketInfoDataMap.get(dayMonthYearStr);
            DailyTicketInfoData newDailyTicketInfoData;
            if (monthlyTicketInfoData == null) {
                newMonthlyTicketInfoData = new MonthlyTicketInfoData();
                monthlyTicketInfoDataMap.put(monthYearStr, newMonthlyTicketInfoData);
                newMonthlyTicketInfoData.update(ticket);
            } else {
                monthlyTicketInfoData.update(ticket);
            }

            if (dailyTicketInfoData == null) {
                newDailyTicketInfoData = new DailyTicketInfoData();
                dailyTicketInfoDataMap.put(dayMonthYearStr, newDailyTicketInfoData);
                newDailyTicketInfoData.update(ticket);
            } else {
                dailyTicketInfoData.update(ticket);
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

        for (Map.Entry<String, DailyTicketInfoData> entry : dailyTicketInfoDataMap.entrySet()) {
            String dayMonthYearStr = entry.getKey();
            DailyTicketInfoData dailyTicketInfoData = entry.getValue();

            DailyTicketInfo dailyTicketInfo = dailyTicketInfoRepository.findByDayMonthYear(dayMonthYearStr);
            DailyTicketInfo newDailyTicketInfo;
            if (dailyTicketInfo == null) {
                newDailyTicketInfo = new DailyTicketInfo();
                newDailyTicketInfo.setDayMonthYear(dayMonthYearStr);
                setInitialDataByMonthlyData(dailyTicketInfoData, newDailyTicketInfo);
                dailyTicketInfoRepository.save(newDailyTicketInfo);
            } else {
                dailyTicketInfo.updateData(dailyTicketInfoData);
                dailyTicketInfoRepository.save(dailyTicketInfo);
            }
        }
    }

    /**
     * Set initial data if the ticket is not found in the database on update
     * (should not happen?)
     * @param ticket
     * @param statTicketInfo
     */
    public void setInitialData(Ticket ticket, StatTicketInfo statTicketInfo) {
        statTicketInfo.setTotalPrice((double) ticket.getPrice());
        statTicketInfo.setTicketCount(1L);
        statTicketInfo.setTicketReservedCount(1L);
        statTicketInfo.setTicketPaidCount(0L);
        statTicketInfo.setTicketUsedCount(0L);
        statTicketInfo.setTicketCancelledCount(0L);
        statTicketInfo.setBenefits(0D);
    }

    /**
     * Set initial data if the ticket is not found in the database on the creation
     * @param ticket
     * @param statTicketInfo
     */
    public void setInitialDataByMonthlyData(TicketInfoData ticket, StatTicketInfo statTicketInfo) {
        statTicketInfo.setTotalPrice(ticket.getTotalPrice());
        statTicketInfo.setTicketCount(ticket.getTicketCount());
        statTicketInfo.setTicketReservedCount(ticket.getTicketCount());
        statTicketInfo.setTicketPaidCount(0L);
        statTicketInfo.setTicketUsedCount(0L);
        statTicketInfo.setTicketCancelledCount(0L);
        statTicketInfo.setBenefits(0D);
    }

    /**
     * Update data on validate or cancel action
     * @param ticket
     * @param statTicketInfo
     * @param newTicket
     * @param previousState
     */
    public void updateData(Ticket ticket,
                           StatTicketInfo statTicketInfo,
                           boolean newTicket,
                           TicketState previousState) {
        if (newTicket) {
            statTicketInfo.setTicketCount(statTicketInfo.getTicketCount() + 1);
            statTicketInfo.setTotalPrice(statTicketInfo.getTotalPrice() + ticket.getPrice());
            statTicketInfo.setTicketReservedCount(statTicketInfo.getTicketReservedCount() + 1);
        } else {
            if (previousState == PAID) {
                statTicketInfo.setTicketPaidCount(statTicketInfo.getTicketPaidCount() - 1);
                if (ticket.getState() == USED) {
                    statTicketInfo.setTicketUsedCount(statTicketInfo.getTicketUsedCount() + 1);
                } else if (ticket.getState() == CANCELLED) {
                    statTicketInfo.setTicketCancelledCount(statTicketInfo.getTicketCancelledCount() + 1);
                    statTicketInfo.setBenefits(statTicketInfo.getBenefits() - ticket.getPrice());
                }
            } else if (previousState == RESERVED) {
                statTicketInfo.setTicketReservedCount(statTicketInfo.getTicketReservedCount() - 1);
                if (ticket.getState() == PAID) {
                    statTicketInfo.setTicketPaidCount(statTicketInfo.getTicketPaidCount() + 1);
                    statTicketInfo.setBenefits(statTicketInfo.getBenefits() + ticket.getPrice());
                } else if (ticket.getState() == CANCELLED) {
                    statTicketInfo.setTicketCancelledCount(statTicketInfo.getTicketCancelledCount() + 1);
                }
            }
        }
    }
}
