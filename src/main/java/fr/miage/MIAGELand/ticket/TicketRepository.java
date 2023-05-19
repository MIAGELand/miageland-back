package fr.miage.MIAGELand.ticket;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface TicketRepository extends JpaRepository<Ticket, Long> {

    Ticket findById(long id);
    List<Ticket> findAllByDateAfter(LocalDateTime now);
    long countByState(TicketState ticketState);
    long countAllByDateBetween(LocalDateTime begin, LocalDateTime end);
    long countAllByDateBetweenAndState(LocalDateTime begin, LocalDateTime end, TicketState state);
    float findPriceById(long id);
    Page<Ticket> findAll(Pageable pageable);

}
