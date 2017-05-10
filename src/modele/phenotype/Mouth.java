package modele.phenotype;

import java.util.ArrayList;

/**
 * Classe représentant une bouche
 */
public class Mouth extends BodyPart {

	private double scale;

	public Mouth(String... groups) {
		this(null, groups);
	}
	
	public Mouth(ArrayList<String> ignore, String... groups) {
		super(ignore, groups);
		scale = 1;
	}

	public double getScale() {
		return scale;
	}

	public void setScale(double scale) {
		this.scale = scale;
	}
}
