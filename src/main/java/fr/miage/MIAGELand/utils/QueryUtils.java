package fr.miage.MIAGELand.utils;

import fr.miage.MIAGELand.employee.EmployeeRole;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.MultiValueMap;
import jakarta.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class QueryUtils {

    public static <T> Specification<T> buildSpecification(MultiValueMap<String, String> params, String entity) {
        return (root, query, criteriaBuilder) -> {
            // Create an empty list to hold the predicates
            List<Predicate> predicates = new ArrayList<>();
            // Iterate over the parameters
            for (Map.Entry<String, List<String>> entry : params.entrySet()) {
                String key = entry.getKey();
                List<String> values = entry.getValue();
                if (entity.equals("employee") && key.equals("role")) {
                    List<Integer> roleSmallint = convertRoleToSmallint(values);
                    predicates.add(root.get(key).in(roleSmallint));
                } else {
                    Predicate predicate = buildPredicate(key, values, root, criteriaBuilder);
                    // Add the predicate to the list
                    predicates.add(predicate);
                }
            }

            // Combine all predicates using OR operator
            return criteriaBuilder.or(predicates.toArray(new Predicate[0]));
        };
    }

    private static <T> Predicate buildPredicate(String key, List<String> values, Root<T> root, CriteriaBuilder criteriaBuilder) {
        if (values.size() == 1) {
            return criteriaBuilder.like(root.get(key), "%" + values.get(0) + "%");
        } else {
            return root.get(key).in(values);
        }
    }

    private static List<Integer> convertRoleToSmallint(List<String> role) {
        List<Integer> roleSmallint = new ArrayList<>();
        for (String r : role) {
            switch (r) {
                case "MANAGER" -> roleSmallint.add(EmployeeRole.MANAGER.ordinal());
                case "CLASSIC" -> roleSmallint.add(EmployeeRole.CLASSIC.ordinal());
                case "ADMIN" -> roleSmallint.add(EmployeeRole.ADMIN.ordinal());
                default -> {
                }
            }
        }
        return roleSmallint;
    }

}
