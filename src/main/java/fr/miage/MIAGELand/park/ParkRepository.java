package fr.miage.MIAGELand.park;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Park repository
 * Handle all park related database requests
 * @see Park
 */
public interface ParkRepository extends JpaRepository<Park, Long> {
}
