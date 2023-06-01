package fr.miage.MIAGELand.stats;

import fr.miage.MIAGELand.park.Park;
import fr.miage.MIAGELand.park.ParkRepository;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static fr.miage.MIAGELand.ticket.TicketState.*;
import static fr.miage.MIAGELand.ticket.TicketState.CANCELLED;

/**
 * Stat ticket info service
 * Handle all statticket info related business logic
 */
@Service
@AllArgsConstructor
public class StatTicketInfoService {

    private final MonthlyTicketInfoRepository monthlyTicketInfoRepository;
    private final DailyTicketInfoRepository dailyTicketInfoRepository;
    private final ParkRepository parkRepository;

    /**
     * Update ticket info on action : pay, validate, cancel
     * @param ticket Ticket
     * @param previousState Previous state of the ticket (before the action)
     */
    public void updateTicketInfoOnAction(Ticket ticket, TicketState previousState) {
        LocalDate date = ticket.getDate();
        YearMonth monthYear = YearMonth.from(date);
        MonthlyTicketInfo monthlyTicketInfo = monthlyTicketInfoRepository.findByMonthYear(monthYear);
        MonthlyTicketInfo newMonthlyTicketInfo;
        DailyTicketInfo dailyTicketInfo = dailyTicketInfoRepository.findByDayMonthYear(date);
        DailyTicketInfo newDailyTicketInfo;

        if (monthlyTicketInfo == null) {
            newMonthlyTicketInfo = new MonthlyTicketInfo();
            newMonthlyTicketInfo.setMonthYear(monthYear);
            setInitialData(ticket, newMonthlyTicketInfo);
            monthlyTicketInfoRepository.save(newMonthlyTicketInfo);
        } else {
            updateDataOnAction(ticket, monthlyTicketInfo, previousState);
            monthlyTicketInfoRepository.save(monthlyTicketInfo);
        }

        if (dailyTicketInfo == null) {
            newDailyTicketInfo = new DailyTicketInfo();
            newDailyTicketInfo.setDayMonthYear(date);
            setInitialData(ticket, newDailyTicketInfo);
            dailyTicketInfoRepository.save(newDailyTicketInfo);
        } else {
            updateDataOnAction(ticket, dailyTicketInfo, previousState);
            dailyTicketInfoRepository.save(dailyTicketInfo);
        }
    }

    /**
     * Add ticket info on the creation of the tickets = reservation
     * @param tickets Tickets
     */
    public void updateTicketListInfo(List<Ticket> tickets) {
        // Initialize maps for monthly and daily ticket info
        Map<YearMonth, MonthlyTicketInfoData> monthlyTicketInfoDataMap = new HashMap<>();
        Map<LocalDate, DailyTicketInfoData> dailyTicketInfoDataMap = new HashMap<>();

        // For each ticket, update the corresponding monthly and daily ticket info
        for (Ticket ticket : tickets) {
            LocalDate date = ticket.getDate();
            YearMonth monthYear = YearMonth.from(date);

            MonthlyTicketInfoData monthlyTicketInfoData = monthlyTicketInfoDataMap.get(monthYear);
            MonthlyTicketInfoData newMonthlyTicketInfoData;

            DailyTicketInfoData dailyTicketInfoData = dailyTicketInfoDataMap.get(date);
            DailyTicketInfoData newDailyTicketInfoData;
            if (monthlyTicketInfoData == null) {
                newMonthlyTicketInfoData = new MonthlyTicketInfoData();
                monthlyTicketInfoDataMap.put(monthYear, newMonthlyTicketInfoData);
                newMonthlyTicketInfoData.update(ticket);
            } else {
                monthlyTicketInfoData.update(ticket);
            }

            if (dailyTicketInfoData == null) {
                newDailyTicketInfoData = new DailyTicketInfoData();
                dailyTicketInfoDataMap.put(date, newDailyTicketInfoData);
                newDailyTicketInfoData.update(ticket);
            } else {
                dailyTicketInfoData.update(ticket);
            }
        }

        // For each monthly ticket info, update the corresponding data and save it
        for (Map.Entry<YearMonth, MonthlyTicketInfoData> entry : monthlyTicketInfoDataMap.entrySet()) {
            YearMonth monthYear = entry.getKey();
            MonthlyTicketInfoData monthlyTicketInfoData = entry.getValue();

            MonthlyTicketInfo monthlyTicketInfo = monthlyTicketInfoRepository.findByMonthYear(monthYear);
            MonthlyTicketInfo newMonthlyTicketInfo;
            if (monthlyTicketInfo == null) {
                newMonthlyTicketInfo = new MonthlyTicketInfo();
                newMonthlyTicketInfo.setMonthYear(monthYear);
                setInitialDataByMonthlyData(monthlyTicketInfoData, newMonthlyTicketInfo);
                monthlyTicketInfoRepository.save(newMonthlyTicketInfo);
            } else {
                monthlyTicketInfo.updateData(monthlyTicketInfoData);
                monthlyTicketInfoRepository.save(monthlyTicketInfo);
            }
        }

        // Get max ticket_count by day
        Park park = parkRepository.findById(1L).get();

        // For each daily ticket info, update the corresponding data and save it
        for (Map.Entry<LocalDate, DailyTicketInfoData> entry : dailyTicketInfoDataMap.entrySet()) {
            LocalDate date = entry.getKey();
            DailyTicketInfoData dailyTicketInfoData = entry.getValue();

            DailyTicketInfo dailyTicketInfo = dailyTicketInfoRepository.findByDayMonthYear(date);
            DailyTicketInfo newDailyTicketInfo;
            if (dailyTicketInfo == null) {
                newDailyTicketInfo = new DailyTicketInfo();
                newDailyTicketInfo.setDayMonthYear(date);
                setInitialDataByMonthlyData(dailyTicketInfoData, newDailyTicketInfo);
                dailyTicketInfoRepository.save(newDailyTicketInfo);
            } else {
                dailyTicketInfo.updateData(dailyTicketInfoData);
                dailyTicketInfoRepository.save(dailyTicketInfo);
                long dailyTicketCount = dailyTicketInfo.getTicketCount();
                if (dailyTicketInfoRepository.count() > 0) {
                    long maxTicketCountByDay = dailyTicketInfoRepository.findMaxTicketCount();
                    park.setMinTicketGauge(Math.max(maxTicketCountByDay, dailyTicketCount));
                    parkRepository.save(park);
                }
            }
        }
    }

    /**
     * Set initial data if the ticket is not found in the database on update
     * This function is used as a protection but should never be called
     * @param ticket Ticket
     * @param statTicketInfo StatTicketInfo
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
     * @param ticket Ticket
     * @param statTicketInfo StatTicketInfo
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
     * @param ticket Ticket
     * @param statTicketInfo StatTicketInfo
     * @param previousState TicketState
     */
    public void updateDataOnAction(Ticket ticket,
                                   StatTicketInfo statTicketInfo,
                                   TicketState previousState) {
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
