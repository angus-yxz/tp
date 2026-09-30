package seedu.address.model.person;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

/**
 * Represents a Person's name in the address book.
 * Guarantees: immutable; is valid as declared in {@link #isValidName(String)}
 */
public class Name {

    public static final String MESSAGE_CONSTRAINTS =
            "Names should only contain alphanumeric characters and spaces, and should not be blank";
    public static final String CLIENT_MESSAGE_CONSTRAINTS =
            "Name must contain 1-100 characters and use only letters, spaces, apostrophes, or hyphens.";

    /*
     * The first character of the name must not be a whitespace,
     * otherwise " " (a blank string) becomes a valid input.
     */
    public static final String VALIDATION_REGEX = "[\\p{Alnum}][\\p{Alnum} ]*";

    public final String fullName;

    /**
     * Constructs a {@code Name}.
     *
     * @param name A valid name.
     */
    public Name(String name) {
        requireNonNull(name);
        checkArgument(isValidName(name), MESSAGE_CONSTRAINTS);
        fullName = name;
    }

    private Name(String name, boolean clientName) {
        requireNonNull(name);
        checkArgument(clientName && isValidClientName(name), CLIENT_MESSAGE_CONSTRAINTS);
        fullName = name;
    }

    /** Returns a client name with repeated spaces collapsed. */
    public static Name forClient(String name) {
        String normalized = requireNonNull(name).trim().replaceAll(" +", " ");
        return new Name(normalized, true);
    }

    public static boolean isValidClientName(String name) {
        return name.matches("(?=.{1,100}$)(?=.*\\p{L})[\\p{L} '-]+");
    }

    /**
     * Returns true if a given string is a valid name.
     */
    public static boolean isValidName(String test) {
        return test.matches(VALIDATION_REGEX);
    }


    @Override
    public String toString() {
        return fullName;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof Name otherName)) {
            return false;
        }

        return fullName.equals(otherName.fullName);
    }

    @Override
    public int hashCode() {
        return fullName.hashCode();
    }

}
