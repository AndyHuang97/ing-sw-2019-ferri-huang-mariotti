package it.polimi.se2019.server.games.input;

public class GrabInput extends Input {

    private String card;

    public GrabInput(String card) {
        super("G");
        this.card = card;
    }

    public String getCard() {
        return card;
    }

    public void setCard(String card) {
        this.card = card;
    }
}
