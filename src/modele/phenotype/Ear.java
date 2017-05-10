package modele.phenotype;

import java.util.ArrayList;

/**
 * Classe représentant une oreille
 */
public class Ear extends BodyPart {

	private double rotation;
	private double profondeur;

	public Ear(String... groups) {
		this(null, groups);
	}

	public Ear(ArrayList<String> ignore, String... groups) {
		super(ignore, groups);
		rotation = 0;
	}

	public double getRotation() {
		return rotation;
	}

	public void setRotation(double rotation) {
		this.rotation = rotation;
	}

	public double getProfondeur() {
		return profondeur;
	}

	public void setProfondeur(double profondeur) {
		this.profondeur = profondeur;
	}

}
