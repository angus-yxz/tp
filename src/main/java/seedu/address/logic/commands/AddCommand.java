package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Client;
import seedu.address.model.person.Person;

/**
 * Adds a person to the address book.
 */
public class AddCommand extends Command {

    public static final String COMMAND_WORD = "add";
    public static final String CLIENT_FORMAT = "add r/REFERENCE n/NAME p/PHONE e/EMAIL a/ADDRESS";
    public static final String MESSAGE_CLIENT_SUCCESS = "Added client: %1$s %2$s";
    public static final String MESSAGE_DUPLICATE_CLIENT = "A client with reference %s already exists.";
    public static final String MESSAGE_CLIENT_SAVE_FAILURE = "Unable to save client data. No client was added.";

    public static final String MESSAGE_USAGE = "Adds a client. Format: " + CLIENT_FORMAT;

    public static final String MESSAGE_SUCCESS = "New person added: %1$s";
    public static final String MESSAGE_DUPLICATE_PERSON = "This person already exists in the address book.";

    private final Person toAdd;

    /**
     * Creates an AddCommand to add the specified {@code Person}
     */
    public AddCommand(Person person) {
        requireNonNull(person);
        toAdd = person;
    }

    public Person getPersonToAdd() {
        return toAdd;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        if (model.hasPerson(toAdd)) {
            if (toAdd instanceof Client client) {
                throw new CommandException(String.format(MESSAGE_DUPLICATE_CLIENT, client.getReference()));
            }
            throw new CommandException(MESSAGE_DUPLICATE_PERSON);
        }

        model.addPerson(toAdd);
        if (toAdd instanceof Client client) {
            return new CommandResult(String.format(MESSAGE_CLIENT_SUCCESS,
                    client.getReference(), client.getName()));
        }
        return new CommandResult(String.format(MESSAGE_SUCCESS, Messages.format(toAdd)));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof AddCommand otherAddCommand)) {
            return false;
        }

        return toAdd.equals(otherAddCommand.toAdd);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("toAdd", toAdd)
                .toString();
    }
}
