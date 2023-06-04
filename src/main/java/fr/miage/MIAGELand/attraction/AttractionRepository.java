package fr.miage.MIAGELand.attraction;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Attraction repository
 * Handle all attraction related database requests
 * @see Attraction
 */
public interface AttractionRepository extends JpaRepository<Attraction, Long> {
    long countByOpened(boolean opened);

    List<Attraction> findAll(Specification<Attraction> specs);
}
