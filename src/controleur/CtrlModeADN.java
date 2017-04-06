package controleur;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import com.sun.jndi.url.dns.dnsURLContext;

import exception.ConstructionException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.Pane;
import modele.DNACreator;
import modele.EnvironmentThreeD;
import modele.genome.Chromosome;
import modele.phenotype.Face;
import utils.FastaExporter;
import vue.FichierChooser;
import vue.MessageAlert;

public class CtrlModeADN {

	private DNACreator dNACreator = null;
	private Face face = null;
	@FXML
	private Pane pane;

	public void createFenetreModeADN(Face face) {
		this.face = face;
		modeDNA();
	}

	/**
	 * Permet de choisir un fichier du répertoire afin d'enregistrer
	 * l'exportation de l'ADN
	 * 
	 * @param event
	 */
	@FXML
	public void ouvrirDirectoryChooser(ActionEvent event) {
		FichierChooser directoryChooser = new FichierChooser(pane.getScene().getWindow());
		
		if (directoryChooser.getFichierChoisi() != null) {
			boolean flagError = (dNACreator == null) ? modeDNA() : false;
			
			if (!flagError) {
				try {
					FastaExporter.sauvegarder(dNACreator.getDna(),
							directoryChooser.getFichierChoisi().getAbsolutePath());
				} catch (IOException e) {
					new MessageAlert("Erreur lors de l'écriture du fichier. Échec de l'exportation");
				}
			
			} else {
				new MessageAlert("Échec de l'exportation");
			}

		}
	}

	/**
	 * Créer l'adn selon la face en mémoire et gère les exceptions si les
	 * fichiers à lire sont introuvables
	 * 
	 * @return Vrai s'il y a eu une erreur qui empêche la construction, faux si
	 *         tout est correct
	 */
	public boolean modeDNA() {
		boolean flagError = false;

		try {
			dNACreator = new DNACreator(this.face);
		} catch (IOException e) {
			File newFolder = alertAndChooseFile(e.getMessage());
			Chromosome.setAltSrcFile(newFolder);

			try {
				dNACreator = new DNACreator(this.face);
			} catch (IOException e1) {
				new MessageAlert("Impossible de trouver le(s) fichier(s).");
				flagError = true;
			} catch (ConstructionException e1) {
				new MessageAlert(e1.getMessage());
			} catch (URISyntaxException e1) {
				new MessageAlert(e1.getMessage());
			}

		} catch (ConstructionException e) {
			new MessageAlert(e.getMessage());
		} catch (URISyntaxException e) {
			new MessageAlert(e.getMessage());
		}

		return flagError;
	}

	/**
	 * Affiche une erreur et ouvre un DirectoryChooser
	 * 
	 * @param message
	 *            le message a afficher
	 * @return le path du dossier sélectionné
	 */
	private File alertAndChooseFile(String message) {
		new MessageAlert(message);
		FichierChooser directoryChooser = new FichierChooser(pane.getScene().getWindow());
		return directoryChooser.getFichierChoisi();
	}
}
