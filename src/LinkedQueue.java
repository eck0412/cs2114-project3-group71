import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Predicate;

/**
 * A singly-linked queue implementation operating in First-In, First-Out (FIFO) order.
 *
 * @param <T> the type of elements held in this queue
 * @author Lab Team
 * @version November 8, 2026
 */
public class LinkedQueue<T> {
    private Node<T> front;
    private Node<T> rear;
    private int size;

    private static class Node<T> {
        T value;
        Node<T> next;

        Node(T value) {
            this.value = value;
            this.next = null;
        }
    }

    /**
     * Creates an empty linked queue.
     */
    public LinkedQueue() {
        front = null;
        rear = null;
        size = 0;
    }

    /**
     * Adds a non-null item to the rear of the queue.
     *
     * @param item the item to add
     * @throws IllegalArgumentException if item is null
     */
    public void enqueue(T item) {
        if (item == null) {
            throw new IllegalArgumentException("Cannot enqueue null item.");
        }
        Node<T> newNode = new Node<>(item);
        if (isEmpty()) {
            front = newNode;
            rear = newNode;
        } else {
            rear.next = newNode;
            rear = newNode;
        }
        size++;
    }

    /**
     * Removes and returns the front item.
     *
     * @return the front item
     * @throws NoSuchElementException if the queue is empty
     */
    public T dequeue() {
        if (isEmpty()) {
            throw new NoSuchElementException("Queue is empty.");
        }
        T value = front.value;
        front = front.next;
        size--;
        if (isEmpty()) {
            rear = null;
        }
        return value;
    }

    /**
     * Returns the front item without removing it.
     *
     * @return the front item
     * @throws NoSuchElementException if the queue is empty
     */
    public T peek() {
        if (isEmpty()) {
            throw new NoSuchElementException("Queue is empty.");
        }
        return front.value;
    }

    /**
     * Returns true when the queue has no items.
     *
     * @return true if empty, false otherwise
     */
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Returns the number of items in the queue.
     *
     * @return queue size
     */
    public int size() {
        return size;
    }

    /**
     * Removes the first matching item based on the given predicate.
     *
     * @param match predicate condition
     * @return true if an item was removed, false otherwise
     */
    public boolean removeFirst(Predicate<T> match) {
        if (isEmpty()) {
            return false;
        }

        if (match.test(front.value)) {
            dequeue();
            return true;
        }

        Node<T> curr = front;
        while (curr.next != null) {
            if (match.test(curr.next.value)) {
                curr.next = curr.next.next;
                size--;
                if (curr.next == null) {
                    rear = curr;
                }
                return true;
            }
            curr = curr.next;
        }
        return false;
    }

    /**
     * Returns an immutable copy of the items in front-to-rear order.
     *
     * @return immutable list snapshot
     */
    public List<T> snapshot() {
        List<T> list = new ArrayList<>();
        Node<T> curr = front;
        while (curr != null) {
            list.add(curr.value);
            curr = curr.next;
        }
        return Collections.unmodifiableList(list);
    }
}