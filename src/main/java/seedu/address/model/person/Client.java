package seedu.address.model.person;

import static java.util.Objects.requireNonNull;

import java.util.Objects;
import java.util.Set;

/**
 * A client with a stable reference and the contact details used by the existing address book.
 */
public final class Client extends Person {
    public static final String EMAIL_MESSAGE_CONSTRAINTS =
            "Email must be in the form local-part@domain, for example alex@example.com.";

    private final ClientReference reference;

    /**
     * Creates a client from a reference and four contact fields.
     */
    public Client(ClientReference reference, String name, String phone, String email, String address) {
        super(Name.forClient(name), Phone.forClient(phone), Email.forClient(email),
                Address.forClient(address), Set.of());
        this.reference = requireNonNull(reference);
    }

    public ClientReference getReference() {
        return reference;
    }

    @Override
    public boolean isSamePerson(Person otherPerson) {
        return otherPerson instanceof Client otherClient && reference.equals(otherClient.reference);
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof Client otherClient && super.equals(otherClient)
                && reference.equals(otherClient.reference);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), reference);
    }

    @Override
    public String toString() {
        return "Client{" + "reference=" + reference + ", " + super.toString() + "}";
    }
}
