package fr.miage.MIAGELand.attraction;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AttractionRepository extends JpaRepository<Attraction, Long> {
    long countByOpened(boolean opened);
}
