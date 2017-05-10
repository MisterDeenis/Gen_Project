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
	/**
	 * Une map avec comme clé les différents group et comme value les différents
	 * index des points à ne pas bouger en se basant sur les groupREM. Utilisée
	 * lorsque qu'on bouge les points d'un groupe pour éviter de déformer le
	 * visage. (Les points du groupe à bouger)
	 */
	private Map<String, Set<Integer>> pointsDodge = null;
	/**
	 * Une map avec comme clé les différents group et comme value la liste des
	 * groupes communs à ne pas bouger. Utilisée lorsqu'on déplace les groupes
	 * pour éviter de déformer le visage. (Les points des groupes communs au
	 * groupe à bouger)
	 */
	private Map<String, List<String>> groupsREM = null;
	/**
	 * Une map avec comme clé les différents group et comme value une map, avec
	 * comme clé les ancêtres (qui sont une étiquette des modifications à
	 * effectuer comme rotationOreille ou hauteurOreille) et comme value l'objet
	 * Transform de la transformation que l'on effectue sur notre group.
	 * Utilisée pour stocker les différentes Transform sur un group et pouvoir
	 * ensuite le remplacer par une autre Transform si on change sa valeur (on
	 * change la hauteur de l'oreille, on va donc changer son objet Translation
	 * pour changer sa hauteur).
	 */
	private Map<String, Map<String, Transform>> groupTransform = null;

	public TransformationPoints() {
		points3DIni = new HashMap<String, ObservableFloatArray>();
		points3DUpdater = new HashMap<String, ObservableFloatArray>();
		pointsSupp = new HashMap<ObservableFloatArray, List<String>>();
		pointsDodge = new HashMap<String, Set<Integer>>();
		groupsREM = new HashMap<String, List<String>>();
		groupTransform = new HashMap<String, Map<String, Transform>>();
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
	 * Permet d'appliquer une translation sur une partie du visage
	 * 
	 * @param part
	 *            la partie du visage
	 * @param transformation
	 *            les paramètres de la transformation (x, y ,z)
	 */
	public void applyTranslation(BodyPart part, String ancestor, Point3D transformation) {
		for (String group : part.getSubParts()) {
			ajoutGroupFactors(group, ancestor,
					new Translate(transformation.getY(), transformation.getZ(), transformation.getX()));
			updatePoints(group, ancestor, part.getIgnore());
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
	public void applyRotation(BodyPart part, String ancestor, Point3D pointCentre, char axe, double degres,
			Point3D translation) {
		for (String group : part.getSubParts()) {
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

			ajoutGroupFactors(group, ancestor, objet);
			ajoutGroupFactors(group, ancestor + "Translate",
					new Translate(translation.getY(), translation.getZ(), translation.getX()));
			updatePoints(group, ancestor, part.getIgnore());
		}
	}

	/**
	 * Permet de grossir dans toutes les dimensions une partie du visage à
	 * partir d'un point central
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
			ObservableFloatArray points = points3DIni.get(group);
			Point3D center = VecteurUtilitaires.findPointMilieu(points);

			for (int i = 0; i < (points.size() / 3); i++) {

				Point3D vecteurDirecteur = VecteurUtilitaires.findVecteur(center,
						new Point3D(points.get((3 * i) + 2), points.get((3 * i)), points.get((3 * i) + 1)));

				Point3D vecteurPointFinal = vecteurDirecteur.multiply(factor);

				Point3D delta = vecteurPointFinal.subtract(vecteurDirecteur.getX(), vecteurDirecteur.getY(),
						vecteurDirecteur.getZ());

				ajoutGroupFactors(group, ancestor, new Translate(delta.getY(), delta.getZ(), delta.getX()));
				updateArrayWithFactors(group, i);
				updatePointCommun(group, ancestor, findPointsGroupREM(part.getIgnore()));
			}
		}
	}

	/**
	 * Permet d'étirer un groupe à partir d'un axe central
	 * 
	 * @param part
	 *            - groupe à étirer
	 * @param ancestor
	 *            - la modification à effectuer
	 * @param pointCentre
	 *            - l'axe central
	 * @param scale
	 *            - l'étirement à effectuer
	 */
	public void applyStretch(BodyPart part, String ancestor, Point3D pointCentre, Point3D scale) {
		for (String group : part.getSubParts()) {
			Scale objet = new Scale(scale.getX(), scale.getY(), scale.getZ(), pointCentre.getX(), pointCentre.getY(),
					pointCentre.getZ());
			ajoutGroupFactors(group, ancestor, objet);
			updatePoints(group, ancestor, part.getIgnore());
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
	 * Ajout de la transformation du groupe
	 * 
	 * @param groupADD
	 *            - groupe à modifier
	 * @param ancestors
	 *            - la modification à effectuer
	 * @param trans
	 *            - la modification à effectuer
	 */
	private void ajoutGroupFactors(String groupADD, String ancestors, Transform trans) {
		if (!groupTransform.containsKey(groupADD)) {
			Map<String, Transform> map = new HashMap<String, Transform>();
			map.put(ancestors, trans);
			groupTransform.put(groupADD, map);
		} else {
			groupTransform.get(groupADD).put(ancestors, trans);
		}
	}

	/**
	 * Update tous les points nécessaire à bouger
	 * 
	 * @param groupADD
	 *            - groupe que l'on bouge
	 * @param ancestor
	 *            - le type de modification à apporter
	 * @param groupREM
	 *            - les groupes communs à ne pas bouger
	 */
	private void updatePoints(String groupADD, String ancestor, List<String> groupREM) {
		ObservableFloatArray points = points3DUpdater.get(groupADD);
		List<ObservableFloatArray> pointsGroupREM = findPointsGroupREM(groupREM);

		Set<Integer> dodge = pointsDodge.get(groupADD);

		updatePointCommun(groupADD, ancestor, pointsGroupREM);

		for (int i = 0; i < points.size() / 3; i++) {
			if ((dodge != null) && (!dodge.isEmpty())) {
				if (!dodge.contains(i)) {
					updateArrayWithFactors(groupADD, i);
				}
			} else {
				updateArrayWithFactors(groupADD, i);
			}
		}
	}

	/**
	 * Update le point à un certain index de notre group à l'aide de
	 * groupTransform
	 * 
	 * @param group
	 *            - groupe que l'on bouge
	 * @param index
	 *            - index de notre point
	 */
	private void updateArrayWithFactors(String group, int index) {
		ObservableFloatArray pointsIni = points3DIni.get(group);
		Point3D trans = new Point3D(pointsIni.get(0 + (3 * index)), pointsIni.get(1 + (3 * index)),
				pointsIni.get(2 + (3 * index)));

		for (Transform factors : groupTransform.get(group).values()) {
			trans = factors.transform(trans);
		}

		ObservableFloatArray pointsUpdater = points3DUpdater.get(group);
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
	 * @param ancestor
	 *            - le type de modification à apporter
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

							for (Transform factors : groupTransform.get(groupADD).values()) {
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
	 * @param pointsREM
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