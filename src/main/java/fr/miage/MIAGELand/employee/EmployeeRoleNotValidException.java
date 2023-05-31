package fr.miage.MIAGELand.employee;

/**
 * Exception thrown when a role is not valid.
 */
public class EmployeeRoleNotValidException extends Exception {
    public EmployeeRoleNotValidException(String message) {
        super(message);
    }
}
