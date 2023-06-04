package fr.miage.MIAGELand.utils;

import fr.miage.MIAGELand.employee.EmployeeRole;
import fr.miage.MIAGELand.ticket.TicketState;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.MultiValueMap;
import jakarta.persistence.criteria.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Query utils
 * Handle all query related methods to build predicates
 * @see Specification
 * @see Predicate
 * @see CriteriaBuilder
 */
public class QueryUtils {

    /**
     * Build a predicate for a given key and values list for a given entity
     * @param params Params
     * @param entity Entity
     * @return Specification
     * @param <T> Generic type
     */
    public static <T> Specification<T> buildSpecification(MultiValueMap<String, String> params, String entity) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            for (Map.Entry<String, List<String>> entry : params.entrySet()) {
                String key = entry.getKey();
                List<String> values = entry.getValue();

                try {
                    if (isKeyValidField(root, key)) {
                        Predicate predicate = buildPredicate(entity, key, values, root, criteriaBuilder);
                        predicates.add(predicate);
                    }
                } catch (IllegalArgumentException e) {
                    // Field not found, continue with the next iteration
                    continue;
                }
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    /**
     * Check if a key is a valid field
     * @param root Root
     * @param key Key
     * @return Boolean
     * @param <T> Generic type
     */
    private static <T> boolean isKeyValidField(Root<T> root, String key) {
        try {
            root.get(key);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Build a predicate for a given key and values list
     * @param entity Entity name
     * @param key Key
     * @param values Values
     * @param root Root
     * @param criteriaBuilder Criteria builder
     * @return Predicate
     * @param <T> Generic type
     */
    private static <T> Predicate buildPredicate(String entity, String key, List<String> values, Root<T> root, CriteriaBuilder criteriaBuilder) {
        if (entity.equals("employee") && key.equals("role")) {
            List<Integer> roleSmallint = convertRoleToSmallintList(values);
            return root.get(key).in(roleSmallint);
        } else if (entity.equals("attraction") && key.equals("opened")) {
            List<Boolean> opened = convertOpenedToBooleanList(values);
            return root.get(key).in(opened);
        } else if (entity.equals("ticket") && key.equals("state")) {
            List<Integer> stateSmallint = convertStateToSmallintList(values);
            return root.get(key).in(stateSmallint);
        } else if (entity.equals("ticket") && key.equals("price")) {
            double price = convertToDouble(values.get(0));
            return criteriaBuilder.equal(root.get(key), price);
        } else if (entity.equals("ticket") && key.equals("date")) {
            LocalDate date = convertToLocalDate(values.get(0));
            return criteriaBuilder.equal(root.get(key), date);
        } else if (key.contains("id")) {
            List<Long> ids = convertToLongList(values);
            return root.get(key).in(ids);
        } else {
            return buildPredicate(key, values, root, criteriaBuilder);
        }
    }

    /**
     * Convert a list of String to a list of LocalDate
     * @param value List of String
     * @return List of LocalDate
     */
    private static LocalDate convertToLocalDate(String value) {
        return LocalDate.parse(value);
    }

    /**
     * Build a predicate from a key and a list of values
     * @param key Key of the predicate
     * @param values List of values
     * @param root Root
     * @param criteriaBuilder CriteriaBuilder
     * @return Predicate
     * @param <T> Entity type
     */
    private static <T> Predicate buildPredicate(String key, List<String> values, Root<T> root, CriteriaBuilder criteriaBuilder) {
        if (values.size() == 1) {
            return criteriaBuilder.like(root.get(key), "%" + values.get(0) + "%");
        } else {
            return root.get(key).in(values);
        }
    }

    /**
     * Convert a list of EmployeeRole to a list of smallint
     * @param role List of role
     * @return List of smallint
     */
    private static List<Integer> convertRoleToSmallintList(List<String> role) {
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

    /**
     * Convert a list of TicketState to a list of smallint
     * @param state List of state
     * @return List of smallint
     */
    public static List<Integer> convertStateToSmallintList(List<String> state) {
        List<Integer> stateSmallint = new ArrayList<>();
        for (String s : state) {
            switch (s) {
                case "RESERVED" -> stateSmallint.add(TicketState.RESERVED.ordinal());
                case "PAID" -> stateSmallint.add(TicketState.PAID.ordinal());
                case "USED" -> stateSmallint.add(TicketState.USED.ordinal());
                case "CANCELLED" -> stateSmallint.add(TicketState.CANCELLED.ordinal());
                default -> {
                }
            }
        }
        return stateSmallint;
    }

    /**
     * Convert a list of opened to a list of boolean (true or false)
     * @param opened List of opened
     * @return List of boolean
     */
    private static List<Boolean> convertOpenedToBooleanList(List<String> opened) {
        List<Boolean> openedBoolean = new ArrayList<>();
        for (String o : opened) {
            switch (o) {
                case "true" -> openedBoolean.add(true);
                case "false" -> openedBoolean.add(false);
                default -> {}
            }
        }
        return openedBoolean;
    }

    /**
     * Convert a list of string to a list of long
     * @param values List of string
     * @return List of long
     */
    private static List<Long> convertToLongList(List<String> values) {
        List<Long> longs = new ArrayList<>();
        for (String value : values) {
            longs.add(Long.parseLong(value));
        }
        return longs;
    }

    /**
     * Convert a string to a double
     * @param value String
     * @return Double
     */
    private static double convertToDouble(String value) {
        return Double.parseDouble(value);
    }

}
