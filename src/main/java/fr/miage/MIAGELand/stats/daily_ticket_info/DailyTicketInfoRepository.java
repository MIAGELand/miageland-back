package fr.miage.MIAGELand.stats.daily_ticket_info;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Daily ticket info repository
 * Handle all daily ticket info related database requests
 * @see DailyTicketInfo
 */
@Repository
public interface DailyTicketInfoRepository extends JpaRepository<DailyTicketInfo, String> {

    /**
     * Find daily ticket info by day month year
     * @param dayMonthYear Day month year
     * @return DailyTicketInfo
     */
    DailyTicketInfo findByDayMonthYear(LocalDate dayMonthYear);

    /**
     * Find the max ticket count
     * @return Max ticket count
     */
    @Query("SELECT MAX(d.ticketCount) FROM DailyTicketInfo d WHERE d.dayMonthYear > CURRENT_DATE ")
    long findMaxTicketCount();

    /**
     * Find all daily ticket info between start and end date
     * @param start Start date
     * @param end End date
     * @return List of daily ticket info
     */
    List<DailyTicketInfo> findAllByDayMonthYearBetween(LocalDate start, LocalDate end);


    // All the methods below are used to get the stats for the dashboard
    // Since we have the monthly basis stats, we can get the daily basis stats by summing the monthly stats
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

}
