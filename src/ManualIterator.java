import java.util.HashMap;
import java.util.Iterator;
public interface ManualIterator<T> extends Iterator<T> {

    /**
     * Get the current position of the iterator.
     * 
     * @return the current item in the iterator
     */
    T getCurrent();

    /**
     * Set the current position of the iterator.
     * 
     * @param item
     *            the new position
     */
    void setCurrent(T item);

    /**
     * Get the item that would be the item returned by the next call to {@code next()}.
     * 
     * @return the successor of the current item
     */
    T getNext();

    /**
     * Get the current traversal direction of the iterator.
     * 
     * @return the current direction
     */
    Direction getDirection();

    /**
     * Set the current traversal direction of the iterator.
     * 
     * @param dir
     *            the new direction
     */
    void setDirection(Direction dir);

}