package fr.miage.MIAGELand.stats.daily_ticket_info;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface DailyTicketInfoRepository extends JpaRepository<DailyTicketInfo, String> {

    DailyTicketInfo findByDayMonthYear(LocalDate dayMonthYear);

    @Query("SELECT SUM(d.ticketCount) FROM DailyTicketInfo d")
    long getAllTickets();

    @Query("SELECT SUM(d.ticketPaidCount) FROM DailyTicketInfo d")
    long getAllPaidTickets();

    @Query("SELECT SUM(d.ticketUsedCount) FROM DailyTicketInfo d")
    long getAllUsedTickets();

    @Query("SELECT SUM(d.ticketCancelledCount) FROM DailyTicketInfo d")
    long getAllCancelledTickets();

    @Query("SELECT SUM(d.ticketReservedCount) FROM DailyTicketInfo d")
    long getAllReservedTickets();

    // Escape the :: characters from below
    @Query("SELECT MAX(d.ticketCount) FROM DailyTicketInfo d WHERE d.dayMonthYear > CURRENT_DATE ")
    long findMaxTicketCount();
}