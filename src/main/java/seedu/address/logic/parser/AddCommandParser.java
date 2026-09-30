package seedu.address.logic.parser;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import seedu.address.logic.commands.AddCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.Address;
import seedu.address.model.person.Client;
import seedu.address.model.person.ClientReference;
import seedu.address.model.person.Email;
import seedu.address.model.person.Name;
import seedu.address.model.person.Phone;

/**
 * Parses the five required fields of the add-client command.
 */
public class AddCommandParser implements Parser<AddCommand> {
    private static final Pattern PREFIX_PATTERN = Pattern.compile("(?<!\\S)([A-Za-z]+/)");
    private static final Set<String> REQUIRED_PREFIXES = Set.of("r/", "n/", "p/", "e/", "a/");
    private static final String MESSAGE_INVALID = "Invalid add command. Format: " + AddCommand.CLIENT_FORMAT;

    @Override
    public AddCommand parse(String args) throws ParseException {
        Matcher matcher = PREFIX_PATTERN.matcher(args);
        Map<String, String> values = new LinkedHashMap<>();
        int previousValueStart = -1;
        String previousPrefix = null;

        while (matcher.find()) {
            if (previousPrefix == null && !args.substring(0, matcher.start()).isBlank()) {
                throw new ParseException(MESSAGE_INVALID);
            }
            if (previousPrefix != null) {
                values.put(previousPrefix, args.substring(previousValueStart, matcher.start()).trim());
            }
            String prefix = matcher.group(1);
            if (!REQUIRED_PREFIXES.contains(prefix)) {
                throw new ParseException(MESSAGE_INVALID);
            }
            if (values.containsKey(prefix) || prefix.equals(previousPrefix)) {
                throw new ParseException("Parameter " + prefix + " was specified more than once.");
            }
            previousPrefix = prefix;
            previousValueStart = matcher.end();
        }

        if (previousPrefix == null && !args.isBlank()) {
            throw new ParseException(MESSAGE_INVALID);
        }
        if (previousPrefix != null) {
            values.put(previousPrefix, args.substring(previousValueStart).trim());
        }

        for (String prefix : new String[] {"r/", "n/", "p/", "e/", "a/"}) {
            if (!values.containsKey(prefix)) {
                throw new ParseException("Missing required parameter: " + prefix + ". Format: "
                        + AddCommand.CLIENT_FORMAT);
            }
        }

        ClientReference reference = null;
        String name = null;
        String phone = null;
        String email = null;
        String address = null;
        for (Map.Entry<String, String> entry : values.entrySet()) {
            String value = entry.getValue();
            switch (entry.getKey()) {
                case "r/" -> {
                    if (!ClientReference.isValidReference(value)) {
                        throw new ParseException(ClientReference.MESSAGE_CONSTRAINTS);
                    }
                    reference = new ClientReference(value);
                }
                case "n/" -> {
                    name = value.replaceAll(" +", " ");
                    if (!Name.isValidClientName(name)) {
                        throw new ParseException(Name.CLIENT_MESSAGE_CONSTRAINTS);
                    }
                }
                case "p/" -> {
                    if (!Phone.isValidClientPhone(value)) {
                        throw new ParseException(Phone.CLIENT_MESSAGE_CONSTRAINTS);
                    }
                    phone = value;
                }
                case "e/" -> {
                    if (!Email.isValidClientEmail(value)) {
                        throw new ParseException(Client.EMAIL_MESSAGE_CONSTRAINTS);
                    }
                    email = value;
                }
                case "a/" -> {
                    if (!Address.isValidClientAddress(value)) {
                        throw new ParseException(Address.CLIENT_MESSAGE_CONSTRAINTS);
                    }
                    address = value;
                }
                default -> throw new AssertionError("Unexpected prefix: " + entry.getKey());
            }
        }

        return new AddCommand(new Client(reference, name, phone, email, address));
    }
}
