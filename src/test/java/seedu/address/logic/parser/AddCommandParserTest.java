package seedu.address.logic.parser;

import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.AddCommand;
import seedu.address.model.person.Address;
import seedu.address.model.person.Client;
import seedu.address.model.person.ClientReference;
import seedu.address.model.person.Name;
import seedu.address.model.person.Phone;

public class AddCommandParserTest {
    private static final String VALID_FIELDS =
            " r/C0001 n/Alex Yeoh p/87438807 e/alex@example.com a/Blk 30, #06-40";
    private final AddCommandParser parser = new AddCommandParser();

    @Test
    public void parse_validFieldsInAnyOrder_returnsClient() {
        Client expected = new Client(new ClientReference("C0001"), "Alex Yeoh", "87438807",
                "alex@example.com", "Blk 30, #06-40");
        assertParseSuccess(parser, VALID_FIELDS, new AddCommand(expected));
        assertParseSuccess(parser,
                " n/Alex   Yeoh e/alex@example.com r/c0001 a/Blk 30, #06-40 p/87438807",
                new AddCommand(expected));
    }

    @Test
    public void parse_missingOrRepeatedField_reportsPrefix() {
        assertParseFailure(parser, " n/Alex Yeoh p/87438807 e/alex@example.com a/Main St",
                "Missing required parameter: r/. Format: " + AddCommand.CLIENT_FORMAT);
        assertParseFailure(parser, VALID_FIELDS.replace(" p/87438807", ""),
                "Missing required parameter: p/. Format: " + AddCommand.CLIENT_FORMAT);
        assertParseFailure(parser, VALID_FIELDS + " r/C0002", "Parameter r/ was specified more than once.");
    }

    @Test
    public void parse_unknownPrefixOrUnprefixedText_rejected() {
        String expected = "Invalid add command. Format: " + AddCommand.CLIENT_FORMAT;
        assertParseFailure(parser, " surprise" + VALID_FIELDS, expected);
        assertParseFailure(parser, VALID_FIELDS + " t/friend", expected);
        assertParseFailure(parser, VALID_FIELDS + " x/unknown", expected);
    }

    @Test
    public void parse_invalidValues_reportsFirstFieldInCommandOrder() {
        assertParseFailure(parser, VALID_FIELDS.replace("C0001", "1INVALID"),
                ClientReference.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, VALID_FIELDS.replace("Alex Yeoh", "Alex 2"),
                Name.CLIENT_MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, VALID_FIELDS.replace("87438807", "12"),
                Phone.CLIENT_MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, VALID_FIELDS.replace("alex@example.com", "alex@localhost"),
                Client.EMAIL_MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, VALID_FIELDS.replace("Blk 30, #06-40", " "),
                Address.CLIENT_MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, " p/12 n/Alex 2 r/C0001 e/alex@example.com a/Main St",
                Phone.CLIENT_MESSAGE_CONSTRAINTS);
    }
}
