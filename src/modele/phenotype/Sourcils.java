package modele.phenotype;

import java.util.ArrayList;
import modele.phenotype.data.HairColor;

/**
 * Classe représentant un sourcil
 */
public class Sourcils extends BodyPart {

	private HairColor color = null;

	public Sourcils(HairColor color, String... groups) {
		this(color, null, groups);
	}

	public Sourcils(HairColor color, ArrayList<String> ignore, String... groups) {
		super(ignore, groups);
		setColor(color);
	}

	public HairColor getColor() {
		return this.color;
	}

	public void setColor(HairColor color) {
		this.color = color;
	}

}
