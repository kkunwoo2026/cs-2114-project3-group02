package src;

import static org.junit.Assert.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

/**
 * Tests the functionality of the HokieHold controller.
 * Tests queue management, room offers, session handling,
 * room searches, and invalid input.
 *
 * @author Zachary
 * @version 2026.11.16
 */
public class ControllerMainTest {

    /** View used by the controller during testing. */
    private ViewMain view;

    /** Controller being tested. */
    private ControllerMain controller;

    /** Study room used in controller tests. */
    private Room studyRoom;

    /** Meeting room used in controller tests. */
    private Room meetingRoom;

    /**
     * Sets up a new controller and test rooms before each test.
     */
    @Before
    public void setUp() {

        view = new ViewMain();

        studyRoom = createRoom("S101", "Study", 4);
        meetingRoom = createRoom("M201", "Meeting", 8);

        List<Room> rooms = new ArrayList<Room>();
        rooms.add(studyRoom);
        rooms.add(meetingRoom);

        controller = new ControllerMain(rooms, view);
    }

    /**
     * Creates a room with standard test date and time information.
     *
     * @param number room number
     * @param category room category
     * @param capacity maximum room capacity
     * @return newly created room
     */
    private Room createRoom(
            String number, String category, int capacity) {

        return new Room(
                number,
                category,
                capacity,
                "Main",
                LocalDate.now(),
                LocalTime.of(0, 0),
                LocalTime.of(0, 30));
    }

    /**
     * Creates a test student.
     *
     * @param pid student's HokiePID
     * @param name student's name
     * @return newly created user
     */
    private User createUser(String pid, String name) {
        return new User(pid, name);
    }

    /**
     * Reserves a room so students joining its line remain queued.
     *
     * @param room room to occupy
     */
    private void occupyRoom(Room room) {
        room.reserve(createUser("BLOCKER", "Blocker"));
    }

    /**
     * Tests that the controller initializes its rooms and queues.
     */
    @Test
    public void testConstructor() {

        assertNotNull(controller);
        assertEquals(2, controller.getRooms().size());
        assertEquals(0, controller.getStudyQueueSize());
        assertEquals(0, controller.getMeetingQueueSize());
    }

    /**
     * Tests that a null room list is rejected.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testConstructorWithNullRooms() {
        new ControllerMain(null, view);
    }

    /**
     * Tests that a null view is rejected.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testConstructorWithNullView() {
        new ControllerMain(Arrays.asList(studyRoom), null);
    }

    /**
     * Tests that a room list containing null is rejected.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testConstructorWithNullRoomInList() {

        List<Room> rooms = new ArrayList<Room>();
        rooms.add(null);

        new ControllerMain(rooms, view);
    }

    /**
     * Tests that a student can join the Study waiting line.
     */
    @Test
    public void testJoinStudyQueue() {

        occupyRoom(studyRoom);

        User user = createUser("PID1", "Alex");

        controller.joinQueue(user, "Study", 2);

        assertEquals(1, controller.getStudyQueueSize());
        assertEquals(1,
                controller.getQueuePosition(user, "Study"));
    }

    /**
     * Tests that a student can join the Meeting waiting line.
     */
    @Test
    public void testJoinMeetingQueue() {

        occupyRoom(meetingRoom);

        User user = createUser("PID2", "Sam");

        controller.joinQueue(user, "Meeting", 5);

        assertEquals(1, controller.getMeetingQueueSize());
        assertEquals(1,
                controller.getQueuePosition(user, "Meeting"));
    }

    /**
     * Tests that the same student cannot join a waiting line twice.
     */
    @Test
    public void testDuplicateUserDoesNotJoinTwice() {

        occupyRoom(studyRoom);

        User user = createUser("PID3", "Jordan");

        controller.joinQueue(user, "Study", 2);
        controller.joinQueue(user, "Study", 2);

        assertEquals(1, controller.getStudyQueueSize());
    }

    /**
     * Tests that joining with a null user is rejected.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testJoinQueueWithNullUser() {
        controller.joinQueue(null, "Study", 2);
    }

    /**
     * Tests that an unsupported room category is rejected.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testJoinQueueWithInvalidCategory() {
        controller.joinQueue(
                createUser("PID4", "Taylor"), "Other", 2);
    }

    /**
     * Tests that a group smaller than the minimum size is rejected.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testJoinQueueWithInvalidGroupSize() {
        controller.joinQueue(
                createUser("PID5", "Chris"), "Study", 1);
    }

    /**
     * Tests that Study rooms reject groups larger than four.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testStudyGroupTooLarge() {
        controller.joinQueue(
                createUser("PID6", "Morgan"), "Study", 5);
    }

    /**
     * Tests that groups larger than eight are rejected.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testMeetingGroupTooLarge() {
        controller.joinQueue(
                createUser("PID7", "Jamie"), "Meeting", 9);
    }

    /**
     * Tests that students retain their original queue order.
     */
    @Test
    public void testQueueOrder() {

        occupyRoom(studyRoom);

        User first = createUser("PID8", "First");
        User second = createUser("PID9", "Second");
        User third = createUser("PID10", "Third");

        controller.joinQueue(first, "Study", 2);
        controller.joinQueue(second, "Study", 2);
        controller.joinQueue(third, "Study", 2);

        assertEquals(1,
                controller.getQueuePosition(first, "Study"));
        assertEquals(2,
                controller.getQueuePosition(second, "Study"));
        assertEquals(3,
                controller.getQueuePosition(third, "Study"));
    }

    /**
     * Tests that the first group fitting a room receives its offer.
     * A larger group remains in its original queue position.
     */
    @Test
    public void testOfferNextRoomSelectsFirstEligibleUser() {

        Room smallRoom = createRoom("S102", "Study", 2);

        smallRoom.reserve(createUser("BLOCKER2", "Blocker"));

        ControllerMain smallController =
                new ControllerMain(Arrays.asList(smallRoom), view);

        User largeGroup = createUser("PID11", "Large Group");
        User smallGroup = createUser("PID12", "Small Group");

        smallController.joinQueue(largeGroup, "Study", 4);
        smallController.joinQueue(smallGroup, "Study", 2);

        smallRoom.release();

        User offered = smallController.offerNextRoom(smallRoom);

        assertEquals(smallGroup, offered);
        assertEquals(smallGroup, smallRoom.getOfferedTo());
        assertEquals(1,
                smallController.getQueuePosition(largeGroup, "Study"));
    }

    /**
     * Tests that offering a null room is rejected.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testOfferNextRoomWithNull() {
        controller.offerNextRoom(null);
    }

    /**
     * Tests that a room not managed by the controller is rejected.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testOfferNextRoomWithUnmanagedRoom() {
        controller.offerNextRoom(createRoom("S999", "Study", 4));
    }

    /**
     * Tests that a student can accept an offer for 60 minutes.
     */
    @Test
    public void testAcceptOffer() {

        User user = createUser("PID13", "Alex");

        controller.joinQueue(user, "Study", 2);

        assertEquals(user, studyRoom.getOfferedTo());

        boolean accepted = controller.acceptOffer(user, 60);

        assertTrue(accepted);
        assertEquals(user, studyRoom.getReservedBy());
        assertNotNull(controller.getSessionEndTime(studyRoom));
    }

    /**
     * Tests that a student can accept the default 30-minute offer.
     */
    @Test
    public void testAcceptOfferDefaultDuration() {

        User user = createUser("PID14", "Sam");

        controller.joinQueue(user, "Study", 2);

        assertTrue(controller.acceptOffer(user));
        assertNotNull(controller.getSessionEndTime(studyRoom));
    }

    /**
     * Tests that accepting without a pending offer returns false.
     */
    @Test
    public void testAcceptOfferWithoutPendingOffer() {

        User user = createUser("PID15", "Jordan");

        assertFalse(controller.acceptOffer(user, 30));
    }

    /**
     * Tests that accepting an offer with a null user is rejected.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAcceptOfferWithNullUser() {
        controller.acceptOffer(null, 30);
    }

    /**
     * Tests that an unsupported session duration is rejected.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAcceptOfferWithInvalidDuration() {
        controller.acceptOffer(
                createUser("PID16", "Taylor"), 45);
    }

    /**
     * Tests that declining an offer passes it to the next student.
     */
    @Test
    public void testDeclineOffer() {

        User first = createUser("PID17", "First");
        User second = createUser("PID18", "Second");

        controller.joinQueue(first, "Study", 2);
        controller.joinQueue(second, "Study", 2);

        assertEquals(first, studyRoom.getOfferedTo());

        assertTrue(controller.declineOffer(first));

        assertEquals(second, studyRoom.getOfferedTo());
    }

    /**
     * Tests that declining without a pending offer returns false.
     */
    @Test
    public void testDeclineWithoutOffer() {
        assertFalse(controller.declineOffer(
                createUser("PID19", "Morgan")));
    }

    /**
     * Tests that declining an offer with a null user is rejected.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testDeclineWithNullUser() {
        controller.declineOffer(null);
    }

    /**
     * Tests that a student can leave a waiting line.
     */
    @Test
    public void testLeaveQueue() {

        occupyRoom(studyRoom);

        User user = createUser("PID20", "Jamie");

        controller.joinQueue(user, "Study", 2);

        assertTrue(controller.leaveQueue(user, "Study"));
        assertEquals(0, controller.getStudyQueueSize());
        assertFalse(controller.leaveQueue(user, "Study"));
    }

    /**
     * Tests that leaving a queue with a null user is rejected.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testLeaveQueueWithNullUser() {
        controller.leaveQueue(null, "Study");
    }

    /**
     * Tests that leaving a queue with an invalid category is rejected.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testLeaveQueueWithInvalidCategory() {
        controller.leaveQueue(
                createUser("PID21", "Chris"), "Invalid");
    }

    /**
     * Tests that ending a session releases the room
     * and offers it to the next eligible student.
     */
    @Test
    public void testEndRoomSession() {

        User first = createUser("PID22", "First");
        User second = createUser("PID23", "Second");

        controller.joinQueue(first, "Study", 2);
        controller.acceptOffer(first, 30);
        controller.joinQueue(second, "Study", 2);

        User previousUser = controller.endRoomSession(studyRoom);

        assertEquals(first, previousUser);
        assertEquals(second, studyRoom.getOfferedTo());
        assertNull(controller.getSessionEndTime(studyRoom));
    }

    /**
     * Tests that ending a session with a null room is rejected.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testEndSessionWithNullRoom() {
        controller.endRoomSession(null);
    }

    /**
     * Tests that an active session has an expected end time.
     */
    @Test
    public void testSessionEndTime() {

        User user = createUser("PID24", "Alex");

        controller.joinQueue(user, "Study", 2);
        controller.acceptOffer(user, 60);

        assertNotNull(controller.getSessionEndTime(studyRoom));
    }

    /**
     * Tests that a room without an active session has no end time.
     */
    @Test
    public void testSessionEndTimeWithoutSession() {
        assertNull(controller.getSessionEndTime(studyRoom));
    }

    /**
     * Tests that checking an unmanaged room's end time is rejected.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testSessionEndTimeWithUnmanagedRoom() {
        controller.getSessionEndTime(
                createRoom("S999", "Study", 4));
    }

    /**
     * Tests that a missing student has queue position -1.
     */
    @Test
    public void testGetQueuePositionForMissingUser() {
        assertEquals(-1,
                controller.getQueuePosition(
                        createUser("PID25", "Taylor"), "Study"));
    }

    /**
     * Tests that a null user has queue position -1.
     */
    @Test
    public void testGetQueuePositionForNullUser() {
        assertEquals(-1,
                controller.getQueuePosition(null, "Study"));
    }

    /**
     * Tests that room searches return rooms matching the request.
     */
    @Test
    public void testSearchRooms() {

        List<Room> matches = controller.searchRooms("Study", 2);

        assertEquals(1, matches.size());
        assertEquals(studyRoom, matches.get(0));
    }

    /**
     * Tests that occupied rooms are excluded from search results.
     */
    @Test
    public void testSearchRoomsExcludesOccupiedRooms() {

        studyRoom.reserve(createUser("BLOCKER3", "Blocker"));

        List<Room> matches = controller.searchRooms("Study", 2);

        assertTrue(matches.isEmpty());
    }

    /**
     * Tests that room searches reject an invalid category.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testSearchRoomsInvalidCategory() {
        controller.searchRooms("Invalid", 2);
    }

    /**
     * Tests that room searches reject an invalid group size.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testSearchRoomsInvalidGroupSize() {
        controller.searchRooms("Meeting", 10);
    }

    /**
     * Tests that the controller does not expose a modifiable room list.
     */
    @Test
    public void testGetRoomsIsUnmodifiable() {

        try {
            controller.getRooms().add(
                    createRoom("S999", "Study", 4));

            fail("Room list should not be modifiable.");
        }
        catch (UnsupportedOperationException expected) {
            // The exception is the expected result.
        }
    }

    /**
     * Tests that refreshing the view does not change queue sizes.
     */
    @Test
    public void testRefreshView() {

        controller.refreshView();

        assertEquals(0, controller.getStudyQueueSize());
        assertEquals(0, controller.getMeetingQueueSize());
    }

    /**
     * Tests that available rooms receive offers from waiting students.
     */
    @Test
    public void testOfferAvailableRooms() {

        User user = createUser("PID26", "Morgan");

        controller.joinQueue(user, "Study", 2);

        assertEquals(user, studyRoom.getOfferedTo());

        controller.offerAvailableRooms();

        assertEquals(user, studyRoom.getOfferedTo());
    }
}