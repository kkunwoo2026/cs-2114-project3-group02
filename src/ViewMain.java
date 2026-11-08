import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Console interface for the HokieHold prototype.
 * This class collects input and displays information. ControllerMain
 * remains responsible for room and waiting-line rules.
 *
 * @author Zachary
 * @version 2026.11.08
 */
public class ViewMain {
    private final Scanner input;
    // Keeps User objects for this running application so a HokiePID entered
    // for an action can be matched to the same User object used by the model.
    private final Map<String, User> knownUsers;

    /** Creates an interactive console view. */
    public ViewMain() {
        input = new Scanner(System.in);
        knownUsers = new HashMap<String, User>();
    }

    /**
     * Starts the interactive menu and accepts commands until the user exits.
     *
     * @param controller controller that performs all application operations
     */
    public void runApplication(ControllerMain controller) {
        if (controller == null) {
            throw new IllegalArgumentException("Controller cannot be null.");
        }

        boolean running = true;
        displayMessage("Welcome to HokieHold!");
        while (running) {
            // This checks elapsed session times whenever control returns to the menu.
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
                        running = false;
                        displayMessage("Thank you for using HokieHold.");
                        break;
                    default:
                        displayMessage("Please enter a number from 0 to 8.");
                        break;
                }
            }
            catch (IllegalArgumentException | IllegalStateException exception) {
                displayMessage("Unable to complete that action: " + exception.getMessage());
            }
        }
    }

    /** Displays the available application actions. */
    private void displayMenu() {
        System.out.println("\n========== HOKIEHOLD ==========");
        System.out.println("1. Join a waiting line");
        System.out.println("2. Accept a room offer");
        System.out.println("3. Decline a room offer");
        System.out.println("4. Leave a waiting line");
        System.out.println("5. Check waiting-line position");
        System.out.println("6. View rooms and waiting-line counts");
        System.out.println("7. End a room session (prototype/staff action)");
        System.out.println("8. Find available rooms for a group");
        System.out.println("0. Exit");
        System.out.println("================================");
    }

    /** Collects student and group information, then joins the selected line. */
    private void joinWaitingLine(ControllerMain controller) {
        String pid = readNonBlank("Enter HokiePID: ");
        User user = knownUsers.get(pid);
        if (user == null) {
            String name = readNonBlank("Enter student name: ");
            user = new User(pid, name);
            knownUsers.put(pid, user);
        }
        else {
            System.out.println("Using saved student profile for " + user.getName() + ".");
        }

        String category = readCategory();
        int groupSize = readInteger("How many people are in your group? ");
        controller.joinQueue(user, category, groupSize);

        int position = controller.getQueuePosition(user, category);
        if (position > 0) {
            displayQueuePosition(category, position);
        }
    }

    /** Accepts a pending room offer for a known student. */
    private void acceptRoomOffer(ControllerMain controller) {
        User user = findKnownUser();
        if (user == null) {
            return;
        }
        int duration = readInteger("Choose session length (30 or 60 minutes): ");
        controller.acceptOffer(user, duration);
    }

    /** Declines a pending room offer for a known student. */
    private void declineRoomOffer(ControllerMain controller) {
        User user = findKnownUser();
        if (user != null) {
            controller.declineOffer(user);
        }
    }

    /** Removes a known student from the selected waiting line. */
    private void leaveWaitingLine(ControllerMain controller) {
        User user = findKnownUser();
        if (user != null) {
            String category = readCategory();
            boolean removed = controller.leaveQueue(user, category);
            if (!removed) {
                displayMessage("That student is not in the selected waiting line.");
            }
        }
    }

    /** Displays the selected student's current line position. */
    private void showQueuePosition(ControllerMain controller) {
        User user = findKnownUser();
        if (user != null) {
            String category = readCategory();
            displayQueuePosition(category, controller.getQueuePosition(user, category));
        }
    }

    /** Ends a room session by room number. */
    private void endRoomSession(ControllerMain controller) {
        displayRooms(controller.getRooms());
        String roomNumber = readNonBlank("Enter the room number whose session is ending: ");
        Room selected = findRoom(controller.getRooms(), roomNumber);
        if (selected == null) {
            displayMessage("No room with that room number was found.");
        }
        else if (selected.getReservedBy() == null) {
            displayMessage("That room does not currently have an active session.");
        }
        else {
            controller.endRoomSession(selected);
        }
    }

    /** Searches currently available rooms by category and group size. */
    private void showMatchingRooms(ControllerMain controller) {
        String category = readCategory();
        int groupSize = readInteger("How many people are in your group? ");
        List<Room> matches = controller.searchRooms(category, groupSize);
        if (matches.isEmpty()) {
            displayMessage("No currently available rooms fit that group.");
        }
        else {
            displayMessage("Available rooms that fit your group:");
            displayRooms(matches);
        }
    }

    /** Finds a User already entered during this application run. */
    private User findKnownUser() {
        String pid = readNonBlank("Enter the student's HokiePID: ");
        User user = knownUsers.get(pid);
        if (user == null) {
            displayMessage("No student with that PID has been entered during this session.");
        }
        return user;
    }

    /** Prompts for either Study or Meeting. */
    private String readCategory() {
        while (true) {
            String category = readLine("Room category (Study or Meeting): ").trim();
            if (category.equalsIgnoreCase("study")
                || category.equalsIgnoreCase("meeting")) {
                return category;
            }
            displayMessage("Please enter Study or Meeting.");
        }
    }

    /** Reads an integer, repeating the prompt when the input is invalid. */
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

    /** Reads a nonempty line of text. */
    private String readNonBlank(String prompt) {
        while (true) {
            String value = readLine(prompt).trim();
            if (!value.isEmpty()) {
                return value;
            }
            displayMessage("This field cannot be blank.");
        }
    }

    /** Reads one line from the console. */
    private String readLine(String prompt) {
        System.out.print(prompt);
        return input.nextLine();
    }

    /** Finds a room by its room number, ignoring capitalization. */
    private Room findRoom(List<Room> rooms, String roomNumber) {
        for (Room room : rooms) {
            if (room.getRoomNumber().equalsIgnoreCase(roomNumber)) {
                return room;
            }
        }
        return null;
    }

    /** Displays all rooms and their current states. */
    public void displayRooms(List<Room> rooms) {
        System.out.println("\n--- HokieHold Rooms ---");
        if (rooms.isEmpty()) {
            System.out.println("No rooms are currently configured.");
        }
        for (Room room : rooms) {
            String status;
            if (room.getReservedBy() != null) {
                status = "In use by " + room.getReservedBy().getName();
            }
            else if (room.getOfferedTo() != null) {
                status = "Offered to " + room.getOfferedTo().getName();
            }
            else {
                status = "Available";
            }
            System.out.println(room.getRoomNumber() + " | " + room.getCategory()
                + " | Capacity: " + room.getCapacity() + " | " + status);
        }
    }

    /** Displays the number of students in each waiting line. */
    public void displayQueueStatus(int studyRoomCount, int meetingRoomCount) {
        System.out.println("Study room line: " + studyRoomCount);
        System.out.println("Meeting room line: " + meetingRoomCount);
    }

    /** Displays a general application message. */
    public void displayMessage(String message) {
        System.out.println(message);
    }

    /** Displays a student's 1-based queue position. */
    public void displayQueuePosition(String category, int position) {
        if (position < 1) {
            System.out.println("You are not currently in the " + category + " line.");
        }
        else {
            System.out.println("Your position in the " + category + " line is "
                + position + ".");
        }
    }
}
