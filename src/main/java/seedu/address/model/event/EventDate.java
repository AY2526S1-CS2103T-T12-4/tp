package seedu.address.model.event;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

/**
 * Represents an Event's date in the address book.
 * Guarantees: immutable; is valid as declared in {@link #isValidDate(String)}
 */
public class EventDate {

    public static final String MESSAGE_CONSTRAINTS =
            "Dates should be in the format dd-MM-yyyy and must be a valid calendar date.";

    public static final String VALIDATION_REGEX = "^(3[01]|[12][0-9]|0[1-9])-(1[0-2]|0[1-9])-[0-9]{4}$";

    public final String value;

    /**
     * Constructs an {@code EventDate}.
     *
     * @param date A valid date.
     */
    public EventDate(String date) {
        requireNonNull(date);
        checkArgument(isValidDate(date), MESSAGE_CONSTRAINTS);
        value = date;
    }

    /**
     * Returns true if a given string is a valid date.
     */
    public static boolean isValidDate(String test) {
        // First check if the format matches the regex
        if (!test.matches(VALIDATION_REGEX)) {
            return false;
        }

        // Then validate if the date actually exists in the calendar
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-uuuu")
                    .withResolverStyle(ResolverStyle.STRICT);
            LocalDate.parse(test, formatter);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof EventDate)) {
            return false;
        }

        EventDate otherDate = (EventDate) other;
        return value.equals(otherDate.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
