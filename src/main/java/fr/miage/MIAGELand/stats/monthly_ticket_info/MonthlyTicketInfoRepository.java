package fr.miage.MIAGELand.stats.monthly_ticket_info;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.YearMonth;

public interface MonthlyTicketInfoRepository extends JpaRepository<MonthlyTicketInfo, String> {
    MonthlyTicketInfo findByMonthYear(YearMonth monthYear);

    @Query("SELECT SUM(m.ticketCount) FROM MonthlyTicketInfo m")
    long getAllTickets();

    @Query("SELECT SUM(m.ticketPaidCount) FROM MonthlyTicketInfo m")
    long getAllPaidTickets();

    @Query("SELECT SUM(m.ticketUsedCount) FROM MonthlyTicketInfo m")
    long getAllUsedTickets();

    @Query("SELECT SUM(m.ticketCancelledCount) FROM MonthlyTicketInfo m")
    long getAllCancelledTickets();

    @Query("SELECT SUM(m.ticketReservedCount) FROM MonthlyTicketInfo m")
    long getAllReservedTickets();
}
