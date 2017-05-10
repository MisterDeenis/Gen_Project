package modele.genome.data;

import java.util.SortedSet;
import java.util.TreeSet;

import utils.Mappable;

/**
 * Classe représentant les caractères de remplacement pour les différentes
 * allèles quand il y a un conflit entre deux allèles possibles
 */
public enum Wildcard implements Mappable<SortedSet<Allele>, Allele> {
	Y(Allele.Y, Allele.YREPLACE), K(Allele.K, Allele.KREPLACE), M(Allele.M, Allele.MREPLACE), S(Allele.S,
			Allele.SREPLACE), W(Allele.W, Allele.WREPLACE), R(Allele.R, Allele.RREPLACE), B1(Allele.B,
					Allele.B1REPLACE), B2(Allele.B, Allele.B2REPLACE), B3(Allele.B, Allele.B3REPLACE), D1(Allele.D,
							Allele.D1REPLACE), D2(Allele.D, Allele.D2REPLACE), D3(Allele.D, Allele.D3REPLACE), H1(
									Allele.H, Allele.H1REPLACE), H2(Allele.H, Allele.H2REPLACE), H3(Allele.H,
											Allele.H3REPLACE), V1(Allele.V, Allele.V1REPLACE), V2(Allele.V,
													Allele.V2REPLACE), V3(Allele.V, Allele.V3REPLACE);

	private Allele wildCard = null;
	private Allele[] replacement = null;

	private Wildcard(Allele wildCard, Allele[] replace) {
		this.wildCard = wildCard;
		this.replacement = replace;
	}

	@Override
	public SortedSet<Allele> getKey() {
		SortedSet<Allele> out = new TreeSet<>();

		for (Allele a : this.replacement) {
			out.add(a);
		}

		return out;
	}

	@Override
	public Allele getValue() {
		return wildCard;
	}

}
