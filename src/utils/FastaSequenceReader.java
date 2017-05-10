
package utils;

import java.io.*;
import java.util.*;

/**
 * Cette classe lit un fichier fasta et ressort tous les SNPs utiles et leurs
 * séquences
 *
 * @author http://www.cs.utexas.edu/~mobios/cs329e/rosetta/src/FastaSequence.
 *         java MÀJ Les génies du génome
 */

public final class FastaSequenceReader {

	private String[] description;
	private String[] sequence;
	private List<String> targets = null;
	private Map<String, String> sequences = null;

	public FastaSequenceReader(File file, List<String> target) throws IOException {
		this.targets = ListTools.formatList(target);
		readSequenceFromFile(file);
		sequences = createMap();
	}

	public Map<String, String> getSequences() {
		return this.sequences;
	}
	
	/**
	 * Permet de lire les séquences ciblées dans un fichier au format FASTA
	 * standard
	 *
	 * @param file
	 *            Le fichier à lire.
	 * @throws IOException
	 *             si le fichier est non-conforme
	 */
	private void readSequenceFromFile(File file) throws IOException {
		List<String> desc = new ArrayList<String>();
		List<String> seq = new ArrayList<String>();
		boolean read = false;
		int index = targets.size() - 1;
		BufferedReader in = new BufferedReader(new FileReader(file));
		StringBuffer buffer = new StringBuffer();
		String line = in.readLine();

		// HEADER
		if (line == null) {
			in.close();
			throw new IOException(file.getName() + " is an empty file");
		}
		if (line.charAt(0) != '>') {
			in.close();
			throw new IOException("First line of " + file.getName() + " should start with '>'");
		} else if (line.contains(targets.get(index))) {
			desc.add(line);
			read = true;
			targets.remove(index);
		}

		// BODY
		for (line = in.readLine().trim(); line != null
				&& (!targets.isEmpty() || seq.size() != desc.size()); line = in.readLine()) {
			// LIGNE DE DESCRIPTION
			if (line.length() > 0 && line.charAt(0) == '>' && line.contains(targets.get(index))) {
				desc.add("rs" + targets.get(index));
				read = true;
				targets.remove(index);
				// LIGNE DE SÉQUENCE
			} else if (read) {
				buffer.append(line.trim());
			}
			// LIGNE VIDE - FIN DE SEQUENCE
			if (line.isEmpty() && (targets.size() == index)) {
				seq.add(buffer.toString());
				buffer = new StringBuffer();
				read = false;
				index--;
			}
		}

		// CLOSING
		storeData(seq, desc);
		in.close();

	}

	/**
	 * Met les données dans une structure de données
	 *
	 * @param seq
	 *            une liste de séquences
	 * @param desc
	 *            une liste de description de séquences
	 */
	private void storeData(List<String> seq, List<String> desc) {
		description = new String[desc.size()];
		sequence = new String[seq.size()];

		for (int i = 0; i < desc.size(); i++) {
			description[i] = (String) desc.get(i);
			sequence[i] = (String) seq.get(i);
		}
	}

	/**
	 * Crée la map qui contient les séquences d'ADN et leurs descriptions
	 *
	 * @return la map
	 */
	private HashMap<String, String> createMap() {
		HashMap<String, String> map = new HashMap<String, String>();

		for (int i = 0; i < description.length; i++) {
			map.put(description[i], sequence[i]);
		}

		return map;
	}
}