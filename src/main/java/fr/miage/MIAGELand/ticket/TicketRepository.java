package fr.miage.MIAGELand.ticket;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface TicketRepository extends JpaRepository<Ticket, Long> {

    List<Ticket> findAllByDateAfter(LocalDateTime now);
    Page<Ticket> findAll(Pageable pageable);

}
