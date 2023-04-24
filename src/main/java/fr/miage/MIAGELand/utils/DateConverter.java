package fr.miage.MIAGELand.utils;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class DateConverter {
    public static LocalDateTime convertFakerDate(String fakerDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
        ZonedDateTime zonedDateTime = ZonedDateTime.parse(fakerDate, formatter);
        return zonedDateTime.toLocalDateTime();
    }
}

