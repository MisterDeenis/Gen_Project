package modele.phenotype;

import java.util.ArrayList;
import java.util.List;

import javafx.geometry.Point3D;
import modele.phenotype.data.EyeColor;
import modele.phenotype.data.HairColor;
import modele.phenotype.data.SkinColor;

/**
 * Classe représentant une face
 */
public class Face {

	/**
	 * Stocke les différentes parts de la Face. Utilisée dans findBodyPart()
	 */
	private List<BodyPart> parts = new ArrayList<BodyPart>();
	private Ear earL = null;
	private Ear earR = null;
	private Eye eyeL = null;
	private Eye eyeR = null;
	private Hair hair = null;
	private Mouth mouth = null;
	private BodyPart nose = null;
	private BodyPart front = null;
	private BodyPart joueL = null;
	private BodyPart joueR = null;
	private BodyPart menton = null;
	private BodyPart arche = null;
	private BodyPart bosseL = null;
	private BodyPart bosseR = null;
	private BodyPart pointe = null;
	private BodyPart cou = null;
	private Sourcils sourcilsL = null;
	private Sourcils sourcilsR = null;
	private SkinColor skinColor = null;
	private TransformationPoints pointsVisage = null;

	public Face(EyeColor eyeColor, SkinColor skinColor, HairColor hairColor) {
		instantiateOreilles();
		instantiateNez();
		instantiateJoue();
		instantiateUpperFace(eyeColor);
		instantiateSourcils(hairColor);
		instantiateRestBody(hairColor);
		this.skinColor = skinColor;
		pointsVisage = new TransformationPoints();
		addToArray();
	}

	/**
	 * Trouve s'il existe une BodyPart correspondante à une string d'entrée
	 * 
	 * @param part
	 *            - le string à rechercher
	 * @return le BodyPart correspondant à la string
	 */
	public BodyPart findBodyPart(String part) {
		BodyPart out = null;
		for (BodyPart e : parts) {
			for (String f : e.getSubParts()) {
				if (f.equals(part)) {
					out = e;
				}
			}
		}
		return out;

	}

	public void setEyeDistance(float distance) {
		pointsVisage.applyRotation(eyeL, "eyeDistance", new Point3D(0, 5, 0), 'x', -eyeL.getRotation(),
				new Point3D(-distance, 0, 0));
		pointsVisage.applyRotation(eyeR, "eyeDistance", new Point3D(0, 5, 0), 'x', eyeR.getRotation(),
				new Point3D(distance, 0, 0));
	}

	public void setProfondeurOreilles(float profondeur) {
		pointsVisage.applyTranslation(earL, "profOreille", new Point3D(0, 0, -profondeur));
		pointsVisage.applyTranslation(earR, "profOreille", new Point3D(0, 0, -profondeur));
	}

	public void setHauteurOreilles(float hauteur) {
		pointsVisage.applyTranslation(earL, "hauteurOreille", new Point3D(0, hauteur, 0));
		pointsVisage.applyTranslation(earR, "hauteurOreille", new Point3D(0, hauteur, 0));
	}

	public void setPositionBouche(float hauteur) {
		pointsVisage.applyTranslation(mouth, "posBouche", new Point3D(0, hauteur, 0));
	}

	public void setGrosseurBouche(double grosseur) {
		mouth.setScale(grosseur);
		pointsVisage.applyStretch(mouth, "grosseurBouche", new Point3D(0, 0, 0), new Point3D(1, 1, mouth.getScale()));
	}

	public void setRotationOreille(double rotation) {
		earL.setRotation(rotation);
		earR.setRotation(rotation);
		pointsVisage.applyRotation(earL, "rotateOreille", new Point3D(0, (-2.5 - earL.getProfondeur()), -9.59), 'x',
				-earL.getRotation(), new Point3D(0, 0, 0));
		pointsVisage.applyRotation(earR, "rotateOreille", new Point3D(0, (-2.5 - earR.getProfondeur()), 9.59), 'x',
				earR.getRotation(), new Point3D(0, 0, 0));
	}

	public void setPositionNez(float hauteur) {
		pointsVisage.applyTranslation(nose, "posNez", new Point3D(0, hauteur, 0));
	}

	public void setPositionSourcils(float ecart) {
		pointsVisage.applyTranslation(sourcilsL, "posSourcils", new Point3D(-ecart, 0, -ecart / 2));
		pointsVisage.applyTranslation(sourcilsR, "posSourcils", new Point3D(ecart, 0, -ecart / 2));
	}

	public void setGrosseurJoues(float ecart) {
		pointsVisage.applyTranslation(joueL, "grosseurJoue", new Point3D(-ecart, 0, ecart / 3));
		pointsVisage.applyTranslation(joueR, "grosseurJoue", new Point3D(ecart, 0, ecart / 3));
	}

	public void setPositionArche(float ecart) {
		pointsVisage.applyTranslation(arche, "posArche", new Point3D(0, ecart / 2, ecart / 2));
	}

	public void setEcartNarine(float ecart) {
		pointsVisage.applyTranslation(bosseL, "ecartNarine", new Point3D(-ecart, 0, 0));
		pointsVisage.applyTranslation(bosseR, "ecartNarine", new Point3D(ecart, 0, 0));
	}

	public void setLongueurPointe(float ecart) {
		pointsVisage.applyTranslation(pointe, "longueurPointe", new Point3D(0, 0, ecart));
	}

	public void setPositionFront(float ecart) {
		pointsVisage.applyTranslation(front, "front", new Point3D(0, 0, ecart));
		pointsVisage.applyTranslation(sourcilsL, "front", new Point3D(0, 0, ecart / 2));
		pointsVisage.applyTranslation(sourcilsR, "front", new Point3D(0, 0, ecart / 2));
		pointsVisage.applyTranslation(eyeL, "front", new Point3D(0, 0, ecart / 2));
		pointsVisage.applyTranslation(eyeR, "front", new Point3D(0, 0, ecart / 2));
		pointsVisage.applyTranslation(joueL, "front", new Point3D(0, 0, ecart / 3));
		pointsVisage.applyTranslation(joueR, "front", new Point3D(0, 0, ecart / 3));
	}

	public void setGrosseurCou(float grosseur) {
		pointsVisage.applyGrossissement(cou, "cou", grosseur);
	}

	public void setLongueurFace(float distance) {
		pointsVisage.applyTranslation(front, "longueurFace", new Point3D(0, distance, 0));
		pointsVisage.applyTranslation(hair, "longueurFace", new Point3D(0, distance, 0));
		pointsVisage.applyTranslation(menton, "longueurFace", new Point3D(0, -distance, 0));

	}

	public void setProeminenceSourcils(float distance) {
		pointsVisage.applyTranslation(sourcilsL, "proeminSourcils", new Point3D(0, 0, distance));
		pointsVisage.applyTranslation(sourcilsR, "proeminSourcils", new Point3D(0, 0, distance));
	}

	public void setProeminenceMenton(float distance) {
		pointsVisage.applyTranslation(menton, "proeminMenton", new Point3D(0, -(distance * 0.25), distance));
		pointsVisage.applyTranslation(mouth, "proeminMenton", new Point3D(0, -(distance * 0.25), distance));
	}

	public Ear getEarL() {
		return earL;
	}

	public void setEarL(Ear earL) {
		this.earL = earL;
	}

	public Ear getEarR() {
		return earR;
	}

	public void setEarR(Ear earR) {
		this.earR = earR;
	}

	public Eye getEyeL() {
		return eyeL;
	}

	public void setEyeL(Eye eyeL) {
		this.eyeL = eyeL;
	}

	public Eye getEyeR() {
		return eyeR;
	}

	public void setEyeR(Eye eyeR) {
		this.eyeR = eyeR;
	}

	public BodyPart getFront() {
		return front;
	}

	public void setFront(BodyPart front) {
		this.front = front;
	}

	public BodyPart getJoueL() {
		return joueL;
	}

	public void setJoueL(BodyPart joueL) {
		this.joueL = joueL;
	}

	public BodyPart getJoueR() {
		return joueR;
	}

	public void setJoueR(BodyPart joueR) {
		this.joueR = joueR;
	}

	public BodyPart getMenton() {
		return menton;
	}

	public void setMenton(BodyPart menton) {
		this.menton = menton;
	}

	public BodyPart getArche() {
		return arche;
	}

	public void setArche(BodyPart arche) {
		this.arche = arche;
	}

	public BodyPart getBosseL() {
		return bosseL;
	}

	public void setBosseL(BodyPart bosseL) {
		this.bosseL = bosseL;
	}

	public BodyPart getBosseR() {
		return bosseR;
	}

	public void setBosseR(BodyPart bosseR) {
		this.bosseR = bosseR;
	}

	public BodyPart getPointe() {
		return pointe;
	}

	public void setPointe(BodyPart pointe) {
		this.pointe = pointe;
	}

	public BodyPart getCou() {
		return cou;
	}

	public void setCou(BodyPart cou) {
		this.cou = cou;
	}

	public Sourcils getSourcilsL() {
		return sourcilsL;
	}

	public void setSourcilsL(Sourcils sourcilsL) {
		this.sourcilsL = sourcilsL;
	}

	public Sourcils getSourcilsR() {
		return sourcilsR;
	}

	public void setSourcilsR(Sourcils sourcilsR) {
		this.sourcilsR = sourcilsR;
	}

	public Hair getHair() {
		return hair;
	}

	public void setHair(Hair hair) {
		this.hair = hair;
	}

	public Mouth getMouth() {
		return mouth;
	}

	public void setMouth(Mouth mouth) {
		this.mouth = mouth;
	}

	public BodyPart getNose() {
		return nose;
	}

	public void setNose(BodyPart nose) {
		this.nose = nose;
	}

	public void setPointsVisage(TransformationPoints pointsVisage) {
		this.pointsVisage = pointsVisage;
	}

	public SkinColor getSkinColor() {
		return skinColor;
	}

	public void setSkinColor(SkinColor skinColor) {
		this.skinColor = skinColor;
	}

	public TransformationPoints getPointsVisage() {
		return pointsVisage;
	}

	private void instantiateOreilles() {
		earL = new Ear("Oreille gauche");
		earR = new Ear("Oreille droite");
	}

	private void instantiateNez() {
		ArrayList<String> groupREM = new ArrayList<String>();
		arche = new BodyPart(groupREM, "Arche");
		bosseL = new BodyPart(groupREM, "Bosse gauche");
		bosseR = new BodyPart(groupREM, "Bosse droite");
		pointe = new BodyPart(groupREM, "Pointe");
		nose = new BodyPart(groupREM, arche, bosseL, bosseR, pointe);
		nose.getSubParts().add("Nez");
		nose.getSubParts().add("Narine");
		nose.getSubParts().add("Bord narine");
		nose.setIgnore("Oeil droit", "Oeil gauche", "Blanc oeil droit", "Blanc oeil gauche", "Sourcil droit",
				"Sourcil gauche");
	}

	private void instantiateJoue() {
		ArrayList<String> groupREM = new ArrayList<String>();
		groupREM.add("Cheveux");
		groupREM.add("Nez");
		groupREM.add("Bouche");
		joueL = new BodyPart(groupREM, "Joue gauche");
		joueR = new BodyPart(groupREM, "Joue droite");
	}

	private void instantiateUpperFace(EyeColor eyeColor) {
		ArrayList<String> groupREM = new ArrayList<String>();
		groupREM.add("Cheveux");
		earL = new Ear(groupREM, "Oreille gauche");
		earR = new Ear(groupREM, "Oreille droite");
		front = new BodyPart(groupREM, "Front");
		eyeL = new Eye(eyeColor, groupREM, "Blanc oeil gauche", "Noir oeil gauche", "Couleur oeil gauche");
		eyeR = new Eye(eyeColor, groupREM, "Blanc oeil droit", "Noir oeil droit", "Couleur oeil droit");
		cou = new BodyPart(groupREM, "Cou");
	}

	private void instantiateSourcils(HairColor hairColor) {
		ArrayList<String> groupREM = new ArrayList<String>();
		groupREM.add("Cheveux");
		groupREM.add("Oeil droit");
		groupREM.add("Oeil gauche");
		sourcilsL = new Sourcils(hairColor, groupREM, "Sourcil gauche");
		sourcilsR = new Sourcils(hairColor, groupREM, "Sourcil droit");
	}

	private void instantiateRestBody(HairColor hairColor) {
		ArrayList<String> groupREM = new ArrayList<String>();
		hair = new Hair(hairColor, groupREM, "Cheveux");
		mouth = new Mouth(groupREM, "Bouche");
		menton = new BodyPart(groupREM, "Menton");
	}

	/**
	 * Pour permettre la recherche avec findBodyPart()
	 */
	private void addToArray() {
		parts.add(earL);
		parts.add(earR);
		parts.add(eyeL);
		parts.add(eyeR);
		parts.add(hair);
		parts.add(mouth);
		parts.add(nose);
		parts.add(front);
		parts.add(joueL);
		parts.add(joueR);
		parts.add(menton);
		parts.add(arche);
		parts.add(bosseL);
		parts.add(bosseR);
		parts.add(pointe);
		parts.add(sourcilsL);
		parts.add(sourcilsR);
		parts.add(cou);
	}

}
