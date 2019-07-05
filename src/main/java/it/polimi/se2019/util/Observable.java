package it.polimi.se2019.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * This is a key part, the observable is the baseline for the majority of our classes, provides the method used to communicate internally
 *
 * @author AH
 *
 */
public abstract class Observable<T> implements Serializable {
    private final transient List<Observer<T>> observers = new ArrayList<>();

    /**
     * Register a new observer
     *
     * @param observer the observer to link
     *
     */
    public void register(Observer<T> observer){
        synchronized (observers) {
            observers.add(observer);
        }
    }

    /**
     * Deregister an observer
     *
     * @param observer the observer to de-link
     *
     */
    public void deregister(Observer<T> observer){
        synchronized (observers) {
            observers.remove(observer);
        }
    }

    /**
     * Notify all observers of something
     *
     * @param message this is the notification
     *
     */
    protected void notify(T message){
        synchronized (observers) {
            for(Observer<T> observer : observers){
                try {
                    observer.update(message);
                } catch (Observer.CommunicationError e) {
                    // do nothing
                }
            }
        }
    }
}
