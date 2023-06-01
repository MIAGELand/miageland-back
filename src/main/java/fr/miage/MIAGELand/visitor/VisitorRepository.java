package fr.miage.MIAGELand.visitor;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Visitor repository
 * Handle all visitor related database requests
 * @see Visitor
 */
@Repository
public interface VisitorRepository extends JpaRepository<Visitor, Long> {
    Visitor findByEmail(String email);
    boolean existsByEmail(String email);
}
