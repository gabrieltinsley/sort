import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.NoSuchElementException;

/**
 * Double-linked node implementation of IndexedUnsortedList.
 * A List Iterator with working remove(), add(), set() methods.
 * 
 * @author Gabriel Tinsley
 * 
 * @param <T> type to store
 */
public class IUDoubleLinkedList<T> implements IndexedUnsortedList<T> {
    private Node<T> head, tail;
    private int size;
    private int modCount;

    /** Creates an empty list */
    public IUDoubleLinkedList() {
        head = tail = null;
        size = 0;
        modCount = 0;
    }

    @Override
    public void addToFront(T element) {
        Node<T> newNode = new Node<T>(element);

        if (!isEmpty()) { // checks for empty lists
            newNode.setNext(head);
            head.setPrevious(newNode);
        } else { // puts tail in the right place
            tail = newNode;
        }

        head = newNode;

        size++;
        modCount++;
    }

    @Override
    public void addToRear(T element) {
        Node<T> newNode = new Node<T>(element);
        if (head == null) { // empty list
            head = newNode;
        } else { // every other kind of list
            newNode.setPrevious(tail);
            tail.setNext(newNode);
        }
        tail = newNode;

        size++;
        modCount++;

    }

    @Override
    public void add(T element) {
        addToRear(element);
    }

    @Override
    public void addAfter(T element, T target) {
        Node<T> current = head;

        while (current != null && !current.getElement().equals(target)) { // find target
            current = current.getNext();
        }

        if (current == null) {
            throw new NoSuchElementException();
        }

        Node<T> newNode = new Node<T>(element);
        newNode.setNext(current.getNext());
        if (current.getNext() != null) { // in the middle of list
            current.getNext().setPrevious(newNode);
        }
        current.setNext(newNode);
        newNode.setPrevious(current);

        if (newNode.getNext() == null) { // puts tail in the right place
            tail = newNode;
        }
        
        size++;
        modCount++;
    }

    @Override
    public void add(int index, T element) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException();
        }

        if (index == 0) {
            addToFront(element);
        } else if (index == size) {
            addToRear(element);
        } else {
            Node<T> current = head;

            for (int i = 0; i < index - 1; i++) { // finds index to add after
                current = current.getNext();
            }

            Node<T> newNode = new Node<T>(element);
            newNode.setNext(current.getNext());

            if (current.getNext() != null) { // in the middle of list
                current.getNext().setPrevious(newNode);
            }

            current.setNext(newNode);
            newNode.setPrevious(current);

            if (newNode.getNext() == null) { // puts tail in the right plave
                tail = newNode;
            }

            size++;
            modCount++;
        }
    }

    @Override
    public T removeFirst() {
        if (isEmpty()) {
            throw new NoSuchElementException();
        }
        T retVal;

        if (size() == 1) { // puts head and tail in the right place for a one element list
            retVal = head.getElement();
            head = tail = null;
        } else { // removes everything else
            Node<T> current = head;

            retVal = current.getElement();
            head = current.getNext();
            head.setPrevious(null);
        }

        size--;
        modCount++;

        return retVal;
    }

    @Override
    public T removeLast() {
        if (isEmpty()) {
            throw new NoSuchElementException();
        }
        T retVal;

        if (size() == 1) { // puts head and tail in the right place for a one element list
            retVal = tail.getElement();
            head = tail = null;
        } else { // removes everything else
            Node<T> current = tail;

            retVal = current.getElement();
            tail = current.getPrevious();
            tail.setNext(null);
        }

        size--;
        modCount++;

        return retVal;
    }

    @Override
    public T remove(T element) {
        if (isEmpty()) {
            throw new NoSuchElementException();
        }
        Node<T> current = head;

        while (current != null && !current.getElement().equals(element)) { // finds element to remove
            current = current.getNext();
        }

        if (current == null) {
            throw new NoSuchElementException();
        }

        T retVal = null;

        if (current != null) {
            retVal = current.getElement();
        }

        Node<T> tempN = current.getNext();

        Node<T> tempP = current.getPrevious();

        if (tempP != null) { // middle of list
            tempP.setNext(tempN);
        } else { // front of list
            head = tempN;
        }

        if (tempN != null) { // middle of list
            tempN.setPrevious(tempP);
        } else { // end of list
            tail = tempP;
        }

        size--;
        modCount++;

        return retVal;
    }

    @Override
    public T remove(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException();
        }
        T retVal;

        if (index == 0) { // first node
            retVal = removeFirst();
        } else { // somewhere in the middle
            Node<T> current = head;

            for (int i = 0; i < index; i++) {
                current = current.getNext();
            }

            retVal = current.getElement();

            Node<T> tempP = current.getPrevious();
            Node<T> tempN = current.getNext();

            if (tempP != null) {
                tempP.setNext(tempN);
            } else {
                head = head.getNext();
            }

            if (tempN != null) {
                tempN.setPrevious(tempP);
            } else {
                tail = tail.getPrevious();
            }
            size--;
            modCount++;
        }

        return retVal;
    }

    @Override
    public void set(int index, T element) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException();
        }

        Node<T> current = head;

        for (int i = 0; i < index; i++) { // finds index to change
            current = current.getNext();
        }

        current.setElement(element);

        modCount++;
    }

    @Override
    public T get(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException();
        }
        T retVal;
        if (index == 0) { // first node
            retVal = head.getElement();
        } else { // somewhere in the middle
            Node<T> current = head;
            for (int i = 0; i < index; i++) {
                current = current.getNext();
            }

            retVal = current.getElement();

        }
        return retVal;
    }

    @Override
    public int indexOf(T element) {
        Node<T> current = head;
        int currentIndex = 0;

        while (current != null && !current.getElement().equals(element)) { // finds index of element
            current = current.getNext();
            currentIndex++;
        }

        if (current == null) {
            currentIndex = -1;
        }

        return currentIndex;
    }

    @Override
    public T first() {
        if (isEmpty()) {
            throw new NoSuchElementException();
        }

        return head.getElement();
    }

    @Override
    public T last() {
        if (isEmpty()) {
            throw new NoSuchElementException();
        }

        return tail.getElement();
    }

    @Override
    public boolean contains(T target) {
        return (indexOf(target) != -1);
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append("[");
        for (T element : this) {
            str.append(element.toString());
            str.append(", ");
        }
        if (size > 0) {
            str.delete(str.length() - 2, str.length()); // remove trailing ", "
        }
        str.append("]");
        return str.toString();
    }

    @Override
    public Iterator<T> iterator() {
        return new DLLIterator();
    }

    @Override
    public ListIterator<T> listIterator() {
        return new DLLIterator();
    }

    @Override
    public ListIterator<T> listIterator(int startingIndex) {
        return new DLLIterator(startingIndex);
    }

    /** ListIterator (and basic iterator) for IUDoubleLinkedList */
    private class DLLIterator implements ListIterator<T> {
        private Node<T> nextNode;
        private Node<T> lastReturnedNode;
        private int nextIndex;
        private int iterModCount;

        /** Intialize iterator at the start of the list */
        public DLLIterator() {
            this(0);
        }

        /**
         * Intialize iterator in front of the given starting index
         * 
         * @param startingIndex where you begin
         */
        public DLLIterator(int startingIndex) {
            if (startingIndex < 0 || startingIndex > size) {
                throw new IndexOutOfBoundsException();
            }

            nextNode = head;
            for (int i = 0; i < startingIndex; i++) { // finds starting index
                nextNode = nextNode.getNext();
            }
            nextIndex = startingIndex;
            iterModCount = modCount;
            lastReturnedNode = null;
        }

        @Override
        public boolean hasNext() {
            if (iterModCount != modCount) {
                throw new ConcurrentModificationException();
            }
            return nextNode != null;
        }

        @Override
        public T next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }

            T retVal = nextNode.getElement();
            lastReturnedNode = nextNode;
            nextNode = nextNode.getNext();
            nextIndex++;
            return retVal;
        }

        @Override
        public boolean hasPrevious() {
            if (iterModCount != modCount) {
                throw new ConcurrentModificationException();
            }

            return nextNode != head;
        }

        @Override
        public T previous() {
            if (!hasPrevious()) {
                throw new NoSuchElementException();
            }

            if (nextNode != null) { // middle of list
                nextNode = nextNode.getPrevious();
            } else { // end of list
                nextNode = tail;
            }

            lastReturnedNode = nextNode;

            nextIndex--;
            return nextNode.getElement();
        }

        @Override
        public int nextIndex() {
            if (iterModCount != modCount) {
                throw new ConcurrentModificationException();
            }

            return nextIndex;
        }

        @Override
        public int previousIndex() {
            if (iterModCount != modCount) {
                throw new ConcurrentModificationException();
            }

            return nextIndex - 1;
        }

        @Override
        public void remove() {
            if (iterModCount != modCount) {
                throw new ConcurrentModificationException();
            }
            if (lastReturnedNode == null) {
                throw new IllegalStateException();
            }
            // front of list
            if (lastReturnedNode != head) { 
                lastReturnedNode.getPrevious().setNext(lastReturnedNode.getNext());
            } else {
                head = head.getNext();
            }
            //end of list
            if (lastReturnedNode != tail) {
                lastReturnedNode.getNext().setPrevious(lastReturnedNode.getPrevious());
            } else {
                tail = tail.getPrevious();
            }
            //next and previous
            if (lastReturnedNode != nextNode) {
                nextIndex--;
            } else {
                nextNode = nextNode.getNext();
            }

            lastReturnedNode = null;
            size--;
            iterModCount++;
            modCount++;

        }

        @Override
        public void set(T e) {
            if (iterModCount != modCount) {
                throw new ConcurrentModificationException();
            }

            if (lastReturnedNode == null) {
                throw new IllegalStateException();
            }

            lastReturnedNode.setElement(e); // sets the element

            iterModCount++;
            modCount++;
        }

        @Override
        public void add(T e) {
            if (iterModCount != modCount) {
                throw new ConcurrentModificationException();
            }

            Node<T> newNode = new Node<T>(e);

            if (head == null) { // empty list
                head = newNode;
                tail = newNode;
            } else if (nextNode == head) { // front of list
                newNode.setNext(head);
                head.setPrevious(newNode);
                head = newNode;
            } else if (nextNode == null) { // end of list
                newNode.setPrevious(tail);
                tail.setNext(newNode);
                tail = newNode;
            } else { // everything else
                newNode.setNext(nextNode);
                newNode.setPrevious(nextNode.getPrevious());
                nextNode.getPrevious().setNext(newNode);
                nextNode.setPrevious(newNode);
            }

            size++;
            modCount++;
            iterModCount++;
            nextIndex++;
        }

    }

}
