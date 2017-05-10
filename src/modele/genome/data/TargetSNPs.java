package modele.genome.data;

/**
 * Enum représentant les SNP qu'il faut chercher dans le fichier fasta (les SNP
 * codant les variations génétiques que nous avons choisis d'implémenter dans
 * notre programme)
 */
public enum TargetSNPs {
	RS12913832("12913832", "HERC2", "15"),
	RS1545397("1545397", "OCA2", "15"), 
	RS1426654("1426654", "SLC24A5","15"), 
	RS6119471("6119471", "ASIP", "20"), 
	RS12896399("12896399", "SLC24A4", "14"), 
	RS12203592("12203592","IRF4","6"),
	RS16891982("16891982", "SLC45A2", "5"), 
	RS4959270("4959270", "EXOC2", "6"), 
	RS28777("28777","SLC45A2", "5"), 
	RS26722("26722", "SLC45A2", "5"),
	RS1393350("1393350", "TYR", "11"),
	RS4904868("4904868","SLC24A4", "14"),
	RS2402130("2402130", "SLC24A4", "14"),
	RS1800407("1800407", "OCA2", "15"),
	RS4778138("4778138", "OCA2", "15"),
	RS916977("916977", "HERC2", "15"),
	RS8039195("8039195", "HERC2", "15"),
	RS1805005("1805005", "MC1R", "16"),
	RS2228479("2228479", "MC1R", "16"),
	RS11547464("11547464", "MC1R", "16"),
	RS1805007("1805007", "MC1R", "16"),
	RS1805008("1805008", "MC1R", "16"),
	RS885479("885479", "MC1R", "16"),
	RS1805009("1805009", "MC1R", "16"),
	RS1015362("1015362", "ASIP", "20"),
	RS2378249("2378249", "ASIP", "20");

	private String id = null;
	private String gene = null;
	private String chromosomeNbr = null;

	private TargetSNPs(String id, String gene, String chromosomeNbr) {
		this.id = id;
		this.gene = gene;
		this.chromosomeNbr = chromosomeNbr;
	}

	public String getId() {
		return id;
	}

	public String getGene() {
		return gene;
	}

	public String getChromosomeNbr() {
		return chromosomeNbr;
	}

}
