package it.polimi.se2019.client.gui;

import it.polimi.se2019.client.View;

public class GUIView extends View {

    private GUIController guiController;

    public GUIView(GUIController guiController) {
        this.guiController = guiController;
    }


    @Override
    public void getInput() {

    }

    @Override
    public void sendInput() {

    }

    @Override
    public void showMessage(String message) {

    }

    @Override
    public void reportError(String error) {

    }

    public GUIController getGuiController() {
        return guiController;
    }

    public void setGuiController(GUIController guiController) {
        this.guiController = guiController;
    }
}
