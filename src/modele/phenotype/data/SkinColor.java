package modele.phenotype.data;

import java.util.Map;

import javafx.scene.paint.Color;
import modele.genome.data.Allele;
import modele.genome.data.LightSkinGenes;
import modele.genome.data.MediumSkinGenes;
import modele.genome.data.DarkSkinGenes;
import modele.genome.data.TargetSNPs;
import utils.Mappable;

public enum SkinColor {

	LIGHT("Pâle", Color.BURLYWOOD, Mappable.valuesAsMap(LightSkinGenes::values)), MEDIUM("Medium", Color.PERU,
			Mappable.valuesAsMap(MediumSkinGenes::values)), DARK("Foncée",
					new Color(102.0 / 255, 79.0 / 255, 60.0 / 255, 1), Mappable.valuesAsMap(DarkSkinGenes::values));

	private Color color = null;
	private String name = null;
	private Map<TargetSNPs, Allele[]> genes = null;

	private SkinColor(String name, Color _col, Map<TargetSNPs, Allele[]> genes) {
		this.color = _col;
		this.name = name;
		this.genes = genes;
	}

	public Color getColor() {
		return this.color;
	}

	public Map<TargetSNPs, Allele[]> getGenes() {
		return this.genes;
	}

	@Override
	public String toString() {
		return this.name;
	}

}
