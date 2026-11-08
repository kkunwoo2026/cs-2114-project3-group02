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
            throw new IllegalArgumentException("Name cannot be null or blank.");
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
       } else if (size == 1) {
           tail = newNode;
           head.setNext(tail);
       } else {
           tail.setNext(newNode);
           tail = newNode;
       }
       
       size++;
    }
    
    public User dequeue() {
        if (isEmpty()) {
            throw new NoSuchElementException("The queue is empty.");
        }
        User userRemoved = head.getUser();
        
        if (size == 1) {
            head = null;
            tail = null;
        } else if (size == 2) {
            head = tail;
        } else {
            head = head.getNext();
        }
        
        size--;
        return userRemoved;
    }
    
    public User peek() {
        return head.getUser();
    }
    
    public boolean isEmpty() {
        return size == 0;
    }
    
    public int size() {
        return size;
    }
    
    public boolean contains(User user) {
        boolean found = false;
        Node curNode = head;
        while(curNode != null || !found) {
            found = curNode.getUser().equals(user);
            curNode = curNode.getNext();
        }
  
        return found;
    }
    
    public int getPosition(User user) {
        if (!contains(user)) {
            return -1;
        }
        int curIndex = 1;
        Node curNode = head;
        
        while (curNode != null) {
            if (curNode.getUser().equals(user)) {
                break;
            }
            
            curIndex++;
            curNode = curNode.getNext();
        }
        
        return curIndex;
    }
    
    public boolean remove(User user) {
        if (!contains(user)) {
            return false;
        }
        
        Node curNode = head;
        
        if (size == 1) {
            if (user.equals(curNode.getUser())) {
                head = null;
                tail = null;
            }
        } else if (size == 2) {
            if (user.equals(curNode.getUser())) {
                head = tail;
            } else if (user.equals(curNode.getNext().getUser())) {
                tail = head;
            }
        } else {
            while (curNode.getNext() != tail) {
                if (user.equals(curNode.getNext().getUser())) {
                    curNode.setNext(curNode.getNext().getNext());
                }
                curNode = curNode.getNext();
            }
            
            if (curNode.getNext().getUser().equals(user)) {
                tail = curNode;
            }
        }
        
        size--;
        return true;
    }
    
    public String getCategory() {
        return category;
    }
}
