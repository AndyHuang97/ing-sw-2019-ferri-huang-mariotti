package it.polimi.se2019.server.games;

import java.util.Collections;
import java.util.List;

/**
 * This class implement a deck of object of a generic type. It gives access to a couple of handy methods to shuffle
 * and draw.
 *
 * @param <T> type of the cards of the deck
 *
 * @author Rodolfo Mariotti
 */
public class Deck<T> {

    private List<T> cardList;

    /**
     * Constructor that creates a new deck from a list of objects.
     *
     * @param cardList list of objects that will become the cards of the deck
     */
    public Deck(List<T> cardList) {
        this.cardList = cardList;
    }

    /**
     * Draw a card from the deck.
     *
     * @return top card of the deck or null if the deck is empty
     */
    public T drawCard() {
        if (!cardList.isEmpty()) {
            return cardList.remove(0);
        }

        return null;
    }

    /**
     * Shuffle the deck.
     */
    public void shuffle() {
        Collections.shuffle(cardList);
    }
}
