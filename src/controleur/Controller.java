package controleur;

import javafx.collections.ObservableList;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Slider;
import javafx.scene.layout.Pane;
import modele.EnvironmentThreeD;
import modele.MusicPlayer;
import modele.phenotype.data.EyeColor;
import modele.phenotype.data.HairColor;
import modele.phenotype.data.SkinColor;

public class Controller {

	@FXML
	private ChoiceBox<EyeColor> choiceBoxYeux;

	@FXML
	private Slider sliderEcartYeux;

	@FXML
	private ChoiceBox<HairColor> choiceBoxCouleurCheveux;

	@FXML
	private ChoiceBox<SkinColor> choiceBoxSkin;

	@FXML
	private Slider sliderProfondeurOreilles;

	@FXML
	private Slider sliderHauteurOreilles;

	@FXML
	private Slider sliderHauteurBouche;

	@FXML
	private Slider sliderHauteurNez;

	@FXML
	private Slider sliderEcartSourcils;

	@FXML
	private Slider sliderLonguerVisage;

	@FXML
	private Slider sliderAvancementSourcils;

	@FXML
	private Slider sliderProeminenceMenton;

	@FXML
	private CheckMenuItem muteButton;

	@FXML
	private Slider sliderJoue;

	@FXML
	private Slider sliderArche;

	@FXML
	private Slider sliderNarine;

	@FXML
	private Slider sliderPointe;

	@FXML
	private Slider sliderFront;

	@FXML
	private Slider sliderCou;

	@FXML
	private Pane pane3D;

	@FXML
	private Slider sliderRotationOreilles;

	@FXML
	private Slider sliderGrosseurBouche;

	private EnvironmentThreeD envirnm = null;
	private MusicPlayer player = null;

	/**
	 * Propriété dictant si nous sommes en mode ADN ou non. Si oui, on ouvre une
	 * nouvelle fenêtre dans CtrlModeADN
	 */
	private BooleanProperty modeADNProperty = null;

	@FXML
	public void initialize() {
		this.player = new MusicPlayer(MusicPlayer.SONG1);
		modeADNProperty = new SimpleBooleanProperty();
		setModeADN(false);
		buildEyeColorBox();
		buildSkinColorBox();
		buildHairColorBox();
		setControlsValue();
		ajouterEcouteurs();
		envirnm = new EnvironmentThreeD(choiceBoxYeux.getValue(), choiceBoxSkin.getValue(),
				choiceBoxCouleurCheveux.getValue());
		pane3D.getChildren().add(envirnm.buildWorld(pane3D, (int) pane3D.getPrefWidth(), (int) pane3D.getPrefHeight()));
	}

	public void setModeADN(boolean val) {
		modeADNProperty.set(val);
	}

	public boolean getModeADN() {
		return modeADNProperty.get();
	}

	public BooleanProperty getModeADNProperty() {
		return modeADNProperty;
	}

	public MusicPlayer getPlayer() {
		return this.player;
	}

	public EnvironmentThreeD getEnvirnm() {
		return envirnm;
	}

	/**
	 * Détermine les valeurs initiales des contrôles
	 */
	private void setControlsValue() {
		choiceBoxYeux.setValue(EyeColor.BROWN);
		choiceBoxSkin.setValue(SkinColor.MEDIUM);
		choiceBoxCouleurCheveux.setValue(HairColor.BLOND);
	}

	/**
	 * Permet de "muter" la musique
	 * 
	 * @param event
	 */
	@FXML
	private void mutePlayer(ActionEvent event) {
		getPlayer().changeMute();
	}

	/**
	 * Permet d'ouvrir le mode ADN
	 * 
	 * @param event
	 */
	@FXML
	private void ouvrirModeADN(ActionEvent event) {
		setModeADN(true);
	}

	/**
	 * Met les éléments dans la ChoiceBox pour la couleur des yeux.
	 */
	private void buildEyeColorBox() {
		ObservableList<EyeColor> list = FXCollections.observableArrayList();
		for (EyeColor c : EyeColor.values()) {
			list.add(c);
		}
		choiceBoxYeux.setItems(list);
	}

	/**
	 * Met les éléments dans la ChoiceBox pour la couleur des yeux.
	 */
	private void buildSkinColorBox() {
		ObservableList<SkinColor> list = FXCollections.observableArrayList();
		for (SkinColor c : SkinColor.values()) {
			list.add(c);
		}
		choiceBoxSkin.setItems(list);
	}

	/**
	 * Met les éléments dans la ChoiceBox pour la couleur des cheveux.
	 */
	private void buildHairColorBox() {
		ObservableList<HairColor> list = FXCollections.observableArrayList();
		for (HairColor c : HairColor.values()) {
			list.add(c);
		}
		choiceBoxCouleurCheveux.setItems(list);
	}

	/**
	 * Ajoute les multiples écouteurs sur les contrôles
	 */
	private void ajouterEcouteurs() {

		choiceBoxYeux.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<EyeColor>() {
			public void changed(ObservableValue<? extends EyeColor> ov, EyeColor old_val, EyeColor new_val) {
				envirnm.getFace().getEyeL().setColor(new_val);
				envirnm.getFace().getEyeR().setColor(new_val);
				envirnm.changementWorld();
			}
		});

		choiceBoxSkin.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<SkinColor>() {
			public void changed(ObservableValue<? extends SkinColor> ov, SkinColor old_val, SkinColor new_val) {
				envirnm.getFace().setSkinColor(new_val);
				envirnm.changementWorld();
			}
		});

		sliderEcartYeux.valueProperty().addListener(new ChangeListener<Number>() {
			public void changed(ObservableValue<? extends Number> ov, Number old_val, Number new_val) {
				envirnm.getFace().getEyeL().setRotation((new_val.doubleValue()) * 3);
				envirnm.getFace().getEyeR().setRotation((new_val.doubleValue()) * 3);
				envirnm.getFace().setEyeDistance(new_val.floatValue());
				envirnm.changementWorld();
			}
		});

		sliderHauteurOreilles.valueProperty().addListener(new ChangeListener<Number>() {
			public void changed(ObservableValue<? extends Number> ov, Number old_val, Number new_val) {
				envirnm.getFace().setHauteurOreilles(new_val.floatValue());
				envirnm.changementWorld();
			}
		});

		sliderProfondeurOreilles.valueProperty().addListener(new ChangeListener<Number>() {
			public void changed(ObservableValue<? extends Number> ov, Number old_val, Number new_val) {
				envirnm.getFace().setProfondeurOreilles(new_val.floatValue());
				envirnm.getFace().getEarL().setProfondeur(new_val.doubleValue());
				envirnm.getFace().getEarR().setProfondeur(new_val.doubleValue());
				envirnm.changementWorld();
			}
		});

		sliderHauteurBouche.valueProperty().addListener(new ChangeListener<Number>() {
			public void changed(ObservableValue<? extends Number> ov, Number old_val, Number new_val) {
				envirnm.getFace().setPositionBouche(new_val.floatValue());
				envirnm.changementWorld();
			}
		});

		sliderHauteurNez.valueProperty().addListener(new ChangeListener<Number>() {
			public void changed(ObservableValue<? extends Number> ov, Number old_val, Number new_val) {
				envirnm.getFace().setPositionNez(new_val.floatValue());
				envirnm.changementWorld();
			}
		});

		sliderEcartSourcils.valueProperty().addListener(new ChangeListener<Number>() {
			public void changed(ObservableValue<? extends Number> ov, Number old_val, Number new_val) {
				envirnm.getFace().setPositionSourcils(new_val.floatValue());
				envirnm.changementWorld();
			}
		});

		sliderJoue.valueProperty().addListener(new ChangeListener<Number>() {
			public void changed(ObservableValue<? extends Number> ov, Number old_val, Number new_val) {
				envirnm.getFace().setGrosseurJoues(new_val.floatValue());
				envirnm.changementWorld();
			}
		});

		sliderArche.valueProperty().addListener(new ChangeListener<Number>() {
			public void changed(ObservableValue<? extends Number> ov, Number old_val, Number new_val) {
				envirnm.getFace().setPositionArche(new_val.floatValue());
				envirnm.changementWorld();
			}
		});

		sliderNarine.valueProperty().addListener(new ChangeListener<Number>() {
			public void changed(ObservableValue<? extends Number> ov, Number old_val, Number new_val) {
				envirnm.getFace().setEcartNarine(new_val.floatValue());
				envirnm.changementWorld();
			}
		});

		sliderPointe.valueProperty().addListener(new ChangeListener<Number>() {
			public void changed(ObservableValue<? extends Number> ov, Number old_val, Number new_val) {
				envirnm.getFace().setLongueurPointe(new_val.floatValue());
				envirnm.changementWorld();
			}
		});

		sliderFront.valueProperty().addListener(new ChangeListener<Number>() {
			public void changed(ObservableValue<? extends Number> ov, Number old_val, Number new_val) {
				envirnm.getFace().setPositionFront(new_val.floatValue());
				envirnm.changementWorld();
			}
		});

		choiceBoxCouleurCheveux.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<HairColor>() {
			public void changed(ObservableValue<? extends HairColor> ov, HairColor old_val, HairColor new_val) {
				envirnm.getFace().getHair().setCouleurCheveux(new_val);
				envirnm.getFace().getSourcilsL().setColor(new_val);
				envirnm.getFace().getSourcilsR().setColor(new_val);
				envirnm.changementWorld();
			}
		});

		sliderLonguerVisage.valueProperty().addListener(new ChangeListener<Number>() {
			public void changed(ObservableValue<? extends Number> ov, Number old_val, Number new_val) {
				envirnm.getFace().setLongueurFace(new_val.floatValue());
				envirnm.changementWorld();
			}
		});

		sliderAvancementSourcils.valueProperty().addListener(new ChangeListener<Number>() {
			public void changed(ObservableValue<? extends Number> ov, Number old_val, Number new_val) {
				envirnm.getFace().setProeminenceSourcils(new_val.floatValue());
				envirnm.changementWorld();
			}
		});

		sliderProeminenceMenton.valueProperty().addListener(new ChangeListener<Number>() {
			public void changed(ObservableValue<? extends Number> ov, Number old_val, Number new_val) {
				envirnm.getFace().setProeminenceMenton(new_val.floatValue());
				envirnm.changementWorld();
			}
		});

		sliderRotationOreilles.valueProperty().addListener(new ChangeListener<Number>() {
			public void changed(ObservableValue<? extends Number> observable, Number old_val, Number new_val) {
				envirnm.getFace().setRotationOreille(new_val.doubleValue());
				envirnm.changementWorld();
			}
		});

		sliderCou.valueProperty().addListener(new ChangeListener<Number>() {
			public void changed(ObservableValue<? extends Number> observable, Number old_val, Number new_val) {
				envirnm.getFace().setGrosseurCou(new_val.floatValue());
				envirnm.changementWorld();
			}
		});

		sliderGrosseurBouche.valueProperty().addListener(new ChangeListener<Number>() {
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				envirnm.getFace().setGrosseurBouche(newValue.doubleValue());
				envirnm.changementWorld();
			}
		});

	}
}
