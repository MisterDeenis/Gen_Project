package utils;

import java.util.ArrayList;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableFloatArray;

public class MapTools {
	/**
	 * Détermine si les valeurs de deux array sont égales.
	 * 
	 * @param p
	 * @param q
	 * @return vrai ou faux dépendamment si les array sont pareils.
	 */

	public static boolean findIfEquals(ObservableFloatArray p, ObservableFloatArray q) {
		boolean out = true;
		for (int i = 0; (i < p.size()) && (i < q.size()); i++) {

			if (p.get(i) != q.get(i)) {
				out = false;
			}

		}
		return out;
	}

	/**
	 * Trouve les index des points de l'array "targets" dans l'array "values",
	 * si les points de l'array "targets" sont dans "values.
	 * 
	 * @param values
	 *            - array recherché
	 * @param targets
	 *            - array des points à trouver
	 * @return liste des index de values
	 */
	public static List<Integer> findIndexOfValues(ObservableFloatArray values, ObservableFloatArray targets) {
		List<Integer> out = new ArrayList<Integer>();
		for (int i = 0; i < values.size() / 3; i++) {
			for (int j = 0; j < targets.size() / 3; j++) {

				if ((values.get(3 * i) == targets.get(3 * j)) && (values.get((3 * i) + 1) == targets.get((3 * j) + 1))
						&& (values.get((3 * i) + 2) == targets.get((3 * j) + 2))) {
					out.add(i);
				}

			}
		}
		return out;
	}

	public static ObservableFloatArray createAndConvertArray(ObservableFloatArray original) {
		ObservableFloatArray pTemp = FXCollections.observableFloatArray();
		pTemp.addAll(original);
		return pTemp;
	}
}
