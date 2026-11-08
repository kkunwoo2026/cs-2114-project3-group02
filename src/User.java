/**
 *  A User class that tracks the user's name and HokiePID.
 * 
 *  @author Kunwoo Kim (kkunwoo)
 *  @version 2026. 11. 2.
 */
public class User {
    //~ Fields ................................................................

    private String name;
    private String hokiePID;
    
    //~ Constructors ..........................................................
    /**
     * Creates a new user.
     * @param hokiePID the user's HokiePID
     * @param name the user's name
     * @throws IllegalArgumentException if PID or name is null or blank
     */
    public User(String hokiePID, String name) {
        if (hokiePID == null || hokiePID.isBlank()) {
            throw new IllegalArgumentException("Hokie PID cannot be null or blank.");
        }

        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name cannot be null or blank.");
        }

        this.hokiePID = hokiePID;
        this.name = name;
    }
    //~Public  Methods ........................................................

    /**
     * getter's method for HokiePID.
     * @return the user's HokiePID
     */
    public String getHokiePID() {
        return hokiePID;
    }

    /**
     * Returns the user's name.
     *
     * @return the user's name
     */
    public String getName() {
        return name;
    }

    /**
     * Compares this user with another object based on Hokie PID.
     * @param other the object to compare
     * @return true if both objects are users with the same PID
     */
    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }

        if (!(other instanceof User)) {
            return false;
        }

        User otherUser = (User) other;
        return hokiePID.equals(otherUser.hokiePID);
    }

    /**
     * Returns a hash code based on the user's HokiePID.
     * @return hash code of the HokiePID
     */
    @Override
    public int hashCode() {
        return hokiePID.hashCode();
    }
}


