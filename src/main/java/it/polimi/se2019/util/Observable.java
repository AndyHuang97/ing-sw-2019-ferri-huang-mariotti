package it.polimi.se2019.util;

import java.util.ArrayList;
import java.util.List;

public abstract class Observable<T> {
    private transient final List<Observer<T>> observers = new ArrayList<>();

    public void register(Observer<T> observer){
        synchronized (observers) {
            observers.add(observer);
        }
    }

    public void deregister(Observer<T> observer){
        synchronized (observers) {
            observers.remove(observer);
        }
    }

    protected void notify(T message){
        synchronized (observers) {
            for(Observer<T> observer : observers){
                observer.update(message);
            }
        }
    }

}
