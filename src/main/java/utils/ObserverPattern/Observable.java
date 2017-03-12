package utils.ObserverPattern;

/**
 * Created by Sergiu on 11/19/2016.
 */
public interface Observable<E> {
    void addObserver(Observer<E> observer);
    void notifyObservers();
}
