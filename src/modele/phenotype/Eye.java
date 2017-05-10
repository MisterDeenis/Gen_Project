package modele.phenotype;

import java.util.ArrayList;

import modele.phenotype.data.EyeColor;

/**
 * Classe représentant un oeil
 */
public class Eye extends BodyPart {

	private EyeColor color;
	private double rotation;

	public Eye(EyeColor color, String... groups) {
		this(color, null, groups);
	}

	public Eye(EyeColor color, ArrayList<String> ignore, String... groups) {
		super(ignore, groups);
		this.color = color;
		rotation = 0;
	}

	public EyeColor getCouleurYeux() {
		return color;
	}

	public void setColor(EyeColor eyeColor) {
		this.color = eyeColor;
	}

	public double getRotation() {
		return rotation;
	}

	public void setRotation(double rotation) {
		this.rotation = rotation;
	}

}
