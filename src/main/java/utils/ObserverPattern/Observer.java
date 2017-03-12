package utils.ObserverPattern;

/**
 * Created by Sergiu on 11/19/2016.
 */
public interface Observer<E> {
    void update(Observable<E> e);
}
