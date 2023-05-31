package fr.miage.MIAGELand.visitor;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class VisitorService {

    private final VisitorRepository visitorRepository;
    private static final int DEFAULT_PAGE_SIZE = 100;

    /**
     * Check if a visitor is valid
     * @param visitor Visitor
     * @return True if the visitor is valid, false otherwise
     */
    public boolean isVisitorFieldValid(Visitor visitor) {
        return visitor.getName() != null && visitor.getSurname() != null && visitor.getEmail() != null;
    }

    /**
     * Get visitors with pagination (100 visitors per page by default)
     * @param pageNumber Page number
     * @return Visitors
     * @see Page
     * @see Pageable
     * @see PageRequest
     */
    public Page<Visitor> getVisitors(int pageNumber) {
        Pageable pageable = PageRequest.of(pageNumber, DEFAULT_PAGE_SIZE);
        return visitorRepository.findAll(pageable);
    }
}
