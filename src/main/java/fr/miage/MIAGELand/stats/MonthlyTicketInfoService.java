package fr.miage.MIAGELand.stats;

import fr.miage.MIAGELand.api.stats.MonthlyTicketInfos;
import fr.miage.MIAGELand.api.stats.NumberStatsTicket;
import fr.miage.MIAGELand.ticket.Ticket;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import static fr.miage.MIAGELand.ticket.TicketState.*;

@Service
@AllArgsConstructor
public class MonthlyTicketInfoService {
    private final MonthlyTicketInfoRepository monthlyTicketInfoRepository;
    public void updateTicketInfo(Ticket ticket, boolean newTicket) {
        LocalDateTime date = ticket.getDate();
        YearMonth monthYear = YearMonth.from(date);
        MonthlyTicketInfo monthlyTicketInfo = monthlyTicketInfoRepository.findByMonthYear(monthYear.format(DateTimeFormatter.ofPattern("MM/yy")));
        MonthlyTicketInfo newMonthlyTicketInfo;
        if (monthlyTicketInfo == null) {
            newMonthlyTicketInfo = new MonthlyTicketInfo();
            newMonthlyTicketInfo.setMonthYear(monthYear.format(DateTimeFormatter.ofPattern("MM/yy")));
            setInitialData(ticket, newMonthlyTicketInfo);
            monthlyTicketInfoRepository.save(newMonthlyTicketInfo);
        } else {
            updateData(ticket, monthlyTicketInfo, newTicket);
            monthlyTicketInfoRepository.save(monthlyTicketInfo);
        }
    }
    public void updateTicketListInfo(List<Ticket> ticket, boolean newTicket) {
        for (Ticket t : ticket) {
            updateTicketInfo(t, newTicket);
        }
    }
    public void setInitialData(Ticket ticket, MonthlyTicketInfo monthlyTicketInfo) {
        monthlyTicketInfo.setTotalPrice((double) ticket.getPrice());
        monthlyTicketInfo.setTicketCount(1L);
        monthlyTicketInfo.setTicketPaidCount(1L);
        monthlyTicketInfo.setTicketUsedCount(0L);
        monthlyTicketInfo.setTicketCancelledCount(0L);
        monthlyTicketInfo.setBenefits((double) ticket.getPrice());
    }

    public void updateData(Ticket ticket,
                           MonthlyTicketInfo monthlyTicketInfo,
                           boolean newTicket) {
        if (newTicket) {
            monthlyTicketInfo.setTicketCount(monthlyTicketInfo.getTicketCount() + 1);
            monthlyTicketInfo.setTotalPrice(monthlyTicketInfo.getTotalPrice() + ticket.getPrice());
            monthlyTicketInfo.setTicketPaidCount(monthlyTicketInfo.getTicketPaidCount() + 1);
            monthlyTicketInfo.setBenefits(monthlyTicketInfo.getBenefits() + ticket.getPrice());
        } else {
            if (ticket.getState() == USED) {
                monthlyTicketInfo.setTicketPaidCount(monthlyTicketInfo.getTicketPaidCount() - 1);
                monthlyTicketInfo.setTicketUsedCount(monthlyTicketInfo.getTicketUsedCount() + 1);
            } else if (ticket.getState() == CANCELLED) {
                monthlyTicketInfo.setTicketPaidCount(monthlyTicketInfo.getTicketPaidCount() - 1);
                monthlyTicketInfo.setTicketCancelledCount(monthlyTicketInfo.getTicketCancelledCount() + 1);
                monthlyTicketInfo.setBenefits(monthlyTicketInfo.getBenefits() - ticket.getPrice());
            }
        }
    }

    public NumberStatsTicket getGlobalStatsTicket() {
        return new NumberStatsTicket(
                monthlyTicketInfoRepository.getAllTickets(),
                monthlyTicketInfoRepository.getAllPaidTickets(),
                monthlyTicketInfoRepository.getAllUsedTickets(),
                monthlyTicketInfoRepository.getAllCancelledTickets()
        );
    }

    public List<MonthlyTicketInfos> getMonthlyTicketInfos() {
        List<MonthlyTicketInfo> monthlyTicketInfos = monthlyTicketInfoRepository.findAll();

        return monthlyTicketInfos.stream()
                .map(monthlyTicketInfo -> new MonthlyTicketInfos(
                        monthlyTicketInfo.getMonthYear(),
                        new NumberStatsTicket(
                                monthlyTicketInfo.getTicketCount(),
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

