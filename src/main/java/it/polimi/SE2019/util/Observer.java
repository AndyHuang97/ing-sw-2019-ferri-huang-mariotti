package it.polimi.SE2019.util;

public interface Observer<T> {

    void update(T message);
}