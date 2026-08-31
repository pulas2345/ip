package pulbot;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

/** Tests Pulbot's date and time conversion methods. */
public class PulbotTest {
    @Test
    public void parseDateTime_validInput_returnsDateTime() {
        LocalDateTime expected = LocalDateTime.of(2019, 12, 2, 18, 0);

        assertEquals(expected, Pulbot.parseDateTime("2/12/2019 1800"));
        assertEquals(expected, Pulbot.parseDateTime(" 2/12/2019 1800 "));
    }

    @Test
    public void parseDateTime_invalidInput_throwsException() {
        assertThrows(IllegalArgumentException.class,
                () -> Pulbot.parseDateTime("2019-12-02 18:00"));
        assertThrows(IllegalArgumentException.class,
                () -> Pulbot.parseDateTime("31/2/2019 1800"));
    }

    @Test
    public void formatDateTime_validDateTime_returnsDisplayFormat() {
        LocalDateTime dateTime = LocalDateTime.of(2019, 12, 2, 18, 0);

        assertEquals("Dec 02 2019 6:00 PM", Pulbot.formatDateTime(dateTime));
        assertEquals("Jan 01 2020 12:05 AM",
                Pulbot.formatDateTime(LocalDateTime.of(2020, 1, 1, 0, 5)));
    }

    @Test
    public void main_byeCommand_startsAndExitsApplication() {
        InputStream originalIn = System.in;
        PrintStream originalOut = System.out;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try {
            System.setIn(new ByteArrayInputStream("bye\n".getBytes(StandardCharsets.UTF_8)));
            System.setOut(new PrintStream(output, true, StandardCharsets.UTF_8));

            Pulbot.main(new String[0]);
        } finally {
            System.setIn(originalIn);
            System.setOut(originalOut);
        }

        String displayed = output.toString(StandardCharsets.UTF_8);
        assertTrue(displayed.contains("Hello! I'm PulBot."));
        assertTrue(displayed.contains("Bye."));
    }
}
