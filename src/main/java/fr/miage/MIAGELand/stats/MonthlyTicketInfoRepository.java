package fr.miage.MIAGELand.stats;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MonthlyTicketInfoRepository extends JpaRepository<MonthlyTicketInfo, String> {

    MonthlyTicketInfo findByMonthYear(String format);
}
