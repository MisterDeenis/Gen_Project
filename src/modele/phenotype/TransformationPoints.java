package modele.phenotype;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.collections.FXCollections;
import javafx.collections.ObservableFloatArray;
import javafx.geometry.Point3D;
import javafx.scene.transform.Rotate;

public class TransformationPoints {

	/**
	 * Une map avec comme clé les différents group et comme value les points 3D
	 * de référence de ces groupes, loader une seule fois au début du programme.
	 */
	private Map<String, ObservableFloatArray> points3DIni = null;
	/**
	 * Une map avec comme clé les différents group et comme value les points 3D
	 * de ces groupes, qu'on utilise et qu'on rafraichît durant la vie de l'app.
	 * C'est elle qui est modifiée pour faire des changements dans la vue.
	 */
	private Map<String, ObservableFloatArray> points3DUpdater = null;
	/**
	 * Une map avec comme clé un point commun entre deux groupes et comme value
	 * une liste de ces deux groupes. Utilisée pour bouger les points communs
	 * entre différents groupes lorsqu'il y a un changement sur ceux-ci.
	 */
	private Map<ObservableFloatArray, List<String>> pointsSupp = null;

	public TransformationPoints() {
		points3DIni = new HashMap<String, ObservableFloatArray>();
		points3DUpdater = new HashMap<String, ObservableFloatArray>();
		pointsSupp = new HashMap<ObservableFloatArray, List<String>>();
	}

	public void addIni3DPoints(String group, ObservableFloatArray points) {
		points3DIni.put(group, createAndConvertArray(points));
		points3DUpdater.put(group, points);
	}

	public ObservableFloatArray getPointsUpdater(String group) {
		return points3DUpdater.get(group);
	}

	/**
	 * Permet d'appliquer une translation sur une des composantes du visage
	 * 
	 * @param part
	 *            la partie du visage
	 * @param transformation
	 *            les paramètres de la transformation (x, y ,z)
	 */
	public void applyTranslation(BodyPart part, List<String> groupREM, Point3D transformation) {
		for (String group : part.getSubParts()) {
			updatePointsTranslation(group, groupREM, transformation);
		}
		//updatePointsTranslation(part.getSubParts().get(0), groupREM, transformation);

	}

	// TODO JCB la réfléchir, la faire pis pas mal toutte là
	public static Rotate applyRotation(Point3D pointCentre, char axe, double degres) {
		Rotate objet = null;
		switch (axe) {
		case 'x':
			objet = new Rotate(degres, pointCentre.getX(), pointCentre.getY(), pointCentre.getZ(), Rotate.X_AXIS);
			break;
		case 'y':
			objet = new Rotate(degres, pointCentre.getX(), pointCentre.getY(), pointCentre.getZ(), Rotate.Y_AXIS);
			break;
		case 'z':
			objet = new Rotate(degres, pointCentre.getX(), pointCentre.getY(), pointCentre.getZ(), Rotate.Z_AXIS);
			break;
		}
		return objet;
	}

	/**
	 * TODO shorten this function && put comments Trouve les différents points
	 * communs entre chaque groupe et les met dans la map pointsSupp.
	 */
	public void findSiblings() {
		for (int k = 0; k < (points3DIni.values().size() - 1); k++) {
			ObservableFloatArray G1Points = (ObservableFloatArray) points3DIni.values().toArray()[k];
			for (int l = k + 1; l < points3DIni.values().size(); l++) {
				ObservableFloatArray G2Points = (ObservableFloatArray) points3DIni.values().toArray()[l];

				if (!G1Points.equals(G2Points)) {
					for (int i = 0; i < G1Points.size() / 3; i++) {

						ObservableFloatArray pointG1 = FXCollections.observableFloatArray();
						pointG1.addAll(G1Points.get(3 * i), G1Points.get((3 * i) + 1), G1Points.get((3 * i) + 2));

						for (int j = 0; j < G2Points.size() / 3; j++) {

							ObservableFloatArray pointG2 = FXCollections.observableFloatArray();
							pointG2.addAll(G2Points.get(3 * j), G2Points.get((3 * j) + 1), G2Points.get((3 * j) + 2));
							if (findIfEquals(pointG1, pointG2)) {
								String t = findKeyFromValueMap(G1Points);
								String s = findKeyFromValueMap(G2Points);

								List<String> groups = new ArrayList<String>();
								groups.add(t);
								groups.add(s);
								pointsSupp.put(pointG2, groups);

							}

						}

					}
				}

			}
		}

	}

	/**
	 * 
	 * Update le point d'un groupe selon un facteur dans chaque dimension.
	 * 
	 * @param groupADD
	 *            le groupe à modifier
	 * @param factors
	 *            - un point 3D qui contient le facteur modificateur dans chaque
	 *            dimension
	 */
	private void updatePointsTranslation(String groupADD, List<String> groupREM, Point3D factors) {
		ObservableFloatArray points = points3DUpdater.get(groupADD);
		// TODO MARCHE PAS CACA
		//TODO dodge cest dla marde
		List<Integer> dodge = new ArrayList<Integer>();
		List<ObservableFloatArray> pointsCommun = findKeyFromValueMap(groupADD);
		for (ObservableFloatArray pointCommun : pointsCommun) {
			List<String> groups = pointsSupp.get(pointCommun);
			if ((groupREM != null) && (!groupREM.isEmpty())) {
				for (String rEM : groupREM) {
					if (!groups.contains(rEM)) {
						fuck(groupADD, groups, pointCommun, factors);
					} else {
						List<Integer> index = findIndexOfValues(points3DIni.get(groupADD), pointCommun);
						dodge.addAll(index);
					}
				}
			} else {
				fuck(groupADD, groups, pointCommun, factors);
			}

		}
		for (int i = 0; i < points.size() / 3; i++) {
			if ((dodge != null) && (!dodge.isEmpty())) {
				if (!dodge.contains(i)) {
					points.set(2 + (3 * i), (float) (points3DIni.get(groupADD).get(2 + (3 * i)) + factors.getX()));
					points.set(0 + (3 * i), (float) (points3DIni.get(groupADD).get(0 + (3 * i)) + factors.getY()));
					points.set(1 + (3 * i), (float) (points3DIni.get(groupADD).get(1 + (3 * i)) + factors.getZ()));
				}
			} else {
				points.set(2 + (3 * i), (float) (points3DIni.get(groupADD).get(2 + (3 * i)) + factors.getX()));
				points.set(0 + (3 * i), (float) (points3DIni.get(groupADD).get(0 + (3 * i)) + factors.getY()));
				points.set(1 + (3 * i), (float) (points3DIni.get(groupADD).get(1 + (3 * i)) + factors.getZ()));
			}
		}
	}

	private void fuck(String groupADD, List<String> groupsCommun, ObservableFloatArray pointCommun, Point3D factors) {
		for (String g : groupsCommun) {
			List<Integer> index = findIndexOfValues(points3DIni.get(g), pointCommun);
			if (!g.equals(groupADD)) {
				for (Integer i : index) {
					points3DUpdater.get(g).set((3 * i) + 2, (float) (pointCommun.get(2) + factors.getX()));
					points3DUpdater.get(g).set((3 * i) + 0, (float) (pointCommun.get(0) + factors.getY()));
					points3DUpdater.get(g).set((3 * i) + 1, (float) (pointCommun.get(1) + factors.getZ()));
				}
			}
		}
	}

	private ObservableFloatArray removeValue(ObservableFloatArray points, List<Integer> toBeRemoved) {
		ObservableFloatArray out = FXCollections.observableFloatArray();
		float[] tempPoints = points.toArray(null);
		System.out.println(toBeRemoved.toString());
		for (int i = 0; i < tempPoints.length / 3; i++) {
			if (!toBeRemoved.contains(new Integer(i))) {
				out.addAll(tempPoints[i]);
				out.addAll(tempPoints[i + 1]);
				out.addAll(tempPoints[i + 2]);
			}
		}

		// System.out.println(newbie.toString());
		return out;
	}

	/**
	 * Détermine si les valeurs de deux array sont égales.
	 * 
	 * @param p
	 * @param q
	 * @return vrai ou faux dépendamment si les array sont pareils.
	 */

	private boolean findIfEquals(ObservableFloatArray p, ObservableFloatArray q) {
		boolean out = true;
		for (int i = 0; (i < p.size()) && (i < q.size()); i++) {
			if (p.get(i) != q.get(i)) {
				out = false;
			}
		}
		return out;
	}

	/**
	 * Trouve la valeur du groupe dans points3DIni à l'aide de points.
	 * 
	 * @param points
	 *            - un array de points
	 * @return la valeur texte du groupe
	 */
	private String findKeyFromValueMap(ObservableFloatArray points) {
		String group = "";
		boolean notFound = true;
		for (String e : points3DIni.keySet()) {
			if (notFound && (points.equals(points3DIni.get(e)))) {
				group = e;
				notFound = false;
			}
		}
		return group;
	}

	/**
	 * Retourne les différents points communs avec d'autres groupes de ce
	 * "group". Utilise la map pointsSupp.
	 * 
	 * @param group
	 * @return une liste des différents points communs
	 */
	private List<ObservableFloatArray> findKeyFromValueMap(String group) {
		List<ObservableFloatArray> out = new ArrayList<ObservableFloatArray>();
		for (ObservableFloatArray e : pointsSupp.keySet()) {
			if (pointsSupp.get(e).contains(group)) {
				out.add(e);
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
	private List<Integer> findIndexOfValues(ObservableFloatArray values, ObservableFloatArray targets) {
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

	private ObservableFloatArray createAndConvertArray(ObservableFloatArray original) {
		ObservableFloatArray pTemp = FXCollections.observableFloatArray();
		pTemp.addAll(original);
		return pTemp;
	}

}
