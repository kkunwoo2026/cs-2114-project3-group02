package src;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Tests the methods for the Room class.
 *
 * @author Matthew Abramson
 * @version 11/16/2026
 */
public class RoomTest extends student.TestCase 
{

    private Room room;
    private User emma;
    private User matthew;
    private LocalDate date;
    private LocalTime start;
    private LocalTime end;

    /**
     * Creates a new test room before each of the tests
     */
    public void setUp() 
    {
        date = LocalDate.of(2026, 11, 16);
        start = LocalTime.of(10, 0);
        end = LocalTime.of(11, 0);
        room = new Room("101", "Study", 4, "North", date, start, end);
        emma = new User("pid1", "Emma");
        matthew = new User("pid2", "Matthew");
    }

    /**
     * Test the getter methods for the room class
     */
    public void testGetters() 
    {
        assertEquals("101", room.getRoomNumber());
        assertEquals("Study", room.getCategory());
        assertEquals(4, room.getCapacity());
        assertEquals("North", room.getZone());
        assertEquals(date, room.getDate());
        assertEquals(start, room.getStartTime());
        assertEquals(end, room.getEndTime());
        assertNull(room.getReservedBy());
        assertNull(room.getOfferedTo());
        assertTrue(room.isAvailable());
    }

    /**
     * Test the capacity of the room, making sure its 2-8
     */
    public void testCapacityBoundsValid() 
    {
        Room small = new Room("101", "Study", 2, "North", date, start, end);
        Room big = new Room("101", "Study", 8, "North", date, start, end);
        assertEquals(2, small.getCapacity());
        assertEquals(8, big.getCapacity());
    }

    /**
     * Tests when the capacity is too small
     */
    public void testCapacityTooSmall() 
    {
        Exception thrown = null;
        try 
        {
            new Room("101", "Study", 1, "North", date, start, end);
        }
        catch (IllegalArgumentException e) 
        {
            thrown = e;
        }
        assertNotNull(thrown);
    }

    /**
     * Test when the capacity is to big 
     */
    public void testCapacityTooBig() 
    {
        Exception thrown = null;
        try 
        {
            new Room("101", "Study", 9, "North", date, start, end);
        }
        catch (IllegalArgumentException e) 
        {
            thrown = e;
        }
        assertNotNull(thrown);
    }

    /**
     * Test when the room number is null
     */
    public void testNullRoomNumber() {
        Exception thrown = null;
        try {
            new Room(null, "Study", 4, "North", date, start, end);
        }
        catch (IllegalArgumentException e) {
            thrown = e;
        }
        assertNotNull(thrown);
    }

    /**
     * Test when the category is null
     */
    public void testNullCategory() {
        Exception thrown = null;
        try {
            new Room("101", null, 4, "North", date, start, end);
        }
        catch (IllegalArgumentException e) {
            thrown = e;
        }
        assertNotNull(thrown);
    }

    /**
     * Test when the zone is null
     */
    public void testNullZone() {
        Exception thrown = null;
        try {
            new Room("101", "Study", 4, null, date, start, end);
        }
        catch (IllegalArgumentException e) {
            thrown = e;
        }
        assertNotNull(thrown);
    }

    /**
     * test when the room number is empty
     */
    public void testEmptyRoomNumber() {
        Exception thrown = null;
        try {
            new Room("", "Study", 4, "North", date, start, end);
        }
        catch (IllegalArgumentException e) {
            thrown = e;
        }
        assertNotNull(thrown);
    }

    /**
     * test when the category is blank 
     */
    public void testBlankCategory() {
        Exception thrown = null;
        try {
            new Room("101", "   ", 4, "North", date, start, end);
        }
        catch (IllegalArgumentException e) {
            thrown = e;
        }
        assertNotNull(thrown);
    }

    /**
     * Test when the zone is blank 
     */
    public void testBlankZone() {
        Exception thrown = null;
        try {
            new Room("101", "Study", 4, " ", date, start, end);
        }
        catch (IllegalArgumentException e) {
            thrown = e;
        }
        assertNotNull(thrown);
    }

    /**
     * test when the date is null 
     */
    public void testNullDate() {
        Exception thrown = null;
        try {
            new Room("101", "Study", 4, "North", null, start, end);
        }
        catch (IllegalArgumentException e) {
            thrown = e;
        }
        assertNotNull(thrown);
    }

    /**
     * Test when the start time is null
     */
    public void testNullStart() {
        Exception thrown = null;
        try {
            new Room("101", "Study", 4, "North", date, null, end);
        }
        catch (IllegalArgumentException e) {
            thrown = e;
        }
        assertNotNull(thrown);
    }

    /**
     * Test when the end time is null
     */
    public void testNullEnd() {
        Exception thrown = null;
        try {
            new Room("101", "Study", 4, "North", date, start, null);
        }
        catch (IllegalArgumentException e) {
            thrown = e;
        }
        assertNotNull(thrown);
    }

    /**
     * test when the end time is before the start time 
     */
    public void testEndBeforeStart() {
        Exception thrown = null;
        try {
            new Room("101", "Study", 4, "North", date, end, start);
        }
        catch (IllegalArgumentException e) {
            thrown = e;
        }
        assertNotNull(thrown);
    }

    /**
     * Test when the end time and the start time are the same
     */
    public void testEndEqualsStart() {
        Exception thrown = null;
        try {
            new Room("101", "Study", 4, "North", date, start, start);
        }
        catch (IllegalArgumentException e) {
            thrown = e;
        }
        assertNotNull(thrown);
    }
    

    /**
     * test the reserve method 
     */
    public void testReserve() {
        room.reserve(emma);
        assertSame(emma, room.getReservedBy());
        assertFalse(room.isAvailable());
    }

    /**
     * Test when you are trying to reserve something that is null
     */
    public void testReserveNull() {
        Exception thrown = null;
        try {
            room.reserve(null);
        }
        catch (IllegalArgumentException e) {
            thrown = e;
        }
        assertNotNull(thrown);
        assertTrue(room.isAvailable());
    }

    /**
     * Testing when you are trying to reserve a room that is already taken
     */
    public void testReserveTwice() {
        room.reserve(emma);
        Exception thrown = null;
        try {
            room.reserve(matthew);
        }
        catch (IllegalStateException e) {
            thrown = e;
        }
        assertNotNull(thrown);
        assertSame(emma, room.getReservedBy());
    }

    /**
     * Test when trying to reserve a room that has given an offer
     */
    public void testReserveWhileOffered() {
        room.offerTo(emma);
        Exception thrown = null;
        try {
            room.reserve(matthew);
        }
        catch (IllegalStateException e) {
            thrown = e;
        }
        assertNotNull(thrown);
        assertNull(room.getReservedBy());
    }

    /**
     * Testing the release method
     */
    public void testRelease() {
        room.reserve(emma);
        assertSame(emma, room.release());
        assertNull(room.getReservedBy());
        assertTrue(room.isAvailable());
    }

    /**
     * Test when you are releasing a room that isn't reserved 
     */
    public void testReleaseNotReserved() {
        Exception thrown = null;
        try {
            room.release();
        }
        catch (IllegalStateException e) {
            thrown = e;
        }
        assertNotNull(thrown);
    }

    /**
     * test when you are releasing a room with only an offer
     */
    public void testReleaseWhileOffered() {
        room.offerTo(emma);
        Exception thrown = null;
        try {
            room.release();
        }
        catch (IllegalStateException e) {
            thrown = e;
        }
        assertNotNull(thrown);
        assertSame(emma, room.getOfferedTo());
    }

    /**
     * test the offer method 
     */
    public void testOfferTo() {
        room.offerTo(emma);
        assertSame(emma, room.getOfferedTo());
        assertNull(room.getReservedBy());
        assertFalse(room.isAvailable());
    }

    /**
     * test the offer method 
     */
    public void testOfferToNull() {
        Exception thrown = null;
        try {
            room.offerTo(null);
        }
        catch (IllegalArgumentException e) {
            thrown = e;
        }
        assertNotNull(thrown);
        assertTrue(room.isAvailable());
    }

    /**
     * test when you offer a room that is reserved
     */
    public void testOfferWhileReserved() {
        room.reserve(emma);
        Exception thrown = null;
        try {
            room.offerTo(matthew);
        }
        catch (IllegalStateException e) {
            thrown = e;
        }
        assertNotNull(thrown);
        assertNull(room.getOfferedTo());
    }

    /**
     * test when you offer a room that already has an offer
     */
    public void testOfferTwice() {
        room.offerTo(emma);
        Exception thrown = null;
        try {
            room.offerTo(matthew);
        }
        catch (IllegalStateException e) {
            thrown = e;
        }
        assertNotNull(thrown);
        assertSame(emma, room.getOfferedTo());
    }

    /**
     * test when you accept an offer
     */
    public void testAcceptOffer() {
        room.offerTo(emma);
        room.acceptOffer();
        assertSame(emma, room.getReservedBy());
        assertNull(room.getOfferedTo());
        assertFalse(room.isAvailable());
    }

   /**
    * test when you try to accept a room with no offer
    */
    public void testAcceptNoOffer() {
        Exception thrown = null;
        try {
            room.acceptOffer();
        }
        catch (IllegalStateException e) {
            thrown = e;
        }
        assertNotNull(thrown);
    }

    /**
     * testing the clear offer method 
     */
    public void testClearOffer() {
        room.offerTo(emma);
        assertSame(emma, room.clearOffer());
        assertNull(room.getOfferedTo());
        assertTrue(room.isAvailable());
    }

    /**
     * testing the clear offer when there is no offer 
     */
    public void testClearNoOffer() {
        Exception thrown = null;
        try {
            room.clearOffer();
        }
        catch (IllegalStateException e) {
            thrown = e;
        }
        assertNotNull(thrown);
    }

   /**
    * testing the reserve method after the room gets cleared 
    */
    public void testReserveAfterClear() {
        room.offerTo(emma);
        room.clearOffer();
        room.reserve(matthew);
        assertSame(matthew, room.getReservedBy());
    }

    /**
     * further testing the offer method after released
     */
    public void testOfferAfterRelease() {
        room.reserve(emma);
        room.release();
        room.offerTo(matthew);
        assertSame(matthew, room.getOfferedTo());
    }
    
    
}