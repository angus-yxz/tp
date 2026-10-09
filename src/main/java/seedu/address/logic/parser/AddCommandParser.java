package seedu.address.logic.parser;

import static seedu.address.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CARD_NUMBER;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CREDIT_SCORE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CVV;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DEBT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EXPIRY_DATE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PROVIDER;
import static seedu.address.logic.parser.CliSyntax.PREFIX_REFERENCE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;

import seedu.address.logic.commands.AddCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.Address;
import seedu.address.model.person.CardDetails;
import seedu.address.model.person.ClientReference;
import seedu.address.model.person.CreditScore;
import seedu.address.model.person.Debt;
import seedu.address.model.person.Email;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;
import seedu.address.model.tag.Tag;

/**
 * Parses client references and contact fields alongside the existing card, financial, and tag parameters.
 */
public class AddCommandParser implements Parser<AddCommand> {
    public static final String MESSAGE_INVALID_ADD = "Invalid add command. " + AddCommand.MESSAGE_USAGE;
    public static final String MESSAGE_MISSING_PARAMETER =
            "Missing required parameter: %s. " + AddCommand.MESSAGE_USAGE;
    public static final String MESSAGE_REPEATED_PARAMETER = "Parameter %s was specified more than once.";

    private static final List<Prefix> REQUIRED_PREFIXES = List.of(PREFIX_REFERENCE, PREFIX_NAME, PREFIX_PHONE,
            PREFIX_EMAIL, PREFIX_ADDRESS, PREFIX_CARD_NUMBER, PREFIX_CVV, PREFIX_EXPIRY_DATE,
            PREFIX_PROVIDER, PREFIX_CREDIT_SCORE, PREFIX_DEBT);
    private static final Pattern PARAMETER_PREFIX = Pattern.compile("(?<!\\S)([a-zA-Z][a-zA-Z0-9_-]*/)");

    @Override
    public AddCommand parse(String args) throws ParseException {
        List<Map.Entry<Prefix, String>> fields = extractFields(args);
        for (Prefix prefix : REQUIRED_PREFIXES) {
            if (fields.stream().noneMatch(field -> field.getKey().equals(prefix))) {
                throw new ParseException(String.format(MESSAGE_MISSING_PARAMETER, prefix));
            }
        }

        ClientReference reference = null;
        Name name = null;
        Phone phone = null;
        Email email = null;
        Address address = null;
        String cardNumber = null;
        String cvv = null;
        String expiryDate = null;
        String provider = null;
        CreditScore creditScore = null;
        Debt debt = null;
        Set<Tag> tags = new HashSet<>();
        // Validate in command order, including each occurrence of an optional tag.
        for (Map.Entry<Prefix, String> field : fields) {
            String value = field.getValue();
            switch (field.getKey().getPrefix()) {
                case "r/" -> reference = ParserUtil.parseClientReference(value);
                case "n/" -> name = ParserUtil.parseName(value);
                case "p/" -> phone = ParserUtil.parsePhone(value);
                case "e/" -> email = ParserUtil.parseEmail(value);
                case "a/" -> address = ParserUtil.parseAddress(value);
                case "cn/" -> cardNumber = ParserUtil.parseCardNumber(value);
                case "cvv/" -> cvv = ParserUtil.parseCvv(value);
                case "exp/" -> expiryDate = ParserUtil.parseExpiryDate(value);
                case "provider/" -> provider = ParserUtil.parseProvider(value);
                case "cs/" -> creditScore = ParserUtil.parseCreditScore(value);
                case "d/" -> debt = ParserUtil.parseDebt(value);
                case "t/" -> tags.add(ParserUtil.parseTag(value));
                default -> throw new ParseException(MESSAGE_INVALID_ADD);
            }
        }
        CardDetails cardDetails = new CardDetails(cardNumber, cvv, expiryDate, provider);
        return new AddCommand(new Person(reference, name, phone, email, address, cardDetails, creditScore, debt, tags));
    }

    private List<Map.Entry<Prefix, String>> extractFields(String args) throws ParseException {
        List<MatchResult> matches = PARAMETER_PREFIX.matcher(args).results().toList();
        int preambleEnd = matches.isEmpty() ? args.length() : matches.getFirst().start();
        if (!args.substring(0, preambleEnd).isBlank()) {
            throw new ParseException(MESSAGE_INVALID_ADD);
        }

        List<Map.Entry<Prefix, String>> fields = new ArrayList<>();
        Set<Prefix> seen = new HashSet<>();
        for (int i = 0; i < matches.size(); i++) {
            MatchResult match = matches.get(i);
            Prefix prefix = new Prefix(match.group());
            if (!REQUIRED_PREFIXES.contains(prefix) && !PREFIX_TAG.equals(prefix)) {
                throw new ParseException(MESSAGE_INVALID_ADD);
            }
            if (!seen.add(prefix) && !PREFIX_TAG.equals(prefix)) {
                throw new ParseException(String.format(MESSAGE_REPEATED_PARAMETER, prefix));
            }
            int valueEnd = i + 1 < matches.size() ? matches.get(i + 1).start() : args.length();
            fields.add(Map.entry(prefix, args.substring(match.end(), valueEnd).trim()));
        }
        return fields;
    }
}
