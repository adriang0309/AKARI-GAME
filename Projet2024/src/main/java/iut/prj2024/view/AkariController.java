package iut.prj2024.view;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import iut.prj2024.AkariApp;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.RadioButton;
import javafx.stage.Stage;

public class AkariController implements Initializable {

	@FXML
	private RadioButton size3x3;
	@FXML
	private RadioButton size5x5;
	@FXML
	private RadioButton size6x4;
	@FXML
	private RadioButton size7x7;
	@FXML
	private RadioButton size10x10;
	@FXML
	private RadioButton size14x14;

	@FXML
	private Button butJouer;

	private AkariApp AkariApp;

	private String dimension;

    private Stage fenetrePrincipale=null;


    public void setFenetrePrincipale(Stage fenetrePrincipale) {
		this.fenetrePrincipale = fenetrePrincipale;
		this.fenetrePrincipale.setOnCloseRequest(event -> actionQuitter());
	}


	public void setAkariApp (AkariApp akariApp){
		this.AkariApp = akariApp;
	}

	//definit la taille de la grille choisi
	@FXML
	private void selected3x3(){
		butJouer.setDisable(false);
		this.dimension="3x3";
	}
	@FXML
	private void selected5x5(){
		butJouer.setDisable(false);
		this.dimension="5x5";
	}
	@FXML
	private void selected6x4(){
		butJouer.setDisable(false);
		this.dimension="6x4";
	}
	@FXML
	private void selected7x7(){
		butJouer.setDisable(false);
		this.dimension="7x7";
	}
	@FXML
	private void selected10x10(){
		butJouer.setDisable(false);
		this.dimension="10x10";
	}
	@FXML
	private void selected14x14(){
		butJouer.setDisable(false);
		this.dimension="14x14";
	}

    @FXML
	private void actionQuitter() {
		Alert confirm = new Alert(AlertType.CONFIRMATION);
		confirm.setTitle("Fermeture de l'application");
		confirm.setHeaderText("Voulez-vous réellement quitter ?");
		confirm.initOwner(this.fenetrePrincipale);

		confirm.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);

		Optional<ButtonType> reponse = confirm.showAndWait();

		if(reponse.orElse(null) == ButtonType.YES){
			this.fenetrePrincipale.close();	
		} else if (reponse.orElse(null) == ButtonType.NO) {
			System.out.println("On reste encore un peu...");
		}
	} 

	@FXML
	private void actionJouer(){
		this.AkariApp.loadJeu(this.dimension);
	}


    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        // TODO Auto-generated method stub
        System.out.println("Initialisation du Menu");
    }
    
}
