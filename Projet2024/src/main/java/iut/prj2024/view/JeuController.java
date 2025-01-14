package iut.prj2024.view;

import java.net.URL;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;

import iut.prj2024.AkariApp;
import iut.prj2024.jeu.Cellule;
import iut.prj2024.jeu.JeuAraki;
import iut.prj2024.jeu.ReponsePlacement;
import iut.prj2024.jeu.TypeCellule;
import javafx.animation.AnimationTimer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class JeuController implements Initializable {

    private Stage fenetrePrincipale = null;

    private String dimension;
    private int largeur;
    private int hauteur;

    private ArrayList<Button> butList; // Liste des boutons de la grille

    private AkariApp AkariApp;
    JeuAraki jeu;
    TypeCellule typeCel;

    @FXML
    private GridPane grillejeu; // Grille de jeu

    @FXML
    private Label labelErreur;

    @FXML
    private Text temps;
    @FXML
    private Text score;

    private int scoreValue = 0;

    private AnimationTimer timer; // Timer pour mesurer le temps de jeu

    public void setFenetrePrincipale(Stage fenetrePrincipale) {
        this.fenetrePrincipale = fenetrePrincipale;
        this.fenetrePrincipale.setOnCloseRequest(event -> actionQuitter());
    }

    public void setAkariApp(AkariApp akariApp) {
        this.AkariApp = akariApp;
    }

    // Fonction qui initialise la grille initiale
    public void initializeGame(String dimensionJeu) {
        this.dimension = dimensionJeu;

        // Détermine la largeur et la hauteur à partir de la chaîne de caractères
        if (dimensionJeu.charAt(0) == '1') {
            largeur = Integer.parseInt("" + dimensionJeu.charAt(0) + dimensionJeu.charAt(1));
        } else {
            largeur = Integer.parseInt("" + dimensionJeu.charAt(0));
        }

        if (dimensionJeu.length() > 3) {
            hauteur = Integer.parseInt("" + dimensionJeu.charAt(3) + dimensionJeu.charAt(4));
        } else {
            hauteur = Integer.parseInt("" + dimensionJeu.charAt(2));
        }

        jeu = new JeuAraki(largeur, hauteur);

        // Charge la grille à partir d'un fichier de configuration pour avoir des niveaux différents, des fichiers .txt
        if (dimensionJeu.equals("6x4")) {
            jeu.chargerGrilleFromStream(
                    AkariApp.class.getResourceAsStream("jeu/dataset/" + dimensionJeu + "/easy1.txt"));
        } else {
            jeu.chargerGrilleFromStream(
                    AkariApp.class.getResourceAsStream("jeu/dataset/" + dimensionJeu + "/easy.txt"));
        }

        butList = new ArrayList<Button>();
        grillejeu.getChildren().clear();

        // Génère les boutons de la grille
        for (int j = 0; j < hauteur; j++) {
            for (int i = 0; i < largeur; i++) {
                // Récupère la cellule à la position (i, j)
                Cellule c = jeu.getCellule(i, j);
                typeCel = jeu.getCellule(i, j).getType();

                final int x = i;
                final int y = j;

                // generation des buttons
                Button button = new Button();
                butList.add(button);

                button.getStyleClass().add("game-button");
                button.setMinSize(50, 50);
                button.setMaxSize(50, 50);
                button.setFont(Font.font("Arial", FontWeight.BOLD, 20));

                // definition des cellules MUR
                if (c.getType() == TypeCellule.MUR) {
                    if (c.getNombreAmpoulesNecessaires() != -1) {
                        button.setText("" + c.getNombreAmpoulesNecessaires());
                        button.getStyleClass().add("numbered-cell");
                    } else {
                        button.setText("#");
                        button.getStyleClass().add("mur-cell");
                    }
                    button.setDisable(true);
                } else {
                    button.setText("");
                    button.getStyleClass().add("vide-cell");
                }

                // ajout des buttons crée dans le gridPane selon ça largeur et hauteur
                grillejeu.add(button, i, j);

                button.setOnAction(event -> actionCliquer(event));

            }

        }
        // Met à jour l'affichage de la grille
        updateGridDisplay();
    }

    // Fonction qui met à jour la grille utilisé suite à le placement ou effacement
    // d'une ampoule
    private void updateGridDisplay() {
        // Parcourt chaque cellule de la grille
        for (int y = 0; y < hauteur; y++) {
            for (int x = 0; x < largeur; x++) {
                // Récupère la cellule courante
                Cellule c = jeu.getCellule(x, y);

                // Récupère le bouton correspondant à la cellule courante
                Button button = (Button) grillejeu.getChildren().get(x + y * largeur);

                // Réinitialiser les classes CSS du bouton
                button.getStyleClass().clear();
                button.getStyleClass().add("game-button");

                // Définit l'apparence du bouton en fonction du type de cellule
                if (c.getType() == TypeCellule.MUR) {
                    if (c.getNombreAmpoulesNecessaires() != -1) {
                        // Affiche le nombre d'ampoules nécessaires sur le bouton
                        button.setText("" + c.getNombreAmpoulesNecessaires());
                        button.getStyleClass().add("numbered-cell");
                    } else {
                        // Affiche un mur sur le bouton
                        button.setText("#");
                        button.getStyleClass().add("mur-cell");
                    }
                    button.setDisable(true);
                } else if (c.getType() == TypeCellule.AMPOULE) {
                    // Affiche une ampoule sur le bouton
                    button.setText("*");
                    button.getStyleClass().add("ampoule-cell");
                } else if (c.getType() == TypeCellule.ILLUMINEE) {
                    // Affiche une cellule illuminée sur le bouton
                    button.setText("-");
                    button.getStyleClass().add("illuminee-cell");
                } else {
                    button.setText("");
                    button.getStyleClass().add("vide-cell");
                }
            }
        }
    }

    // Méthode appelée lors du clic sur un bouton de la grille
    @FXML
    public void actionCliquer(ActionEvent event) {
        Button butclick = (Button) event.getSource();
        int x = -1;
        int y = -1;

        // Trouver les coordonnées (x, y) de la cellule cliquée
        for (int i = 0; i < jeu.getHauteur(); i++) {
            for (int j = 0; j < jeu.getLargeur(); j++) {
                if (butList.get(i * jeu.getLargeur() + j) == butclick) {
                    x = j;
                    y = i;
                    break;
                }
            }
        }

        if (x == -1 || y == -1) {
            return; // Coordonnées non trouvées
        }

        try {

            // Essaie de placer une ampoule à la position (x, y)
            ReponsePlacement erreur = jeu.placerAmpoule(x, y);
            if (erreur == ReponsePlacement.AJOUTE_AMPOULE) {
                // Aucun erreur s'affiche et on met a jour la grille
                labelErreur.setText("");
                updateGridDisplay();

                // Mettre à jour le score
                scoreValue++;
                score.setText("Score: " + scoreValue);
            }
            if (erreur == ReponsePlacement.SUPPRIME_AMPOULE) {
                // Aucun erreur s'affiche et on met a jour la grille
                labelErreur.setText("");
                updateGridDisplay();

                // Mettre à jour le score
                scoreValue--;
                score.setText("Score: " + scoreValue);
            }
            if (erreur == ReponsePlacement.DEJA_ECLAIREE || erreur == ReponsePlacement.NON_CONFORME_NOMBRE) {
                labelErreur.setText("Vous avez fait une erreur !");
            }

            if (jeu.verifierVictoire()) {
                // on arrete le timer
                timer.stop();

                // On Affiche un message de victoire qui te permet de re-jouer et voir tes
                // resultats
                Alert confirmation2 = new Alert(AlertType.CONFIRMATION);
                confirmation2.setTitle("Akari résolu!");
                confirmation2.setHeaderText("Voulez-vous rejouer?");
                confirmation2.setContentText("Vous avez compléter le jeu en : " + temps.getText() + "\n"
                        + "Vous avez compléter le jeu avec un  " + score.getText());
                confirmation2.initOwner(fenetrePrincipale);
                confirmation2.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);
                Optional<ButtonType> reponsepartie = confirmation2.showAndWait();

                if (reponsepartie.isPresent()) {
                    if (reponsepartie.get() == ButtonType.YES) {
                        this.AkariApp.loadMenu();
                    } else if (reponsepartie.get() == ButtonType.NO) {
                        this.fenetrePrincipale.close();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Gère la fermeture de l'application
    @FXML
    private void actionQuitter() {
        Alert confirm = new Alert(AlertType.CONFIRMATION);
        confirm.setTitle("Fermeture de l'application");
        confirm.setHeaderText("Voulez-vous réellement quitter ?");
        confirm.initOwner(this.fenetrePrincipale);

        confirm.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);

        Optional<ButtonType> reponse = confirm.showAndWait();

        if (reponse.orElse(null) == ButtonType.YES) {
            fenetrePrincipale.close();
            this.AkariApp.loadMenu();
        } else if (reponse.orElse(null) == ButtonType.NO) {
            this.fenetrePrincipale.close();
        }
    }

    // Gère le retour au menu principal
    @FXML
    private void actionMenu() {
        this.AkariApp.loadMenu();
    }

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        System.out.println("Initialisation du Jeu");

        // on declanche le timer quand on initialise et le score
        score.setText("Score: " + scoreValue);
        configurationTimer();
    }

    // Configure le timer pour mesurer le temps de jeu
    private void configurationTimer() {
        timer = new AnimationTimer() {
            private long startTime;
            @Override
            public void start() {
                startTime = System.currentTimeMillis();
                super.start();
            }

            @Override
            public void stop() {
                super.stop();
            }

            @Override
            public void handle(long timestamp) {
                var elapsed = System.currentTimeMillis() - startTime;
                temps.setText(
                        String.format(
                                "%02d:%02d:%02d:%03d",
                                // gere les minisecondes
                                elapsed / 1000 / 60 / 60,
                                // gere les secondes
                                elapsed / 1000 / 60 % 60,
                                // gere les minutes
                                elapsed / 1000 % 60,
                                // gere les heures
                                elapsed % 1000));
            }
        };
        timer.start();
    }

}
