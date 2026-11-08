import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controls the HokieHold room waiting-line prototype.
 * The controller owns the application logic; ViewMain only displays results.
 *
 * @author Zachary
 * @version 2026.11.08
 */
public class ControllerMain {
    private static final String STUDY = "Study";
    private static final String MEETING = "Meeting";
    private static final int MIN_GROUP_SIZE = 2;
    private static final int MAX_GROUP_SIZE = 8;
    private static final int DEFAULT_SESSION_MINUTES = 30;

    private final List<Room> rooms;
    private final WaitingList studyRoomQueue;
    private final WaitingList meetingRoomQueue;
    private final ViewMain view;

    // The existing WaitingList stores Users only, so the controller stores
    // each waiting user's requested group size separately.
    private final Map<User, Integer> requestedGroupSizes;
    private final Map<Room, LocalDateTime> sessionStartTimes;
    private final Map<Room, Integer> sessionDurations;

    /**
     * Creates a controller using the supplied rooms and view.
     *
     * @param roomList rooms managed by this controller
     * @param mainView view used to display messages
     * @throws IllegalArgumentException if either argument is null or a room is null
     */
    public ControllerMain(List<Room> roomList, ViewMain mainView) {
        if (roomList == null || mainView == null) {
            throw new IllegalArgumentException("Rooms and view cannot be null.");
        }
        this.rooms = new ArrayList<Room>();
        for (Room room : roomList) {
            if (room == null) {
                throw new IllegalArgumentException("Room list cannot contain null.");
            }
            this.rooms.add(room);
        }
        this.view = mainView;
        this.studyRoomQueue = new WaitingList(STUDY);
        this.meetingRoomQueue = new WaitingList(MEETING);
        this.requestedGroupSizes = new HashMap<User, Integer>();
        this.sessionStartTimes = new HashMap<Room, LocalDateTime>();
        this.sessionDurations = new HashMap<Room, Integer>();
    }

    /**
     * Adds a student to the appropriate live waiting line.
     *
     * @param user student joining the line
     * @param category "Study" or "Meeting" (case-insensitive)
     * @param groupSize number of people in the student's group
     */
    public void joinQueue(User user, String category, int groupSize) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null.");
        }
        String normalizedCategory = normalizeCategory(category);
        validateGroupSize(normalizedCategory, groupSize);
        if (isUserInSystem(user)) {
            view.displayMessage(user.getName() + " is already waiting for or using a room.");
            return;
        }

        WaitingList queue = getQueue(normalizedCategory);
        queue.enqueue(user);
        requestedGroupSizes.put(user, groupSize);
        view.displayMessage(user.getName() + " joined the " + normalizedCategory
            + " room line (group of " + groupSize + ").");
        offerAvailableRooms();
        refreshView();
    }

    /**
     * Offers an available room to the first waiting group that fits its capacity.
     * Students who do not fit remain in their original relative queue order.
     *
     * @param room available room to offer
     * @return the user receiving the offer, or null if nobody fits
     */
    public User offerNextRoom(Room room) {
        if (room == null) {
            throw new IllegalArgumentException("Room cannot be null.");
        }
        if (!containsRoom(room)) {
            throw new IllegalArgumentException("Room is not managed by this controller.");
        }
        if (!room.isAvailable()) {
            return null;
        }

        WaitingList queue = getQueueForRoom(room);
        List<User> waitingUsers = drainQueue(queue);
        User selectedUser = null;

        for (User waitingUser : waitingUsers) {
            Integer groupSize = requestedGroupSizes.get(waitingUser);
            if (selectedUser == null && groupSize != null
                && groupSize.intValue() <= room.getCapacity()) {
                selectedUser = waitingUser;
            }
        }

        // Rebuild the line in its original order, excluding only the selected user.
        for (User waitingUser : waitingUsers) {
            if (!waitingUser.equals(selectedUser)) {
                queue.enqueue(waitingUser);
            }
        }

        if (selectedUser != null) {
            requestedGroupSizes.remove(selectedUser);
            room.offerTo(selectedUser);
            view.displayMessage("Room " + room.getRoomNumber() + " is being offered to "
                + selectedUser.getName() + ".");
            refreshView();
        }
        return selectedUser;
    }

    /**
     * Accepts the current user's pending offer for the default 30-minute session.
     *
     * @param user user accepting the offer
     * @return true if an offer was accepted
     */
    public boolean acceptOffer(User user) {
        return acceptOffer(user, DEFAULT_SESSION_MINUTES);
    }

    /**
     * Accepts an offer for either 30 or 60 minutes.
     *
     * @param user user accepting the offer
     * @param durationMinutes session length, either 30 or 60
     * @return true if an offer was accepted
     */
    public boolean acceptOffer(User user, int durationMinutes) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null.");
        }
        if (durationMinutes != 30 && durationMinutes != 60) {
            throw new IllegalArgumentException("Session length must be 30 or 60 minutes.");
        }
        Room offeredRoom = findOfferedRoom(user);
        if (offeredRoom == null) {
            view.displayMessage(user.getName() + " does not have a pending room offer.");
            return false;
        }

        offeredRoom.acceptOffer();
        sessionStartTimes.put(offeredRoom, LocalDateTime.now());
        sessionDurations.put(offeredRoom, durationMinutes);
        view.displayMessage(user.getName() + " accepted room " + offeredRoom.getRoomNumber()
            + " for " + durationMinutes + " minutes.");
        refreshView();
        return true;
    }

    /**
     * Declines an offer. The next eligible student is then offered the room.
     *
     * @param user user declining the offer
     * @return true if an offer was declined
     */
    public boolean declineOffer(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null.");
        }
        Room offeredRoom = findOfferedRoom(user);
        if (offeredRoom == null) {
            view.displayMessage(user.getName() + " does not have a pending room offer.");
            return false;
        }

        offeredRoom.clearOffer();
        view.displayMessage(user.getName() + " declined the offer for room "
            + offeredRoom.getRoomNumber() + ".");
        offerNextRoom(offeredRoom);
        refreshView();
        return true;
    }

    /**
     * Removes a student from a waiting line.
     *
     * @param user student leaving the line
     * @param category "Study" or "Meeting"
     * @return true if the student was removed
     */
    public boolean leaveQueue(User user, String category) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null.");
        }
        WaitingList queue = getQueue(normalizeCategory(category));
        boolean removed = queue.remove(user);
        if (removed) {
            requestedGroupSizes.remove(user);
            view.displayMessage(user.getName() + " left the " + queue.getCategory()
                + " room line.");
            offerAvailableRooms();
            refreshView();
        }
        return removed;
    }

    /**
     * Ends a student's room session and offers the room to the next eligible group.
     * This method is called when the session's selected duration has elapsed.
     *
     * @param room room whose session is ending
     * @return user who was using the room
     */
    public User endRoomSession(Room room) {
        if (room == null || !containsRoom(room)) {
            throw new IllegalArgumentException("A managed room is required.");
        }
        User previousUser = room.release();
        sessionStartTimes.remove(room);
        sessionDurations.remove(room);
        view.displayMessage(previousUser.getName() + "'s session in room "
            + room.getRoomNumber() + " has ended.");
        offerNextRoom(room);
        refreshView();
        return previousUser;
    }

    /**
     * Returns the expected end time for an active session, or null if the room
     * does not currently have a tracked session.
     *
     * @param room room to check
     * @return expected session end time, or null
     */
    public LocalDateTime getSessionEndTime(Room room) {
        if (room == null || !containsRoom(room)) {
            throw new IllegalArgumentException("A managed room is required.");
        }
        LocalDateTime start = sessionStartTimes.get(room);
        Integer duration = sessionDurations.get(room);
        if (start == null || duration == null) {
            return null;
        }
        return start.plusMinutes(duration.intValue());
    }

    /**
     * Ends every session whose selected duration has elapsed. A UI or driver
     * can call this periodically to enforce session limits.
     */
    public void endExpiredSessions() {
        LocalDateTime now = LocalDateTime.now();
        List<Room> expiredRooms = new ArrayList<Room>();
        for (Room room : rooms) {
            LocalDateTime end = getSessionEndTime(room);
            if (end != null && !now.isBefore(end)) {
                expiredRooms.add(room);
            }
        }
        for (Room room : expiredRooms) {
            endRoomSession(room);
        }
    }

    /**
     * Returns a student's 1-based position in a category's line, or -1 if absent.
     *
     * @param user student being checked
     * @param category queue category
     * @return queue position, or -1
     */
    public int getQueuePosition(User user, String category) {
        if (user == null) {
            return -1;
        }
        return getQueue(normalizeCategory(category)).getPosition(user);
    }

    /**
     * Returns available rooms that fit the requested group size.
     *
     * @param category room category
     * @param groupSize number of people
     * @return matching available rooms
     */
    public List<Room> searchRooms(String category, int groupSize) {
        String normalizedCategory = normalizeCategory(category);
        validateGroupSize(normalizedCategory, groupSize);
        List<Room> matches = new ArrayList<Room>();
        for (Room room : rooms) {
            if (categoryMatches(room.getCategory(), normalizedCategory)
                && room.getCapacity() >= groupSize && room.isAvailable()) {
                matches.add(room);
            }
        }
        return Collections.unmodifiableList(matches);
    }

    /** Displays the current rooms and queue sizes. */
    public void refreshView() {
        view.displayRooms(Collections.unmodifiableList(rooms));
        view.displayQueueStatus(studyRoomQueue.size(), meetingRoomQueue.size());
    }

    /** @return an unmodifiable view of the managed rooms */
    public List<Room> getRooms() {
        return Collections.unmodifiableList(rooms);
    }

    /** @return the number of students in the study room line */
    public int getStudyQueueSize() {
        return studyRoomQueue.size();
    }

    /** @return the number of students in the meeting room line */
    public int getMeetingQueueSize() {
        return meetingRoomQueue.size();
    }

    /**
     * Offers every currently available room to the next eligible student.
     */
    public void offerAvailableRooms() {
        for (Room room : rooms) {
            if (room.isAvailable()) {
                offerNextRoom(room);
            }
        }
    }

    private List<User> drainQueue(WaitingList queue) {
        List<User> users = new ArrayList<User>();
        while (!queue.isEmpty()) {
            users.add(queue.dequeue());
        }
        return users;
    }

    private WaitingList getQueueForRoom(Room room) {
        return getQueue(normalizeCategory(room.getCategory()));
    }

    private WaitingList getQueue(String category) {
        if (STUDY.equals(category)) {
            return studyRoomQueue;
        }
        return meetingRoomQueue;
    }

    private String normalizeCategory(String category) {
        if (category == null) {
            throw new IllegalArgumentException("Category cannot be null.");
        }
        String lower = category.trim().toLowerCase();
        if (lower.equals("study") || lower.equals("study room")
            || lower.equals("studyroom")) {
            return STUDY;
        }
        if (lower.equals("meeting") || lower.equals("meeting room")
            || lower.equals("meetingroom")) {
            return MEETING;
        }
        throw new IllegalArgumentException("Category must be Study or Meeting.");
    }

    private boolean categoryMatches(String roomCategory, String requestedCategory) {
        try {
            return normalizeCategory(roomCategory).equals(requestedCategory);
        }
        catch (IllegalArgumentException exception) {
            return false;
        }
    }

    private void validateGroupSize(String category, int groupSize) {
        if (groupSize < MIN_GROUP_SIZE || groupSize > MAX_GROUP_SIZE) {
            throw new IllegalArgumentException("Group size must be between 2 and 8.");
        }
        if (STUDY.equals(category) && groupSize > 4) {
            throw new IllegalArgumentException("Study rooms are limited to groups of 4.");
        }
    }

    private boolean isUserInSystem(User user) {
        if (studyRoomQueue.contains(user) || meetingRoomQueue.contains(user)
            || findOfferedRoom(user) != null) {
            return true;
        }
        for (Room room : rooms) {
            if (user.equals(room.getReservedBy())) {
                return true;
            }
        }
        return false;
    }

    private Room findOfferedRoom(User user) {
        for (Room room : rooms) {
            if (user.equals(room.getOfferedTo())) {
                return room;
            }
        }
        return null;
    }

    private boolean containsRoom(Room target) {
        for (Room room : rooms) {
            if (room == target) {
                return true;
            }
        }
        return false;
    }

    /**
     * Small runnable demonstration of the main prototype workflow.
     *
     * @param args command-line arguments (unused)
     */
    public static void main(String[] args) {
        List<Room> sampleRooms = new ArrayList<Room>();
        LocalTime start = LocalTime.of(0, 0);
        LocalTime end = LocalTime.of(0, 30);
        java.time.LocalDate today = java.time.LocalDate.now();

        // Sample rooms let the prototype run immediately. Replace these with
        // the library's actual room data when that information is available.
        sampleRooms.add(new Room("S101", "Study", 4, "Main", today, start, end));
        sampleRooms.add(new Room("S102", "Study", 2, "Main", today, start, end));
        sampleRooms.add(new Room("M201", "Meeting", 8, "Main", today, start, end));

        ViewMain mainView = new ViewMain();
        ControllerMain controller = new ControllerMain(sampleRooms, mainView);
        mainView.runApplication(controller);
    }
}
