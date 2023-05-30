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

    public boolean isVisitorFieldValid(Visitor visitor) {
        return visitor.getName() != null && visitor.getSurname() != null && visitor.getEmail() != null;
    }

    public Page<Visitor> getVisitors(int pageNumber) {
        Pageable pageable = PageRequest.of(pageNumber, DEFAULT_PAGE_SIZE);
        return visitorRepository.findAll(pageable);
    }
}
