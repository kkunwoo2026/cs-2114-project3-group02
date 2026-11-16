package src;

/**
 * A test class for user
 *  @author Kunwoo Kim (kkunwoo)
 *  @version 2026. 11. 2.
 */
public class UserTest extends student.TestCase {
    //~ Fields ................................................................
    private User user1;
    //~ Constructors ..........................................................
    protected void setUp() {
        user1 = new User("PID1", "user1");
    }
    //~Public  Methods ........................................................
    /**
     * A test class for getHokiePID().
     */
    public void testGetHokiePID() {
        assertEquals(user1.getHokiePID(), "PID1");
        try {
            User user2 = new User("", "user2");
            user2.getHokiePID();
        }
        catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), "Hokie PID cannot be null or blank.");
        }
    }
    /**
     * A test class for getName().
     */
    public void testGetName() {
        assertEquals(user1.getName(), "user1");
        try {
            User user3 = new User("PID3", "");
            user3.getName();
        }
        catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), "Name cannot be null or blank.");
        }
    }
    /**
     * A test class for equals().
     */
    public void testEquals() {
        User u1 = new User("PID1", "user1");
        User u2 = new User("PID1", "user2");
        User u3 = new User("PID2", "user3");
        assertTrue(u1.equals(u1));
        assertTrue(u1.equals(u2));
        assertFalse(u1.equals(u3));
        assertFalse(u1.equals(null));
        assertFalse(u1.equals("PID1"));
    }
    
    /**
     * A test class for hashCode().
     */
    public void testHashCode() {
        User us1 = new User("PID1", "user1");
        User us2 = new User("PID1", "user2");
        User us3 = new User("PID2", "user3");
        assertEquals(us1.hashCode(), us2.hashCode());
        assertFalse(us1.hashCode() == us3.hashCode());
    }
}
