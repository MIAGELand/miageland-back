package fr.miage.MIAGELand.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateConverter {

    /**
     * Convert a string to a LocalDate
     * @param localDate String
     * @return LocalDate object
     */
    public static LocalDate convertLocalDate(String localDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return LocalDate.parse(localDate, formatter);
    }
}

