package modele.phenotype;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javafx.collections.FXCollections;
import javafx.collections.ObservableFloatArray;
import javafx.geometry.Point3D;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Transform;
import javafx.scene.transform.Translate;
import utils.MapTools;
import utils.VecteurUtilitaires;

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

	private Map<String, Set<Integer>> pointsDodge = null;

	private Map<String, List<String>> groupsREM = null;

	private Map<String, Map<String, Transform>> groupFactors = null;

	public TransformationPoints() {
		points3DIni = new HashMap<String, ObservableFloatArray>();
		points3DUpdater = new HashMap<String, ObservableFloatArray>();
		pointsSupp = new HashMap<ObservableFloatArray, List<String>>();
		pointsDodge = new HashMap<String, Set<Integer>>();
		groupsREM = new HashMap<String, List<String>>();
		groupFactors = new HashMap<String, Map<String, Transform>>();
	}

	public void addIni3DPoints(String group, ObservableFloatArray points) {
		points3DIni.put(group, MapTools.createAndConvertArray(points));
		points3DUpdater.put(group, points);
	}

	public void addGroupREM(String group, List<String> groupREM) {
		groupsREM.put(group, groupREM);
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
	public void applyTranslation(BodyPart part, String ancestor, Point3D transformation) {
		for (String group : part.getSubParts()) {
			updatePointsTranslation(group, ancestor, part.getIgnore(), transformation);
		}
	}

	public void applyRotation(BodyPart part, String ancestor, Point3D pointCentre, char axe, double degres,
			Point3D translation) {
		for (String group : part.getSubParts()) {
			updatePointsRotation(group, ancestor, part.getIgnore(), pointCentre, axe, degres, translation);
		}
	}

	/**
	 * Permet de grossir dans toutes les dimensions une partie du visage
	 * 
	 * @param part
	 *            la partie du visage à grossir
	 * @param groupREM
	 *            les parties à ignorer
	 * @param factor
	 *            le facteur de grossissement
	 */
	public void applyGrossissement(BodyPart part, String ancestor, double factor) {
		for (String group : part.getSubParts()) {
			updatePointGrossissement(group, ancestor, part.getIgnore(), factor);
		}
	}

	public void applyStretch(BodyPart part, String ancestor, Point3D pointCentre, Point3D scale) {
		for (String group : part.getSubParts()) {
			updatePointStretch(group, ancestor, part.getIgnore(), pointCentre, scale);
		}
	}

	/**
	 * Trouve les différents points communs entre chaque groupe et les met dans
	 * la map pointsSupp.
	 */
	public void findSiblings() {
		for (int k = 0; k < (points3DIni.values().size() - 1); k++) {
			ObservableFloatArray G1Points = (ObservableFloatArray) points3DIni.values().toArray()[k];
			for (int l = k + 1; l < points3DIni.values().size(); l++) {
				ObservableFloatArray G2Points = (ObservableFloatArray) points3DIni.values().toArray()[l];

				if (!G1Points.equals(G2Points)) {
					for (int i = 0; i < G1Points.size() / 3; i++) {

						Point3D pointG1 = new Point3D(G1Points.get(3 * i), G1Points.get((3 * i) + 1),
								G1Points.get((3 * i) + 2));

						for (int j = 0; j < G2Points.size() / 3; j++) {

							Point3D pointG2 = new Point3D(G2Points.get(3 * j), G2Points.get((3 * j) + 1),
									G2Points.get((3 * j) + 2));

							if (pointG1.equals(pointG2)) {
								String t = findKeyFromValueMap(G1Points);
								String s = findKeyFromValueMap(G2Points);

								List<String> groups = new ArrayList<String>();
								groups.add(t);
								groups.add(s);
								ObservableFloatArray point = FXCollections.observableFloatArray();
								point.addAll((float) pointG2.getX(), (float) pointG2.getY(), (float) pointG2.getZ());
								pointsSupp.put(point, groups);

							}

						}

					}
				}

			}
		}

		Set<String> keys = groupsREM.keySet();
		for (String e : keys) {
			findIndexToDodge(e, findPointsGroupREM(groupsREM.get(e)));
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
	private void updatePointsTranslation(String groupADD, String ancestor, List<String> groupREM, Point3D factors) {
		if (!groupFactors.containsKey(groupADD)) {
			Map<String, Transform> map = new HashMap<String, Transform>();
			map.put(ancestor, new Translate(factors.getY(), factors.getZ(), factors.getX()));
			groupFactors.put(groupADD, map);
		} else {
			groupFactors.get(groupADD).put(ancestor, new Translate(factors.getY(), factors.getZ(), factors.getX()));
		}

		ObservableFloatArray points = points3DUpdater.get(groupADD);
		List<ObservableFloatArray> pointsGroupREM = findPointsGroupREM(groupREM);

		Set<Integer> dodge = pointsDodge.get(groupADD);

		updatePointCommun(groupADD, ancestor, pointsGroupREM);

		for (int i = 0; i < points.size() / 3; i++) {
			if ((dodge != null) && (!dodge.isEmpty())) {
				if (!dodge.contains(i)) {
					updateArrayWithFactors(points, groupADD, i);
				}
			} else {
				updateArrayWithFactors(points, groupADD, i);
			}
		}
	}

	/**
	 * Permet de faire tourner une partie du visage
	 * 
	 * @param pointCentre
	 *            Le centre de rotation
	 * @param axe
	 *            L'axe autour duquel on tourne
	 * @param degres
	 *            Le nombre de degrés qu'on veut tourner
	 * @return Un objet Rotate à appliquer sur les composantes voulues
	 */
	// TODO les points translation vs rotation de l'oreille sont pas alignés
	private void updatePointsRotation(String group, String ancestor, List<String> groupREM, Point3D pointCentre,
			char axe, double degres, Point3D translation) {
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
		if (!groupFactors.containsKey(group)) {
			Map<String, Transform> map = new HashMap<String, Transform>();
			map.put(ancestor, objet);
			map.put(ancestor + "Translate", new Translate(translation.getY(), translation.getZ(), translation.getX()));
			groupFactors.put(group, map);
		} else {
			groupFactors.get(group).put(ancestor, objet);
			groupFactors.get(group).put(ancestor + "Translate",
					new Translate(translation.getY(), translation.getZ(), translation.getX()));
		}
		ObservableFloatArray points = points3DUpdater.get(group);
		List<ObservableFloatArray> pointsGroupREM = findPointsGroupREM(groupREM);

		Set<Integer> dodge = pointsDodge.get(group);

		updatePointCommun(group, ancestor, pointsGroupREM);

		for (int i = 0; i < points.size() / 3; i++) {
			if ((dodge != null) && (!dodge.isEmpty())) {
				if (!dodge.contains(i)) {
					updateArrayWithFactors(points, group, i);
				}
			} else {
				updateArrayWithFactors(points, group, i);
			}
		}
	}

	private void updatePointGrossissement(String groupADD, String ancestor, List<String> groupREM, double factor) {
		ObservableFloatArray points = points3DIni.get(groupADD);
		Point3D center = VecteurUtilitaires.findPointMilieu(points);

		for (int i = 0; i < (points.size() / 3); i++) {

			Point3D vecteurDirecteur = VecteurUtilitaires.findVecteur(center,
					new Point3D(points.get((3 * i) + 2), points.get((3 * i)), points.get((3 * i) + 1)));

			Point3D vecteurPointFinal = vecteurDirecteur.multiply(factor);

			Point3D delta = vecteurPointFinal.subtract(vecteurDirecteur.getX(), vecteurDirecteur.getY(),
					vecteurDirecteur.getZ());

			if (!groupFactors.containsKey(groupADD)) {
				Map<String, Transform> map = new HashMap<String, Transform>();
				map.put(ancestor, new Translate(delta.getY(), delta.getZ(), delta.getX()));
				groupFactors.put(groupADD, map);
			} else {
				groupFactors.get(groupADD).put(ancestor, new Translate(delta.getY(), delta.getZ(), delta.getX()));
			}
			
			updateArrayWithFactors(points3DUpdater.get(groupADD), groupADD, i);
			updatePointCommun(groupADD, ancestor, findPointsGroupREM(groupREM));

		}
	}

	// TODO problème de stretch (deuxième bouche apparait)
	private void updatePointStretch(String group, String ancestor, List<String> groupREM, Point3D pointCentre,
			Point3D scale) {
		Scale objet = new Scale(scale.getX(), scale.getY(), scale.getZ(), pointCentre.getX(), pointCentre.getY(),
				pointCentre.getZ());
		if (!groupFactors.containsKey(group)) {
			Map<String, Transform> map = new HashMap<String, Transform>();
			map.put(ancestor, objet);
			groupFactors.put(group, map);
		} else {
			groupFactors.get(group).put(ancestor, objet);
		}
		ObservableFloatArray points = points3DUpdater.get(group);
		List<ObservableFloatArray> pointsGroupREM = findPointsGroupREM(groupREM);

		Set<Integer> dodge = pointsDodge.get(group);

		updatePointCommun(group, ancestor, pointsGroupREM);

		for (int i = 0; i < points.size() / 3; i++) {
			if ((dodge != null) && (!dodge.isEmpty())) {
				if (!dodge.contains(i)) {
					updateArrayWithFactors(points, group, i);
				}
			} else {
				updateArrayWithFactors(points, group, i);
			}
		}
	}

	private void updateArrayWithFactors(ObservableFloatArray pointsUpdater, String group, int index) {
		ObservableFloatArray po = points3DIni.get(group);
		Point3D trans = new Point3D(po.get(0 + (3 * index)), po.get(1 + (3 * index)), po.get(2 + (3 * index)));
		for (Transform factors : groupFactors.get(group).values()) {
			trans = factors.transform(trans);
		}
		pointsUpdater.set(2 + (3 * index), (float) (trans.getZ()));
		pointsUpdater.set(0 + (3 * index), (float) (trans.getX()));
		pointsUpdater.set(1 + (3 * index), (float) (trans.getY()));
	}

	/**
	 * Update les points communs au groupe que l'on bouge de la même façon, tout
	 * en évitant de bouger les points dodge. (findPointsGroupREM)
	 * 
	 * @param groupADD
	 *            - groupe que l'on bouge
	 * @param dodge
	 *            - points à ne pas bouger.
	 * @param factors
	 */
	private void updatePointCommun(String groupADD, String ancestor, List<ObservableFloatArray> dodge) {
		List<ObservableFloatArray> pointsCommun = findKeyFromValueMap(groupADD, dodge);
		for (ObservableFloatArray pointCommun : pointsCommun) {
			List<String> groupsCommun = pointsSupp.get(pointCommun);
			for (String g : groupsCommun) {
				List<Integer> index = MapTools.findIndexOfValues(points3DIni.get(g), pointCommun);
				if (!g.equals(groupADD)) {
					for (Integer i : index) {
						if (!ancestor.equals("")) {
							Point3D updated = new Point3D(pointCommun.get(0), pointCommun.get(1), pointCommun.get(2));
							for (Transform factors : groupFactors.get(groupADD).values()) {
								updated = factors.transform(updated);
							}
							points3DUpdater.get(g).set((3 * i) + 2, (float) (updated.getZ()));
							points3DUpdater.get(g).set((3 * i) + 0, (float) (updated.getX()));
							points3DUpdater.get(g).set((3 * i) + 1, (float) (updated.getY()));
						}
					}
				}
			}
		}
	}

	/**
	 * Trouve les points des groupes à ne pas bouger.
	 * 
	 * @param groupREM
	 *            - les groupes à ne pas bouger
	 * @return une liste des points à ne pas bouger
	 */
	private List<ObservableFloatArray> findPointsGroupREM(List<String> groupREM) {
		List<ObservableFloatArray> pointsGroupREM = new ArrayList<ObservableFloatArray>();
		if ((groupREM != null) && (!groupREM.isEmpty())) {
			for (String rEM : groupREM) {
				ObservableFloatArray g = points3DIni.get(rEM);
				for (int i = 0; i < g.size() / 3; i++) {
					ObservableFloatArray f = FXCollections.observableFloatArray();
					f.addAll(g.get(3 * i), g.get((3 * i) + 1), g.get((3 * i) + 2));
					pointsGroupREM.add(f);
				}
			}
		}
		return pointsGroupREM;
	}

	/**
	 * À l'aide des groupes que l'on veut pas bouger en bougeant notre groupADD,
	 * on trouve les index des points de notre groupADD, qu'il ne faut pas
	 * bouger et ainsi ne pas bouger les groupREM.
	 * 
	 * @param groupADD
	 *            - le groupe que l'on veut bouger
	 * @param groupREM
	 *            - les groupes que l'on ne veut pas bouger
	 */
	private void findIndexToDodge(String groupADD, List<ObservableFloatArray> pointsGroupREM) {
		Set<Integer> dodge = new HashSet<Integer>();
		ObservableFloatArray points = points3DIni.get(groupADD);
		if ((pointsGroupREM != null) && (!pointsGroupREM.isEmpty())) {
			for (int i = 0; i < points.size() / 3; i++) {
				for (int j = 0; j < pointsGroupREM.size(); j++) {
					for (int j2 = j + 1; j2 < pointsGroupREM.size() - 1; j2++) {
						if (pointsGroupREM.get(j).size() == 3 && pointsGroupREM.get(j2).size() == 3) {
							double distance = VecteurUtilitaires.findDistance(
									new Point3D(pointsGroupREM.get(j).get(2), pointsGroupREM.get(j).get(0),
											pointsGroupREM.get(j).get(1)),
									new Point3D(pointsGroupREM.get(j2).get(2), pointsGroupREM.get(j2).get(0),
											pointsGroupREM.get(j2).get(1)),
									new Point3D(points.get((3 * i) + 2), points.get((3 * i)), points.get((3 * i) + 1)));
							if (distance <= 0.001) {
								dodge.add(i);

							}
						} else {
							throw new ArithmeticException();
						}
					}
				}
			}
		}
		pointsDodge.put(groupADD, dodge);
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
	private List<ObservableFloatArray> findKeyFromValueMap(String group, List<ObservableFloatArray> pointsREM) {
		List<ObservableFloatArray> out = new ArrayList<ObservableFloatArray>();
		for (ObservableFloatArray e : pointsSupp.keySet()) {
			if (pointsSupp.get(e).contains(group)) {
				boolean notFound = true;
				if ((pointsREM != null) && (!pointsREM.isEmpty())) {
					for (int i = 0; i < pointsREM.size(); i++) {
						if (MapTools.findIfEquals(e, pointsREM.get(i))) {
							notFound = false;
						}
					}
					if (notFound) {
						out.add(e);
					}
				} else {
					out.add(e);
				}
			}
		}
		return out;
	}
}