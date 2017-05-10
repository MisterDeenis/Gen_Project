package modele.phenotype;

import java.util.ArrayList;

/**
 * Classe générale d'une partie du corps donnant un modèle à ses descendants sur
 * leurs attributs et leurs méthodes
 */
public class BodyPart {

	/**
	 * Partie du corps incluse dans cette partie du corps (Les bosses du nez
	 * dans le nez)
	 */
	private ArrayList<String> subParts = null;
	/**
	 * Parties du corps à ignorer si on bouge cette partie (pas bouger le nez
	 * quand on bouge les yeux)
	 */
	private ArrayList<String> ignore = null;

	public BodyPart(String... groups) {
		this(null, groups);
	}

	public BodyPart(BodyPart... groups) {
		this(null, groups);
	}

	public BodyPart(ArrayList<String> ignore, String... groups) {
		this.subParts = new ArrayList<>();
		this.ignore = new ArrayList<>();
		for (String gr : groups) {
			this.subParts.add(gr);
		}
		this.ignore = ignore;
	}

	public BodyPart(ArrayList<String> ignore, BodyPart... groups) {
		this.subParts = new ArrayList<>();
		this.ignore = new ArrayList<>();
		for (BodyPart gr : groups) {
			for (String subgr : gr.getSubParts())
				this.subParts.add(subgr);
			this.ignore.addAll(gr.getIgnore());
		}
		this.ignore.addAll(ignore);
	}

	public ArrayList<String> getSubParts() {
		return subParts;
	}

	public void setIgnore(String... ign) {
		this.ignore.clear();
		for (String gr : ign) {
			this.ignore.add(gr);
		}

	}

	public ArrayList<String> getIgnore() {
		return ignore;
	}
}
