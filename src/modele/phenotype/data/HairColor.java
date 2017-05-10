package modele.phenotype.data;

import java.util.Map;

import javafx.scene.paint.Color;
import modele.genome.data.Allele;
import modele.genome.data.TargetSNPs;
import utils.Mappable;
import modele.genome.data.BlondHairGenes;
import modele.genome.data.BrownHairGenes;
import modele.genome.data.BlackHairGenes;
import modele.genome.data.RedHairGenes;

public enum HairColor {

	BLOND(new Color(218.0 / 255, 188.0 / 255, 80.0 / 255, 1), "Blond",
			Mappable.valuesAsMap(BlondHairGenes::values)), BROWN(new Color(140.0 / 255, 102.0 / 255, 0.0, 1), "Brun",
					Mappable.valuesAsMap(BrownHairGenes::values)), BLACK(Color.BLACK, "Noir",
							Mappable.valuesAsMap(BlackHairGenes::values)), RED(
									new Color(173.0 / 255, 79.0 / 255, 9.0 / 255, 1), "Roux",
									Mappable.valuesAsMap(RedHairGenes::values));

	private Map<TargetSNPs, Allele[]> genes = null;
	private Color color = null;
	private String name = null;

	private HairColor(Color color, String name, Map<TargetSNPs, Allele[]> genes) {
		this.color = color;
		this.genes = genes;
		this.name = name;
	}

	public Color getColor() {
		return this.color;
	}

	public String getName() {
		return this.name;
	}

	public Map<TargetSNPs, Allele[]> getGenes() {
		return this.genes;
	}

	@Override
	public String toString() {
		return this.getName();
	}

}
