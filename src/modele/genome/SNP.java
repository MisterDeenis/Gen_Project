package modele.genome;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import modele.genome.data.Allele;
import modele.genome.data.Wildcard;
import utils.Mappable;

/**
 * Classe représentant un SNP (un nucléotide souffrant de polymorphisme)
 */
public class SNP {
	private String rs = null;
	private String seqModel = null;
	private String seq = null;
	private Allele allele = null;
	private int varPos = 0;

	public SNP(String id, String seq) {
		this.rs = id;
		this.seqModel = seq;
		this.seq = "";
		this.allele = Allele.N;
	}

	public String getRS() {
		return this.rs;
	}

	public String getSeq() {
		return this.seq;
	}

	public String getSeqModel() {
		return seqModel;
	}

	public Allele getAllele() {
		return allele;
	}

	public void setAllele(Allele a) {
		this.allele = a;
		applyAlleleOnSeq();
	}

	public int getVarPos() {
		return this.varPos;
	}
	
	@Override
	public String toString() {
		return this.getRS();
	}
	
	/**
	 * Trouve les caractères possible des allèles à partir de l'énum Allele
	 *
	 * @return La liste des caractères possibles d'allèles
	 */
	private List<Character> getAlleleChars() {
		List<Character> ls = new ArrayList<>();

		for (Allele a : Allele.values()) {
			if (!a.isWildCard()) {
				ls.add(new Character(a.getPrimarySymbol()));
				ls.add(new Character(a.getSecondarySymbol()));
			}
		}
		ls.add(new Character(' '));

		return ls;
	}

	/**
	 * Utilise la séquence modèle pour créer la "vraie" séquence en intégrant
	 * l'allèle du SNP (remplace les lettres génériques par les lettres
	 * spécifiques).
	 */
	private void applyAlleleOnSeq() {
		List<Character> chrAllele = getAlleleChars();
		boolean replaced = false;
		int size = seqModel.length();

		if (seq.isEmpty()) {
			for (int i = 0; i < size; i++) {
				Character current = new Character(seqModel.charAt(i));
				if (replaced || chrAllele.contains(current)) {
					seq += current.toString();
				} else {
					seq += new Character(getAllele().getPrimarySymbol()).toString();
					this.varPos = i;
					replaced = true;
				}
			}
		} else {
			char courant = seq.charAt(getVarPos());
			char latest = getAllele().getPrimarySymbol();
			this.allele = getWildCard(Allele.valueOf(Character.toString(courant)),
					Allele.valueOf(Character.toString(latest)));
			char[] letters = getSeq().toCharArray();
			letters[getVarPos()] = getAllele().getPrimarySymbol();
			this.seq = new String(letters);
		}
	}

	/**
	 * Permet de retourner le caractère qui remplace plusieurs allèles sur un
	 * même SNP
	 *
	 * @param x
	 *            Ancien allèle
	 * @param y
	 *            Nouvel allèle
	 * @return Le caractère qui convient
	 */
	private Allele getWildCard(Allele x, Allele y) {
		Allele out = Allele.N;
		SortedSet<Allele> set = new TreeSet<>();
		set.add(x);
		set.add(y);

		if (set.size() == 1) {
			out = x;
		} else {
			Map<SortedSet<Allele>, Allele> wildcards = Mappable.valuesAsMap(Wildcard::values);
			out = wildcards.get(set);
		}

		return out;
	}
}
