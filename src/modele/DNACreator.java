package modele;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import exception.ConstructionException;
import modele.genome.Chromosome;
import modele.genome.DNA;
import modele.genome.data.Allele;
import modele.genome.data.TargetSNPs;
import modele.phenotype.Face;

/**
 * Classe qui va créer l'ADN selon le visage, donc affecter les variations
 * appropriées au génome
 *
 * @author Les génies du génome
 *
 */
public class DNACreator {

	private DNA dna = null;
	private Face face = null;
	private DoubleProperty readingProgressProperty = null;

	public DNACreator(Face f, DoubleProperty progress, BooleanProperty booleanp)
			throws ConstructionException, IOException, URISyntaxException {
		if (f != null) {
			this.readingProgressProperty = progress;
			this.dna = new DNA(chrSymByTargets(), progress, booleanp);
			this.face = f;
			updateDNA();
		} else {
			throw new ConstructionException("VISAGE INEXISTANT");
		}

	}

	/**
	 * Met à jour l'ADN selon l'aspect actuel du visage
	 */
	public void updateDNA() {
		setGenes(this.face.getEyeL().getCouleurYeux().getGenes());
		setGenes(this.face.getSkinColor().getGenes());
		setGenes(this.face.getHair().getCouleurCheveux().getGenes());
	}

	public DNA getDna() {
		return dna;
	}

	public DoubleProperty readingProgressProperty() {
		return readingProgressProperty;
	}

	public double getReadingProgress() {
		return readingProgressProperty.get();
	}

	public void setReadingProgress(double val) {
		this.readingProgressProperty.set(val);
	}

	/**
	 * Retourne la liste des différents chromosomes à inspecter selon les snp à
	 * trouver (le Set élimine les doublons)
	 *
	 * @param chrNbr
	 *            le numéro du chromosome
	 * @return la liste des identifiants des SNPs
	 */
	private Set<String> chrSymByTargets() {
		TargetSNPs[] tgt = TargetSNPs.values();
		Set<String> chrSym = new HashSet<String>();

		for (TargetSNPs t : tgt) {
			chrSym.add(t.getChromosomeNbr());
		}

		return chrSym;
	}

	/**
	 * Affecte les bonnes variations sur l'ADN
	 *
	 * @param map
	 *            contient comme clés les SNP et comme valeurs les allèles
	 */
	private void setGenes(Map<TargetSNPs, Allele[]> map) {
		map.forEach((snp, alleles) -> {
			int pos = 0;
			TargetSNPs current = snp;
			for (Chromosome chr : getDna().getChrPair(current.getChromosomeNbr())) {
				chr.getSNPByRS("rs" + current.getId()).setAllele(alleles[pos]);
				pos++;
			}
		});

	}
}
