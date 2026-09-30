package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

import seedu.address.model.AddressBook;

public class ClientTest {
    @Test
    public void reference_isNormalizedAndValidated() {
        assertEquals("SG-1024", new ClientReference(" sg-1024 ").value);
        assertThrows(IllegalArgumentException.class, ClientReference.MESSAGE_CONSTRAINTS, () ->
                new ClientReference("1ABC"));
        assertThrows(IllegalArgumentException.class, ClientReference.MESSAGE_CONSTRAINTS, () ->
                new ClientReference("A".repeat(21)));
    }

    @Test
    public void client_hasReferenceIdentityAndNormalizedName() {
        Client first = client("c0001", "  Mary   O'Connor  ");
        Client sameReference = client("C0001", "Another Name");
        Client sameName = client("C0002", "Mary O'Connor");

        assertEquals("C0001", first.getReference().value);
        assertEquals("Mary O'Connor", first.getName().fullName);
        assertTrue(first.isSamePerson(sameReference));
        assertFalse(first.isSamePerson(sameName));

        AddressBook book = new AddressBook();
        book.addPerson(first);
        assertTrue(book.hasPerson(sameReference));
        assertFalse(book.hasPerson(sameName));
        book.addPerson(sameName);
    }

    @Test
    public void client_invalidField_throwsFieldSpecificMessage() {
        assertThrows(IllegalArgumentException.class, Name.CLIENT_MESSAGE_CONSTRAINTS, () ->
                client("C0001", "Alex 2"));
        assertThrows(IllegalArgumentException.class, Phone.CLIENT_MESSAGE_CONSTRAINTS, () ->
                new Client(new ClientReference("C0001"), "Alex", "12", "alex@example.com", "Main St"));
        assertThrows(IllegalArgumentException.class, Address.CLIENT_MESSAGE_CONSTRAINTS, () ->
                new Client(new ClientReference("C0001"), "Alex", "123", "alex@example.com", "\n"));
    }

    private static Client client(String reference, String name) {
        return new Client(new ClientReference(reference), name, "87438807", "alex@example.com", "Main St");
    }
}
