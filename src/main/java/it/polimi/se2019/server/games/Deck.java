package it.polimi.se2019.server.games;

import java.util.Collections;
import java.util.List;

public class Deck<T> {

    private List<T> deck;

    public Deck(List<T> deck) {
        this.deck = deck;
    }

    public T drawCard()
    {
        return deck.remove(0);
    }

    public void shuffle()
    {
        Collections.shuffle(deck);
    }
}
