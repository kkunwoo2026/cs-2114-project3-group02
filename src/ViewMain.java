package src;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Console interface for the HokieHold prototype.
 * Collects user input and displays application information.
 * ControllerMain handles application rules and operations.
 *
 * @author Zachary
 * @version 2026.11.16
 */
public class ViewMain {

    private final Scanner input;
    private final Map<String, User> knownUsers;

    /**
     * Creates the console view.
     */
    public ViewMain() {
        input = new Scanner(System.in);
        knownUsers = new HashMap<String, User>();
    }

    /**
     * Starts the application menu.
     *
     * @param controller controller that performs application operations
     */
    public void runApplication(ControllerMain controller) {

        if (controller == null) {
            throw new IllegalArgumentException(
                    "Controller cannot be null.");
        }

        boolean running = true;

        displayMessage("Welcome to HokieHold!");

        while (running) {

            controller.endExpiredSessions();
            displayMenu();

            String choice = readLine("Choose an option: ").trim();

            try {
                switch (choice) {

                    case "1":
                        joinWaitingLine(controller);
                        break;

                    case "2":
                        acceptRoomOffer(controller);
                        break;

                    case "3":
                        declineRoomOffer(controller);
                        break;

                    case "4":
                        leaveWaitingLine(controller);
                        break;

                    case "5":
                        showQueuePosition(controller);
                        break;

                    case "6":
                        controller.refreshView();
                        break;

                    case "7":
                        endRoomSession(controller);
                        break;

                    case "8":
                        showMatchingRooms(controller);
                        break;

                    case "0":
                        try {
                            controller.saveWaitingLists();
                            displayMessage(
                                    "Waiting lists saved successfully.");
                        }
                        catch (IOException exception) {
                            displayMessage(
                                    "Error saving waiting lists: "
                                    + exception.getMessage());
                        }

                        running = false;
                        break;

                    default:
                        displayMessage(
                                "Please enter a number from 0 to 8.");
                        break;
                }
            }
            catch (IllegalArgumentException
                    | IllegalStateException exception) {

                displayMessage(
                        "Unable to complete that action: "
                        + exception.getMessage());
            }
        }

        displayMessage("Thank you for using HokieHold!");
    }

    /**
     * Displays the menu.
     */
    private void displayMenu() {

        System.out.println("\n========== HOKIEHOLD ==========");
        System.out.println("1. Join a waiting line");
        System.out.println("2. Accept a room offer");
        System.out.println("3. Decline a room offer");
        System.out.println("4. Leave a waiting line");
        System.out.println("5. Check waiting-line position");
        System.out.println("6. View rooms and waiting-line counts");
        System.out.println("7. End a room session");
        System.out.println("8. Find available rooms for a group");
        System.out.println("0. Exit");
        System.out.println("================================");
    }

    /**
     * Allows a student to join a waiting line.
     *
     * @param controller application controller
     */
    private void joinWaitingLine(ControllerMain controller) {

        String pid = readNonBlank("Enter HokiePID: ");

        User user = knownUsers.get(pid);

        if (user == null) {
            String name = readNonBlank("Enter student name: ");

            user = new User(pid, name);
            knownUsers.put(pid, user);
        }
        else {
            displayMessage(
                    "Using saved student profile for "
                    + user.getName() + ".");
        }

        String category = readCategory();

        int groupSize = readInteger(
                "How many people are in your group? ");

        controller.joinQueue(user, category, groupSize);

        int position = controller.getQueuePosition(user, category);

        if (position > 0) {
            displayQueuePosition(category, position);
        }
    }

    /**
     * Accepts a room offer.
     *
     * @param controller application controller
     */
    private void acceptRoomOffer(ControllerMain controller) {

        User user = findKnownUser();

        if (user == null) {
            return;
        }

        int duration = readInteger(
                "Choose session length (30 or 60 minutes): ");

        controller.acceptOffer(user, duration);
    }

    /**
     * Declines a room offer.
     *
     * @param controller application controller
     */
    private void declineRoomOffer(ControllerMain controller) {

        User user = findKnownUser();

        if (user != null) {
            controller.declineOffer(user);
        }
    }

    /**
     * Removes a student from a waiting line.
     *
     * @param controller application controller
     */
    private void leaveWaitingLine(ControllerMain controller) {

        User user = findKnownUser();

        if (user != null) {

            String category = readCategory();

            boolean removed = controller.leaveQueue(user, category);

            if (!removed) {
                displayMessage(
                        "That student is not in the selected waiting line.");
            }
        }
    }

    /**
     * Displays a student's queue position.
     *
     * @param controller application controller
     */
    private void showQueuePosition(ControllerMain controller) {

        User user = findKnownUser();

        if (user != null) {

            String category = readCategory();

            int position = controller.getQueuePosition(
                    user, category);

            displayQueuePosition(category, position);
        }
    }

    /**
     * Ends an active room session.
     *
     * @param controller application controller
     */
    private void endRoomSession(ControllerMain controller) {

        displayRooms(controller.getRooms());

        String roomNumber = readNonBlank(
                "Enter the room number whose session is ending: ");

        Room selected = findRoom(
                controller.getRooms(), roomNumber);

        if (selected == null) {
            displayMessage(
                    "No room with that room number was found.");
        }
        else if (selected.getReservedBy() == null) {
            displayMessage(
                    "That room does not currently have an active session.");
        }
        else {
            controller.endRoomSession(selected);
        }
    }

    /**
     * Finds available rooms matching a group.
     *
     * @param controller application controller
     */
    private void showMatchingRooms(ControllerMain controller) {

        String category = readCategory();

        int groupSize = readInteger(
                "How many people are in your group? ");

        List<Room> matches = controller.searchRooms(
                category, groupSize);

        if (matches.isEmpty()) {
            displayMessage(
                    "No currently available rooms fit that group.");
        }
        else {
            displayMessage(
                    "Available rooms that fit your group:");

            displayRooms(matches);
        }
    }

    /**
     * Finds a previously entered student.
     *
     * @return student or null if not found
     */
    private User findKnownUser() {

        String pid = readNonBlank(
                "Enter the student's HokiePID: ");

        User user = knownUsers.get(pid);

        if (user == null) {
            displayMessage(
                    "No student with that PID has been entered "
                    + "during this session.");
        }

        return user;
    }

    /**
     * Reads the room category.
     *
     * @return Study or Meeting
     */
    private String readCategory() {

        while (true) {

            String category = readLine(
                    "Room category (Study or Meeting): ").trim();

            if (category.equalsIgnoreCase("study")
                    || category.equalsIgnoreCase("meeting")) {
                return category;
            }

            displayMessage("Please enter Study or Meeting.");
        }
    }

    /**
     * Reads an integer from the console.
     *
     * @param prompt message to display
     * @return entered integer
     */
    private int readInteger(String prompt) {

        while (true) {

            String value = readLine(prompt).trim();

            try {
                return Integer.parseInt(value);
            }
            catch (NumberFormatException exception) {
                displayMessage("Please enter a whole number.");
            }
        }
    }

    /**
     * Reads nonblank text.
     *
     * @param prompt message to display
     * @return entered text
     */
    private String readNonBlank(String prompt) {

        while (true) {

            String value = readLine(prompt).trim();

            if (!value.isEmpty()) {
                return value;
            }

            displayMessage("This field cannot be blank.");
        }
    }

    /**
     * Reads one console line.
     *
     * @param prompt message to display
     * @return entered line
     */
    private String readLine(String prompt) {

        System.out.print(prompt);
        return input.nextLine();
    }

    /**
     * Finds a room by its number.
     *
     * @param rooms configured rooms
     * @param roomNumber room number to find
     * @return matching room or null
     */
    private Room findRoom(List<Room> rooms, String roomNumber) {

        for (Room room : rooms) {

            if (room.getRoomNumber().equalsIgnoreCase(roomNumber)) {
                return room;
            }
        }

        return null;
    }

    /**
     * Displays room information.
     *
     * @param rooms rooms to display
     */
    public void displayRooms(List<Room> rooms) {

        System.out.println("\n--- HokieHold Rooms ---");

        if (rooms.isEmpty()) {
            System.out.println("No rooms are currently configured.");
        }

        for (Room room : rooms) {

            String status;

            if (room.getReservedBy() != null) {
                status = "In use by "
                        + room.getReservedBy().getName();
            }
            else if (room.getOfferedTo() != null) {
                status = "Offered to "
                        + room.getOfferedTo().getName();
            }
            else {
                status = "Available";
            }

            System.out.println(
                    room.getRoomNumber() + " | "
                    + room.getCategory() + " | Capacity: "
                    + room.getCapacity() + " | " + status);
        }
    }

    /**
     * Displays waiting-line counts.
     *
     * @param studyRoomCount number in Study line
     * @param meetingRoomCount number in Meeting line
     */
    public void displayQueueStatus(
            int studyRoomCount, int meetingRoomCount) {

        System.out.println(
                "Study room line: " + studyRoomCount);

        System.out.println(
                "Meeting room line: " + meetingRoomCount);
    }

    /**
     * Displays a message.
     *
     * @param message message to display
     */
    public void displayMessage(String message) {
        System.out.println(message);
    }

    /**
     * Displays a student's queue position.
     *
     * @param category room category
     * @param position queue position
     */
    public void displayQueuePosition(
            String category, int position) {

        if (position < 1) {
            System.out.println(
                    "You are not currently in the "
                    + category + " line.");
        }
        else {
            System.out.println(
                    "Your position in the " + category
                    + " line is " + position + ".");
        }
    }

    /**
     * Remembers a student restored from saved data.
     *
     * @param user student to remember
     */
    public void rememberUser(User user) {

        if (user == null) {
            throw new IllegalArgumentException(
                    "User cannot be null.");
        }

        knownUsers.put(user.getHokiePID(), user);
    }
}