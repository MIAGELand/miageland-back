package fr.miage.MIAGELand.visitor;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VisitorRepository extends JpaRepository<Visitor, Long> {
    Visitor findByNameAndSurname(String name, String surname);

    Visitor findByEmail(String email);
}
