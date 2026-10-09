package seedu.address.logic.parser;

import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.AddCommand;
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
import seedu.address.testutil.PersonBuilder;

public class AddCommandParserTest {
    private static final List<String> FIELDS = List.of("r/C0001", "n/Alex Yeoh", "p/87438807",
            "e/alex@example.com", "a/Blk 30 Geylang Street 29, #06-40", "cn/4111111111111111", "cvv/123",
            "exp/12/28", "provider/Visa", "cs/700", "d/0.00");
    private static final String VALID_ARGUMENTS = " " + String.join(" ", FIELDS);
    private static final String FINANCIAL_ARGUMENTS = " " + String.join(" ", FIELDS.subList(5, FIELDS.size()));
    private final AddCommandParser parser = new AddCommandParser();

    @Test
    public void parse_referenceWithAllCommittedFields_success() {
        Person expected = new PersonBuilder().withClientReference("C0001").withName("Alex Yeoh")
                .withPhone("87438807").withEmail("alex@example.com")
                .withAddress("Blk 30 Geylang Street 29, #06-40").build();
        assertParseSuccess(parser, VALID_ARGUMENTS, new AddCommand(expected));
    }

    @Test
    public void parse_reorderedFields_normalizesReferenceAndNameSpacing() {
        Person expected = new PersonBuilder().withClientReference("SG-1024").withName("Mary O'Connor")
                .withPhone("91234567").withEmail("Mary.oconnor@example.com").withAddress("10  Main Street").build();
        assertParseSuccess(parser, "  n/  Mary   O'Connor  e/ Mary.oconnor@example.com "
                + "r/ sg-1024  a/ 10  Main Street  p/91234567  " + FINANCIAL_ARGUMENTS, new AddCommand(expected));
    }

    @Test
    public void parse_missingRequiredField_reportsItsPrefix() {
        for (int i = 0; i < FIELDS.size(); i++) {
            List<String> fields = new ArrayList<>(FIELDS);
            String removed = fields.remove(i);
            assertParseFailure(parser, " " + String.join(" ", fields),
                    String.format(AddCommandParser.MESSAGE_MISSING_PARAMETER, prefixOf(removed)));
        }
        assertParseFailure(parser, "", String.format(AddCommandParser.MESSAGE_MISSING_PARAMETER, "r/"));
    }

    @Test
    public void parse_repeatedRequiredField_reportsItsPrefix() {
        for (String field : FIELDS) {
            assertParseFailure(parser, VALID_ARGUMENTS + " " + field,
                    String.format(AddCommandParser.MESSAGE_REPEATED_PARAMETER, prefixOf(field)));
        }
    }

    @Test
    public void parse_optionalTags_preservesExistingSupport() {
        Person expected = new PersonBuilder().withClientReference("C0001").withName("Alex Yeoh")
                .withPhone("87438807").withEmail("alex@example.com")
                .withAddress("Blk 30 Geylang Street 29, #06-40").withTags("friends", "owesMoney").build();
        assertParseSuccess(parser, VALID_ARGUMENTS + " t/friends t/owesMoney t/friends", new AddCommand(expected));
        assertParseFailure(parser, VALID_ARGUMENTS + " t/#invalid", Tag.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_unknownPrefixOrPreamble_rejected() {
        for (String prefix : List.of("x/", "R/", "future/")) {
            assertParseFailure(parser, VALID_ARGUMENTS + " " + prefix + "value",
                    AddCommandParser.MESSAGE_INVALID_ADD);
        }
        assertParseFailure(parser, "extra-text" + VALID_ARGUMENTS, AddCommandParser.MESSAGE_INVALID_ADD);
        assertParseFailure(parser, "extra-text", AddCommandParser.MESSAGE_INVALID_ADD);
    }

    @Test
    public void parse_blankRequiredValues_reportsFieldConstraint() {
        List<String> messages = List.of(ClientReference.MESSAGE_CONSTRAINTS, Name.MESSAGE_CONSTRAINTS,
                Phone.MESSAGE_CONSTRAINTS, Email.MESSAGE_CONSTRAINTS, Address.MESSAGE_CONSTRAINTS,
                CardDetails.CARD_NUMBER_MESSAGE_CONSTRAINTS, CardDetails.CVV_MESSAGE_CONSTRAINTS,
                CardDetails.EXPIRY_DATE_MESSAGE_CONSTRAINTS, CardDetails.PROVIDER_MESSAGE_CONSTRAINTS,
                CreditScore.MESSAGE_CONSTRAINTS, Debt.MESSAGE_CONSTRAINTS);
        for (int i = 0; i < FIELDS.size(); i++) {
            List<String> fields = new ArrayList<>(FIELDS);
            fields.set(i, prefixOf(fields.get(i)));
            assertParseFailure(parser, " " + String.join(" ", fields), messages.get(i));
        }
    }

    @Test
    public void parse_invalidContactValues_reportsSpecConstraint() {
        assertParseFailure(parser, VALID_ARGUMENTS.replace("C0001", "1C0001"), ClientReference.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, VALID_ARGUMENTS.replace("Alex Yeoh", "Alex2"), Name.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, VALID_ARGUMENTS.replace("87438807", "123-456"), Phone.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, VALID_ARGUMENTS.replace("alex@example.com", "alex@example"),
                Email.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, VALID_ARGUMENTS.replace("Blk 30 Geylang Street 29, #06-40", "Main\nStreet"),
                Address.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_invalidCardOrFinancialValues_preservesCommittedConstraints() {
        for (String expiry : List.of("13/28", "00/28", "1/28", "12/2028")) {
            assertParseFailure(parser, VALID_ARGUMENTS.replace("exp/12/28", "exp/" + expiry),
                    CardDetails.EXPIRY_DATE_MESSAGE_CONSTRAINTS);
        }
        for (String score : List.of("-1", "7.5")) {
            assertParseFailure(parser, VALID_ARGUMENTS.replace("cs/700", "cs/" + score),
                    CreditScore.MESSAGE_CONSTRAINTS);
        }
        for (String debt : List.of("-1", "1.234")) {
            assertParseFailure(parser, VALID_ARGUMENTS.replace("d/0.00", "d/" + debt), Debt.MESSAGE_CONSTRAINTS);
        }
    }

    @Test
    public void parse_multipleInvalidValues_reportsFirstInCommandOrder() {
        assertParseFailure(parser, " p/12 n/Alex2 r/C0001 e/alex@example.com a/Main Street" + FINANCIAL_ARGUMENTS,
                Phone.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, " n/Alex2 p/12 r/C0001 e/alex@example.com a/Main Street" + FINANCIAL_ARGUMENTS,
                Name.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, " a/ n/Alex Yeoh r/1Invalid p/123 e/alex@example.com" + FINANCIAL_ARGUMENTS,
                Address.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, " t/friends " + VALID_ARGUMENTS.replace("87438807", "12") + " t/#invalid",
                Phone.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, " cs/-1 " + VALID_ARGUMENTS.replace("cs/700", "").replace("Alex Yeoh", "Alex2"),
                CreditScore.MESSAGE_CONSTRAINTS);
    }

    private String prefixOf(String field) {
        return field.substring(0, field.indexOf('/') + 1);
    }
}
