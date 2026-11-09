package src;

import java.util.NoSuchElementException;

public class WaitingListTest extends student.TestCase {

    private WaitingList list;
    private User alice;
    private User bob;
    private User carol;


    public void setUp() {
        list = new WaitingList("Study Room");
        alice = new User("pid1", "Alice");
        bob = new User("pid2", "Bob");
        carol = new User("pid3", "Carol");
    }

    public void testConstructorValid() {
        assertEquals("Study Room", list.getCategory());
        assertTrue(list.isEmpty());
        assertEquals(0, list.size());
    }

    public void testConstructorNullCategory() {
        Exception thrown = null;
        try {
            new WaitingList(null);
        }
        catch (IllegalArgumentException e) {
            thrown = e;
        }
        assertNotNull(thrown);
    }

    public void testConstructorEmptyCategory() {
        Exception thrown = null;
        try {
            new WaitingList("");
        }
        catch (IllegalArgumentException e) {
            thrown = e;
        }
        assertNotNull(thrown);
    }

    public void testConstructorBlankCategory() {
        Exception thrown = null;
        try {
            new WaitingList("   ");
        }
        catch (IllegalArgumentException e) {
            thrown = e;
        }
        assertNotNull(thrown);
    }

    public void testEnqueueSingle() {
        list.enqueue(alice);
        assertFalse(list.isEmpty());
        assertEquals(1, list.size());
        assertSame(alice, list.peek());
    }

    public void testEnqueueMultipleKeepsOrder() {
        list.enqueue(alice);
        list.enqueue(bob);
        list.enqueue(carol);
        assertEquals(3, list.size());
        assertSame(alice, list.peek());
        assertEquals(1, list.getPosition(alice));
        assertEquals(2, list.getPosition(bob));
        assertEquals(3, list.getPosition(carol));
    }

    public void testEnqueueNull() {
        Exception thrown = null;
        try {
            list.enqueue(null);
        }
        catch (IllegalArgumentException e) {
            thrown = e;
        }
        assertNotNull(thrown);
        assertEquals(0, list.size());
    }

    public void testEnqueueDuplicate() {
        list.enqueue(alice);
        Exception thrown = null;
        try {
            list.enqueue(alice);
        }
        catch (IllegalStateException e) {
            thrown = e;
        }
        assertNotNull(thrown);
        assertEquals(1, list.size());
    }

    public void testDequeueEmpty() {
        Exception thrown = null;
        try {
            list.dequeue();
        }
        catch (NoSuchElementException e) {
            thrown = e;
        }
        assertNotNull(thrown);
    }

    public void testDequeueSingle() {
        list.enqueue(alice);
        assertSame(alice, list.dequeue());
        assertTrue(list.isEmpty());
        assertEquals(0, list.size());
        Exception thrown = null;
        try {
            list.peek();
        }
        catch (NoSuchElementException e) {
            thrown = e;
        }
        assertNotNull(thrown);
    }

    public void testDequeueFifoOrder() {
        list.enqueue(alice);
        list.enqueue(bob);
        list.enqueue(carol);
        assertSame(alice, list.dequeue());
        assertSame(bob, list.dequeue());
        assertSame(carol, list.dequeue());
        assertTrue(list.isEmpty());
    }

    public void testEnqueueAfterEmptied() {
        list.enqueue(alice);
        list.dequeue();
        list.enqueue(bob);
        assertEquals(1, list.size());
        assertSame(bob, list.peek());
        assertEquals(1, list.getPosition(bob));
    }

    public void testDequeueThenReenqueueSameUser() {
        list.enqueue(alice);
        list.dequeue();
        list.enqueue(alice);
        assertEquals(1, list.size());
        assertTrue(list.contains(alice));
    }

    public void testPeekEmpty() {
        Exception thrown = null;
        try {
            list.peek();
        }
        catch (NoSuchElementException e) {
            thrown = e;
        }
        assertNotNull(thrown);
    }

    public void testPeekDoesNotRemove() {
        list.enqueue(alice);
        list.enqueue(bob);
        assertSame(alice, list.peek());
        assertSame(alice, list.peek());
        assertEquals(2, list.size());
    }

    public void testIsEmptyAndSize() {
        assertTrue(list.isEmpty());
        assertEquals(0, list.size());
        list.enqueue(alice);
        assertFalse(list.isEmpty());
        assertEquals(1, list.size());
        list.enqueue(bob);
        assertEquals(2, list.size());
        list.dequeue();
        assertEquals(1, list.size());
    }

    public void testContainsPresent() {
        list.enqueue(alice);
        list.enqueue(bob);
        assertTrue(list.contains(alice));
        assertTrue(list.contains(bob));
    }

    public void testContainsAbsent() {
        list.enqueue(alice);
        assertFalse(list.contains(bob));
    }

    public void testContainsEmptyList() {
        assertFalse(list.contains(alice));
    }

    public void testContainsNull() {
        list.enqueue(alice);
        assertFalse(list.contains(null));
    }

    public void testContainsAfterDequeue() {
        list.enqueue(alice);
        list.enqueue(bob);
        list.dequeue();
        assertFalse(list.contains(alice));
        assertTrue(list.contains(bob));
    }

    public void testGetPositionAbsent() {
        list.enqueue(alice);
        assertEquals(-1, list.getPosition(bob));
    }

    public void testGetPositionEmptyList() {
        assertEquals(-1, list.getPosition(alice));
    }

    public void testGetPositionNull() {
        list.enqueue(alice);
        assertEquals(-1, list.getPosition(null));
    }

    public void testGetPositionUpdatesAfterDequeue() {
        list.enqueue(alice);
        list.enqueue(bob);
        list.enqueue(carol);
        list.dequeue();
        assertEquals(1, list.getPosition(bob));
        assertEquals(2, list.getPosition(carol));
    }

    public void testRemoveNull() {
        list.enqueue(alice);
        assertFalse(list.remove(null));
        assertEquals(1, list.size());
    }

    public void testRemoveEmptyList() {
        assertFalse(list.remove(alice));
    }

    public void testRemoveAbsent() {
        list.enqueue(alice);
        assertFalse(list.remove(bob));
        assertEquals(1, list.size());
    }

    public void testRemoveOnlyElement() {
        list.enqueue(alice);
        assertTrue(list.remove(alice));
        assertTrue(list.isEmpty());
        assertEquals(0, list.size());
        assertFalse(list.contains(alice));
        Exception thrown = null;
        try {
            list.peek();
        }
        catch (NoSuchElementException e) {
            thrown = e;
        }
        assertNotNull(thrown);
    }

    public void testRemoveHead() {
        list.enqueue(alice);
        list.enqueue(bob);
        list.enqueue(carol);
        assertTrue(list.remove(alice));
        assertEquals(2, list.size());
        assertSame(bob, list.peek());
        assertEquals(1, list.getPosition(bob));
        assertEquals(2, list.getPosition(carol));
    }

    public void testRemoveMiddle() {
        list.enqueue(alice);
        list.enqueue(bob);
        list.enqueue(carol);
        assertTrue(list.remove(bob));
        assertEquals(2, list.size());
        assertFalse(list.contains(bob));
        assertEquals(1, list.getPosition(alice));
        assertEquals(2, list.getPosition(carol));
    }

    public void testRemoveTail() {
        list.enqueue(alice);
        list.enqueue(bob);
        list.enqueue(carol);
        assertTrue(list.remove(carol));
        assertEquals(2, list.size());
        assertFalse(list.contains(carol));
        assertEquals(2, list.getPosition(bob));
    }

    public void testEnqueueAfterRemovingTail() {
        list.enqueue(alice);
        list.enqueue(bob);
        list.remove(bob);
        list.enqueue(carol);
        assertEquals(2, list.size());
        assertEquals(1, list.getPosition(alice));
        assertEquals(2, list.getPosition(carol));
        assertSame(alice, list.dequeue());
        assertSame(carol, list.dequeue());
    }

    public void testEnqueueAfterRemovingOnlyElement() {
        list.enqueue(alice);
        list.remove(alice);
        list.enqueue(bob);
        assertEquals(1, list.size());
        assertSame(bob, list.peek());
        assertSame(bob, list.dequeue());
        assertTrue(list.isEmpty());
    }

    public void testRemoveThenReenqueueSameUser() {
        list.enqueue(alice);
        list.enqueue(bob);
        list.remove(alice);
        list.enqueue(alice);
        assertEquals(1, list.getPosition(bob));
        assertEquals(2, list.getPosition(alice));
    }

    public void testRemoveSameUserTwice() {
        list.enqueue(alice);
        assertTrue(list.remove(alice));
        assertFalse(list.remove(alice));
    }

    public void testSeparateListsAreIndependent() {
        WaitingList other = new WaitingList("Meeting Room");
        list.enqueue(alice);
        other.enqueue(bob);
        assertTrue(list.contains(alice));
        assertFalse(list.contains(bob));
        assertTrue(other.contains(bob));
        assertFalse(other.contains(alice));
        assertEquals("Meeting Room", other.getCategory());
    }
    
    public void testEnqueueDuplicateDifferentObjectSamePid() {
        list.enqueue(alice);
        User aliceCopy = new User("pid1", "Alice Copy");
        Exception thrown = null;
        try {
            list.enqueue(aliceCopy);
        }
        catch (IllegalStateException e) {
            thrown = e;
        }
        assertNotNull(thrown);
        assertEquals(1, list.size());
    }

    public void testEnqueueSameNameDifferentPid() {
        list.enqueue(alice);
        User otherAlice = new User("pid9", "Alice");
        list.enqueue(otherAlice);
        assertEquals(2, list.size());
        assertEquals(2, list.getPosition(otherAlice));
    }

    public void testContainsEqualUserDifferentObject() {
        list.enqueue(alice);
        assertTrue(list.contains(new User("pid1", "Different Name")));
    }

    public void testGetPositionEqualUserDifferentObject() {
        list.enqueue(alice);
        list.enqueue(bob);
        assertEquals(2, list.getPosition(new User("pid2", "Bobby")));
    }

    public void testRemoveEqualUserDifferentObject() {
        list.enqueue(alice);
        list.enqueue(bob);
        assertTrue(list.remove(new User("pid1", "Alice Again")));
        assertEquals(1, list.size());
        assertFalse(list.contains(alice));
        assertSame(bob, list.peek());
    }
}