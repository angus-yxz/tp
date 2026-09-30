package seedu.address.model.person;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

import java.util.Locale;

/**
 * A client's unique, case-insensitive reference.
 */
public final class ClientReference {
    public static final String MESSAGE_CONSTRAINTS = "Client reference must start with a letter and contain only "
            + "letters, digits, or hyphens (maximum 20 characters).";
    private static final String VALIDATION_REGEX = "[A-Za-z][A-Za-z0-9-]{0,19}";

    public final String value;

    /**
     * Creates a normalized client reference.
     */
    public ClientReference(String reference) {
        requireNonNull(reference);
        String trimmedReference = reference.trim();
        checkArgument(isValidReference(trimmedReference), MESSAGE_CONSTRAINTS);
        value = trimmedReference.toUpperCase(Locale.ROOT);
    }

    public static boolean isValidReference(String reference) {
        return reference.matches(VALIDATION_REGEX);
    }

    @Override
    public boolean equals(Object other) {
        return other == this || other instanceof ClientReference that && value.equals(that.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public String toString() {
        return value;
    }
}
