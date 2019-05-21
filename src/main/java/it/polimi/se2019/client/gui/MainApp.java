package it.polimi.se2019.client.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

/**
 * This is the main class of the GUI.
 */
public class MainApp extends Application {

    private Stage primaryStage;
    private BorderPane rootlayout;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Adrenaline");

        initRootLayout();
        showPlayerBoard();

        primaryStage.setResizable(true);
        primaryStage.sizeToScene();
        primaryStage.show();

    }

    public void initRootLayout() {
        try{
            // Load root layout from fxml file
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/fxml/RootLayout.fxml"));
            rootlayout = (BorderPane) loader.load();

            // Set the scene containing the root layout
            Scene scene = new Scene(rootlayout);
            primaryStage.setScene(scene);

            RootLayoutController controller = loader.getController();
            controller.setMainApp(this);

        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public void showGameBoard(int index) {

        String map = "/images/maps/map" + index + ".png";
        Image image = new Image(map);
        ImageView iv = new ImageView();
        iv.setImage(image);
        iv.setFitWidth(1024);
        iv.setPreserveRatio(true);
        iv.setSmooth(true);
        iv.setCache(true);

        AnchorPane anchorPane = new AnchorPane();
        AnchorPane.setTopAnchor(iv, 0.0);
        AnchorPane.setRightAnchor(iv, 0.0);
        AnchorPane.setBottomAnchor(iv, 0.0);
        AnchorPane.setLeftAnchor(iv, 0.0);
        anchorPane.getChildren().add(iv);
        //anchorPane.setPrefSize(1024, 1024);
        //iv.autosize();
        //anchorPane.autosize();

        rootlayout.setCenter(anchorPane);

        //rootlayout.autosize();
    }

    public void showGameBoard() {

        try{
            // Load root layout from fxml file
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("/fxml/GameBoard.fxml"));
            AnchorPane anchorPane = (AnchorPane) loader.load();

            // Set the scene containing the root layout
            rootlayout.setCenter(anchorPane);

        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public void selectGameBoard(File file) {

        String sub = file.getName().split(".png")[0];
        String index = sub.substring(sub.length()-1);
        //System.out.println(index);
        showGameBoard(Integer.parseInt(index));

    }

    public void showPlayerBoard() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/fxml/PlayerBoard.fxml"));
            AnchorPane anchorPane = (AnchorPane) loader.load();

            rootlayout.setCenter(anchorPane);

            PlayerBoardController controller = loader.getController();
            controller.setMainApp(this);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }
}
