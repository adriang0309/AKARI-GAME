package iut.prj2024;

import java.io.IOException;

import iut.prj2024.view.AkariController;
import iut.prj2024.view.JeuController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class AkariApp extends Application {

    private Stage primaryStage;
    private BorderPane rootPane;

    private String dimension;

    @Override
    public void start(Stage primaryStage) throws Exception {

        this.primaryStage = primaryStage;
        this.rootPane = new BorderPane();
        Scene scene = new Scene(rootPane);
        rootPane.setStyle(STYLESHEET_CASPIAN);
        scene.getStylesheets().add(AkariApp.class.getResource("style.css").toExternalForm());
        primaryStage.setTitle("Akari Jeu");
        primaryStage.setScene(scene);
        loadMenu();
        primaryStage.show();

    }

    public void loadMenu() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(AkariApp.class.getResource("view/Menu.fxml"));
            BorderPane borderPane = loader.load(); // peut lever IOEXception
            AkariController ctrl = loader.getController();
            ctrl.setFenetrePrincipale(primaryStage);
            ctrl.setAkariApp(this);
            this.rootPane.setCenter(borderPane);
        } catch (Exception e) {
            System.out.println("Ressource FXML non disponible : Menu");
            System.exit(1);
        }

    }

    public void loadJeu(String dimension) {
        try {
	        FXMLLoader loader = new FXMLLoader(getClass().getResource("view/Jeu.fxml"));
	        BorderPane vueSimple = loader.load();
	        JeuController ctrl = loader.getController();
	        this.rootPane.setCenter(vueSimple);
	        ctrl.setFenetrePrincipale(primaryStage);
            ctrl.setAkariApp(this);
            ctrl.initializeGame(dimension);
	        
	    } catch (IOException e) {
	        System.out.println("Ressource FXML non disponible : Jeu");
	        System.exit(1);
	    }

    }

    public static void main2(String[] args) {
        Application.launch(args);
    }

}