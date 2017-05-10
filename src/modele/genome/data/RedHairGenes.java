package modele.genome.data;

import utils.Mappable;

public enum RedHairGenes implements Mappable<TargetSNPs, Allele[]> {
	SNP1(TargetSNPs.RS1800407, Allele.HOMOC), 
	SNP2(TargetSNPs.RS916977, Allele.HOMOC), 
	SNP3(TargetSNPs.RS8039195,Allele.HOMOT), 
	SNP4(TargetSNPs.RS2228479, Allele.HOMOG), 
	SNP5(TargetSNPs.RS11547464, Allele.HOMOG), 
	SNP6(TargetSNPs.RS1805007, Allele.HOMOC), 
	SNP7(TargetSNPs.RS1805008, Allele.HOMOC), 
    SNP8(TargetSNPs.RS1805009, Allele.HOMOG), 
	SNP9(TargetSNPs.RS2378249, Allele.HOMOA);

	private TargetSNPs snp = null;
	private Allele[] alleles = null;

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
