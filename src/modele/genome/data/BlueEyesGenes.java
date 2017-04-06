package modele.genome.data;

import utils.Mapable;

public enum BlueEyesGenes implements Mapable<TargetSNPs, Allele[]>{

	SNP1(TargetSNPs.RS12203592, Allele.HOMOT), 
	SNP2(TargetSNPs.RS12913832, Allele.HOMOG), 
	SNP3(TargetSNPs.RS16891982,Allele.HOMOG), 
	SNP4(TargetSNPs.RS1426654, Allele.HOMOA);
	TargetSNPs snp = null;
	Allele[] alleles = null;

	private BlueEyesGenes(TargetSNPs snp, Allele[] alleles) {
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
