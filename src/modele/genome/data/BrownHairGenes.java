package modele.genome.data;

import utils.Mappable;

public enum BrownHairGenes implements Mappable<TargetSNPs, Allele[]> {
	SNP1(TargetSNPs.RS1393350, Allele.HOMOG), 
	SNP2(TargetSNPs.RS4778138, Allele.HOMOT),
    SNP3(TargetSNPs.RS1015362, Allele.HOMOC);

	private TargetSNPs snp = null;
	private Allele[] alleles = null;

	private BrownHairGenes(TargetSNPs snp, Allele[] alleles) {
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
