package fr.miage.MIAGELand.visitor;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Visitor repository
 * Handle all visitor related database requests
 * @see Visitor
 */
@Repository
public interface VisitorRepository extends JpaRepository<Visitor, Long> {
    Visitor findByEmail(String email);
    boolean existsByEmail(String email);
    List<Visitor> findAll(Specification<Visitor> specs);
}
