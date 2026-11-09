package src;

import java.util.NoSuchElementException;

public class WaitingList {
    
    private class Node {
        
        private User user;
        private Node next;
        
        public Node(User user) {
            this.user = user;
            next = null;
        }
        
        public User getUser() {
            return user;
        }
        
        public void setNext(Node next) {
            this.next = next; 
        }
        
        public Node getNext() {
            return next;
        }
    }
    
    
    //~ Fields ................................................................

    
    private String category;
    private Node head;
    private Node tail;
    private int size;
    
    
    //~ Constructors ..........................................................

    public WaitingList(String category) {
        
        if (category == null || category.isBlank()) {
            throw new IllegalArgumentException("Category cannot be null or blank.");
        }
        
        this.category = category;
        this.head = null;
        this.tail = null;
        this.size = 0;
    }
    
    //~Public  Methods ........................................................

    public void enqueue(User user) {
       if (user == null) {
           throw new IllegalArgumentException("User cannot be null.");
       }
       
       if (contains(user)) {
           throw new IllegalStateException("User is already in the line.");
       }
       
       Node newNode = new Node(user);
       
       if (isEmpty()) {
           head = newNode;
           tail = newNode;
       }
       else {
           tail.setNext(newNode);
           tail = newNode;
       }
       size++;
       
    }
    
    public User dequeue() {
        if (isEmpty()) {
            throw new NoSuchElementException("The queue is empty.");
        }
        User removed = head.getUser();
        head = head.getNext();
        size--;
        if (size == 0) {
            tail = null;
        }
        return removed;
    }
    
    public User peek() {
        if (isEmpty()) {
            throw new NoSuchElementException("The queue is empty.");
        }
        return head.getUser();
    }
    
    public boolean isEmpty() {
        return size == 0;
    }
    
    public int size() {
        return size;
    }
    
    public boolean contains(User user) {
        if (user == null) {
            return false;
        }
        Node current = head;
        while (current != null) {
            if (current.getUser().equals(user)) {
                return true;
            }
            current = current.getNext();
        }
        return false;
    }
    
    public int getPosition(User user) {
        if (user == null) {
            return -1;
        }

        int position = 1;
        Node current = head;
        while (current != null) {
            if (current.getUser().equals(user)) {
                return position;
            }
            current = current.getNext();
            position++;
        }
        return -1;
    }
    
    public boolean remove(User user) {
        if (user == null) {
            return false;
        }
        Node previous = null;
        Node current = head;
        while (current != null && !current.getUser().equals(user)) {
            previous = current;
            current = current.getNext();
        }
        if (current == null) {
            return false;
        }
        if (previous == null) {
            head = current.getNext();
        }
        else {
            previous.setNext(current.getNext());
        }
        if (current == tail) {
            tail = previous;
        }
        size--;
        if (size == 0) {
            head = null;
            tail = null;
        }
        return true;
    }
    
    public String getCategory() {
        return category;
    }
}
