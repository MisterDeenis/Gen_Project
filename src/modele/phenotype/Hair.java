package modele.phenotype;

import java.util.ArrayList;

import modele.phenotype.data.HairColor;

public class Hair extends BodyPart {

	public HairColor couleurCheveux;

	public Hair(HairColor hc, String... groups) {
		this(hc, null, groups);
	}

	public Hair(HairColor hc, ArrayList<String> ignore, String... groups) {
		super(ignore, groups);
		setCouleurCheveux(hc);
	}

	public HairColor getCouleurCheveux() {
		return couleurCheveux;
	}

	public void setCouleurCheveux(HairColor couleurCheveux) {
		this.couleurCheveux = couleurCheveux;
	}

}
