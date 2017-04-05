package modele.genome.data;

import utils.Mapable;

public enum RedHairGenes implements Mapable<TargetSNPs, Allele[]> {
	;

	TargetSNPs snp = null;
	Allele[] alleles = null; 
	
	private RedHairGenes(TargetSNPs snp, Allele[] alleles) {
		this.alleles = alleles;
		this.snp = snp;
	}
	
	@Override
	public TargetSNPs getKey() {
		return snp;
	}

	@Override
	public Allele[] getValue() {
		return alleles;
	}

}