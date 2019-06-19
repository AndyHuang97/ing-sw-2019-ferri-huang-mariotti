package it.polimi.se2019.client.gui;

import javafx.fxml.FXML;
import javafx.stage.FileChooser;

import java.io.File;

/**
 * The root layout contains the menu bar.
 */
public class RootLayoutController {

    private ClientGui clientGui;

    /**
     *  Is called by the main application to set itself.
     *
     * @param clientGui
     */
    public void setClientGui(ClientGui clientGui) {
        this.clientGui = clientGui;
    }

    /**
     * Open a FileChooser to let user choose the map.
     */
    @FXML
    public void handleOpen() {
        FileChooser fileChooser = new FileChooser();

        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(
                "board images (*.png)", "*.png");
        fileChooser.getExtensionFilters().add(extFilter);

        File file = fileChooser.showOpenDialog(clientGui.getPrimaryStage());

        if (file != null) {
            //clientGui.selectGameBoard(file);
        }
    }

    /**
     * Closes the application.
     */
    @FXML
    private void handleExit() {
        System.exit(0);
    }


}
