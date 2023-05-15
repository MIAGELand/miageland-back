package fr.miage.MIAGELand.ticket;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface TicketRepository extends JpaRepository<Ticket, Long> {

    Ticket findById(long id);

    List<Ticket> findAllByDateAfter(LocalDateTime now);
}
