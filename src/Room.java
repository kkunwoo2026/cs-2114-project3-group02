import java.time.LocalDate;
import java.time.LocalTime;

/**
 * This creates a bookable library room. The room has three possible states 
 * that it can be in: free, offered to a user, or reserved by the user
 * 
 * @author Matthew Abramson
 * @version 11/08/2026
 */

public class Room {

    private static final int MIN_CAPACITY = 2;
    private static final int MAX_CAPACITY = 8;

    private final String roomNumber;
    private final String category;
    private final int capacity;
    private final String zone;
    private final LocalDate date;
    private final LocalTime startTime;
    private final LocalTime endTime;

    private User reservedBy;
    private User offeredTo;

    /**
     * Creates the room for the class
     *
     * @param roomNumber the room's number
     * @param category the type of room
     * @param capacity how many people the room can fit, 2-8
     * @param zone the zone of the library the room is in
     * @param date the day the room can be booked
     * @param startTime when the time slot starts
     * @param endTime when the time slot ends
     * @throws IllegalArgumentException if any text is null or blank, or if 
     * any date or time is null, or if the capacity is outside 2-8, 
     * or endTime is not after startTime
     */
    
    public Room(String roomNumber, String category, int capacity,
        String zone, LocalDate date, LocalTime startTime, LocalTime endTime) 
    {
        if (isBlank(roomNumber) || isBlank(category) || isBlank(zone)) 
        {
            throw new IllegalArgumentException("Text cannot be null or blank");
        }
        if (capacity < MIN_CAPACITY || capacity > MAX_CAPACITY) 
        {
            throw new IllegalArgumentException("Capacity is 2 to 8 people");
        }
        if (date == null || startTime == null || endTime == null) 
        {
            throw new IllegalArgumentException("Date and times are required");
        }
        if (!endTime.isAfter(startTime)) 
        {
            throw new IllegalArgumentException("End time must be after start");
        }
        
        this.roomNumber = roomNumber;
        this.category = category;
        this.capacity = capacity;
        this.zone = zone;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    /**
     * Checks whether the room is neither reserved nor offered to anyone.
     *
     * @return true if the room is free
     */
    public boolean isAvailable() 
    {
        return reservedBy == null && offeredTo == null;
    }

    /**
     * Assigns the room to a user.
     *
     * @param user the user who gets the room
     * @throws IllegalArgumentException if user is null
     * @throws IllegalStateException if the room isn't available
     */
    public void reserve(User user) 
    {
        if (user == null)
        {
            throw new IllegalArgumentException("User cannot be null");
        }
        if (!isAvailable()) 
        {
            throw new IllegalStateException("Room is not available");
        }
        
        reservedBy = user;
    }

    /**
     * Makes the room empty 
     *
     * @return the user who was holding the room
     * @throws IllegalStateException if the room is not reserved
     */
    public User release() 
    {
        if (reservedBy == null) 
        {
            throw new IllegalStateException("Room is not reserved");
        }
        
        User previous = reservedBy;
        reservedBy = null;
        return previous;
    }

    /**
     * Offers up the room to the user 
     *
     * @param user is the user receiving the offer
     * @throws IllegalArgumentException if user is null throws exception
     * @throws IllegalStateException if the room is reserved or already offered
     */
    public void offerTo(User user) 
    {
        if (user == null) 
        {
            throw new IllegalArgumentException("User cannot be null");
        }
        if (!isAvailable())
        {
            throw new IllegalStateException("Room is not available");
        }
        
        offeredTo = user;
    }

    /**
     * Turns the offered into an accepted reservation
     *
     * @throws IllegalStateException if there is no pending offer
     */
    public void acceptOffer() 
    {
        if (offeredTo == null) 
        {
            throw new IllegalStateException("There is no pending offer");
        }
        
        reservedBy = offeredTo;
        offeredTo = null;
    }

    /**
     * This method will cancel the offer
     *
     * @return the user who had been offered the room
     * @throws IllegalStateException if there is no pending offer
     */
    public User clearOffer() 
    {
        if (offeredTo == null) 
        {
            throw new IllegalStateException("No pending offer");
        }
        
        User previous = offeredTo;
        offeredTo = null;
        return previous;
    }

    /**
     * Returns the user holding the room.
     *
     * @return the user who is holding the room
     */
    public User getReservedBy() 
    {
        return reservedBy;
    }

    /**
     * Gets the user who the room is offered to 
     *
     * @return the user who has the pending offer
     */
    public User getOfferedTo()
    {
        return offeredTo;
    }

    /**
     * Gets the room number
     *
     * @return the room number
     */
    public String getRoomNumber() 
    {
        return roomNumber;
    }

    /**
     * Gets the room category
     *
     * @return the category
     */
    public String getCategory() 
    {
        return category;
    }

    /**
     * Gets the zone that the room is in 
     *
     * @return the zone
     */
    public String getZone() 
    {
        return zone;
    }

    /**
     * Gets how many people the room holds
     *
     * @return the capacity that the room has 
     */
    public int getCapacity() {
        return capacity;
    }

    /**
     * Gets the date for the time slot
     *
     * @return the date for the time 
     */
    public LocalDate getDate() 
    {
        return date;
    }

    /**
     * Gets when the time slot starts
     *
     * @return the start time for the reservation
     */
    public LocalTime getStartTime() 
    {
        return startTime;
    }

    /**
     * Gets when the time slot ends
     *
     * @return the end time for the reservation
     */
    public LocalTime getEndTime() 
    {
        return endTime;
    }

    /**
     * Checks for things if they are null or blank
     *
     * @param text that is being checked
     * @return true if text is null
     */
    private static boolean isBlank(String text) 
    {
        return text == null || text.trim().isEmpty();
    }
    
}