package it.polimi.se2019.util;

public interface Observer<T> {

    void update(T message);
}