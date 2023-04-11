package fr.miage.MIAGELand.ticket;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketRepository extends JpaRepository<Ticket, Long> {

    Ticket findByNbTicket(Long nbTicket);
}
