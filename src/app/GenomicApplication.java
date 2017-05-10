package app;

import java.io.IOException;
import controleur.Controller;
import controleur.CtrlModeADN;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class GenomicApplication extends Application {

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/vue/interface_v.02.fxml"));
		BorderPane root = (BorderPane) loader.load();
		Scene scene = new Scene(root);
		scene.getStylesheets().add(getClass().getResource("/styles/mainstyle.css").toString());
		stage.setTitle("Genomic Physionomy Viewer");
		stage.setScene(scene);
		stage.setResizable(false);
		stage.show();
		Controller c = ((Controller) loader.getController());

		ajoutEcouteurs(c);
	}

	public void ajoutEcouteurs(Controller controller) {
		controller.getModeADNProperty().addListener(new ChangeListener<Boolean>() {
			public void changed(ObservableValue<? extends Boolean> ov, Boolean old_val, Boolean new_val) {
				if (new_val == true) {
					creationFenetre(controller);
				}
			}
		});
	}

	public void creationFenetre(Controller controller) {
		try {
			FXMLLoader loaderModeADN = new FXMLLoader(getClass().getResource("/vue/ModeADN.fxml"));
			Pane pane = (Pane) loaderModeADN.load();
			Stage stageModeADN = new Stage();
			Scene sceneModeADN = new Scene(pane);
			sceneModeADN.getStylesheets().add(getClass().getResource("/styles/modeADNstyle.css").toString());
			stageModeADN.setScene(sceneModeADN);
			stageModeADN.initModality(Modality.APPLICATION_MODAL);
			stageModeADN.setTitle("Mode ADN");
			stageModeADN.setScene(sceneModeADN);
			stageModeADN.setResizable(false);
			stageModeADN.show();

			CtrlModeADN ctrl = ((CtrlModeADN) loaderModeADN.getController());
			ctrl.createFenetreModeADN(controller.getEnvirnm().getFace());
			controller.setModeADN(false);

			stageModeADN.setOnCloseRequest(new EventHandler<WindowEvent>() {

				@Override
				public void handle(WindowEvent arg0) {
					ctrl.setArreterThread(false);
				}
			});

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
