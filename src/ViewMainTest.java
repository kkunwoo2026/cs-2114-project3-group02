package src;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests the HokieHold console view.
 * Verifies displayed messages, room information,
 * queue information, and student profile handling.
 *
 * @author Zachary
 * @version 2026.11.16
 */
public class ViewMainTest {

    /** View being tested. */
    private ViewMain view;

    /** Captures output printed by the view. */
    private ByteArrayOutputStream output;

    /** Original console output stream. */
    private PrintStream originalOutput;

    /** Room used in display tests. */
    private Room studyRoom;

    /**
     * Sets up a new view and captures console output.
     */
    @Before
    public void setUp() {

        view = new ViewMain();

        output = new ByteArrayOutputStream();
        originalOutput = System.out;

        System.setOut(new PrintStream(output));

        studyRoom = new Room(
                "S101",
                "Study",
                4,
                "Main",
                LocalDate.now(),
                LocalTime.of(0, 0),
                LocalTime.of(0, 30));
    }

    /**
     * Restores the original console output after each test.
     */
    @After
    public void restoreOutput() {
        System.setOut(originalOutput);
    }

    /**
     * Returns the text captured from console output.
     *
     * @return captured console output
     */
    private String getOutput() {
        return output.toString();
    }

    /**
     * Tests that the view displays a regular message.
     */
    @Test
    public void testDisplayMessage() {

        view.displayMessage("Hello HokieHold");

        assertTrue(getOutput().contains("Hello HokieHold"));
    }

    /**
     * Tests that both waiting-line counts are displayed.
     */
    @Test
    public void testDisplayQueueStatus() {

        view.displayQueueStatus(3, 5);

        assertTrue(getOutput().contains("Study room line: 3"));
        assertTrue(getOutput().contains("Meeting room line: 5"));
    }

    /**
     * Tests that empty waiting lines display zero counts.
     */
    @Test
    public void testDisplayEmptyQueueStatus() {

        view.displayQueueStatus(0, 0);

        assertTrue(getOutput().contains("Study room line: 0"));
        assertTrue(getOutput().contains("Meeting room line: 0"));
    }

    /**
     * Tests that a positive queue position is displayed correctly.
     */
    @Test
    public void testDisplayQueuePosition() {

        view.displayQueuePosition("Study", 2);

        assertTrue(getOutput().contains(
                "Your position in the Study line is 2."));
    }

    /**
     * Tests that a missing queue position is displayed correctly.
     */
    @Test
    public void testDisplayMissingQueuePosition() {

        view.displayQueuePosition("Meeting", -1);

        assertTrue(getOutput().contains(
                "You are not currently in the Meeting line."));
    }

    /**
     * Tests that room information and availability are displayed.
     */
    @Test
    public void testDisplayRooms() {

        view.displayRooms(Arrays.asList(studyRoom));

        assertTrue(getOutput().contains("S101"));
        assertTrue(getOutput().contains("Study"));
        assertTrue(getOutput().contains("Capacity: 4"));
        assertTrue(getOutput().contains("Available"));
    }

    /**
     * Tests the message displayed when no rooms are configured.
     */
    @Test
    public void testDisplayEmptyRoomList() {

        view.displayRooms(Arrays.asList());

        assertTrue(getOutput().contains(
                "No rooms are currently configured."));
    }

    /**
     * Tests that an offered room displays the student's name.
     */
    @Test
    public void testDisplayRoomOfferedToStudent() {

        User user = new User("PID1", "Alex");

        studyRoom.offerTo(user);

        view.displayRooms(Arrays.asList(studyRoom));

        assertTrue(getOutput().contains("Offered to Alex"));
    }

    /**
     * Tests that an occupied room displays the reserved student's name.
     */
    @Test
    public void testDisplayRoomInUse() {

        User user = new User("PID2", "Sam");

        studyRoom.reserve(user);

        view.displayRooms(Arrays.asList(studyRoom));

        assertTrue(getOutput().contains("In use by Sam"));
    }

    /**
     * Tests that the view accepts a valid student profile.
     */
    @Test
    public void testRememberUser() {

        User user = new User("PID3", "Jordan");

        view.rememberUser(user);

        assertNotNull(user);
    }

    /**
     * Tests that the view rejects a null student profile.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testRememberNullUser() {
        view.rememberUser(null);
    }
}
