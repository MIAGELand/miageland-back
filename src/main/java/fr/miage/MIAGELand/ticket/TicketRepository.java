package fr.miage.MIAGELand.ticket;

import fr.miage.MIAGELand.visitor.Visitor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface TicketRepository extends JpaRepository<Ticket, Long> {

    List<Ticket> findAllByDateAfter(LocalDate now);
    Page<Ticket> findAll(Pageable pageable);
    List<Ticket> findByVisitor(Visitor visitor);
}
