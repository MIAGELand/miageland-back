package fr.miage.MIAGELand.ticket;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Ticket repository
 * Handle all ticket related database requests
 * @see Ticket
 * @see Page
 */
public interface TicketRepository extends JpaRepository<Ticket, Long> {

    Page<Ticket> findAll(Pageable pageable);
}
