package fr.miage.MIAGELand.stats.monthly_ticket_info;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.YearMonth;
import java.util.List;

public interface MonthlyTicketInfoRepository extends JpaRepository<MonthlyTicketInfo, String> {
    MonthlyTicketInfo findByMonthYear(YearMonth monthYear);

    @Query("SELECT SUM(m.ticketCount) FROM MonthlyTicketInfo m")
    long getAllTickets();
    long countAllByMonthYearBetween(YearMonth start, YearMonth end);

    @Query("SELECT SUM(m.ticketPaidCount) FROM MonthlyTicketInfo m")
    long getAllPaidTickets();

    long countAllPaidByMonthYearBetween(YearMonth start, YearMonth end);

    @Query("SELECT SUM(m.ticketUsedCount) FROM MonthlyTicketInfo m")
    long getAllUsedTickets();

    long countAllUsedByMonthYearBetween(YearMonth start, YearMonth end);

    @Query("SELECT SUM(m.ticketCancelledCount) FROM MonthlyTicketInfo m")
    long getAllCancelledTickets();

    long countAllCancelledByMonthYearBetween(YearMonth start, YearMonth end);

    @Query("SELECT SUM(m.ticketReservedCount) FROM MonthlyTicketInfo m")
    long getAllReservedTickets();

    long countAllReservedByMonthYearBetween(YearMonth start, YearMonth end);

    List<MonthlyTicketInfo> findAllByMonthYearBetween(YearMonth start, YearMonth end);
}
