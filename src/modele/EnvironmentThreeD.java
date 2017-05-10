package modele;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import utils.ToolsThreeD;
import utils.importerLib.importers.obj.ObjImporter;
import vue.MessageAlert;
import javafx.collections.ObservableFloatArray;
import javafx.event.EventHandler;
import javafx.scene.PerspectiveCamera;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Rotate;
import modele.phenotype.BodyPart;
import modele.phenotype.Face;
import modele.phenotype.data.EyeColor;
import modele.phenotype.data.HairColor;
import modele.phenotype.data.SkinColor;

/*
 * Classe créant une scène movable 3D avec un OBJ dedans
 */
public class EnvironmentThreeD {

	private static final double CAMERA_INITIAL_DISTANCE = -100, CAMERA_INITIAL_X_ANGLE = 70.0,
			CAMERA_INITIAL_Y_ANGLE = 320.0;
	private static final double CAMERA_NEAR_CLIP = 0.1, CAMERA_FAR_CLIP = 10000.0;
	private static final double CONTROL_MULTIPLIER = 0.1, SHIFT_MULTIPLIER = 10.0;
	private static final double MOUSE_SPEED = 0.1, MOUSE_WHEEL_SPEED = 0.15, ROTATION_SPEED = 1.0, TRACK_SPEED = 0.3;
	private static final String URL = "/obj/face1.obj";

	/**
	 * World étant notre Group supérieur, contenant les caméras et l'OBJ
	 */
	private final ToolsThreeD world = new ToolsThreeD();

	/**
	 * Caméra et ses 3 dimensions de vue permettant de regarder dans l'espace
	 */
	private final PerspectiveCamera camera = new PerspectiveCamera(true);
	private final ToolsThreeD cameraX = new ToolsThreeD(), cameraY = new ToolsThreeD(), cameraZ = new ToolsThreeD();

	/**
	 * Variables pour le MouseEvent concernant les positions de la souris
	 */
	private double mousePosX, mousePosY, mouseOldX, mouseOldY, mouseDeltaX, mouseDeltaY, modifier = 1.0;

	/**
	 * Le reader qui permet de lire le fichier OBJ et qui nous permet d'avoir
	 * ses valeurs
	 */
	private ObjImporter reader = null;

	/**
	 * Group contenant notre OBJ 3D
	 */
	private ToolsThreeD objGroup;

	/**
	 * Notre visage (OBJ) contenant les points et les attributs que l'on doit
	 * modifier (couleur des yeux, hauteur des oreilles, etc.)
	 */
	private Face face = null;

	public EnvironmentThreeD(EyeColor initEyeColor, SkinColor initSkinColor, HairColor initHairColor) {
		face = new Face(initEyeColor, initSkinColor, initHairColor);
	}

	/**
	 * Construit le monde 3D de départ et retourne ce monde pour l'inclure dans
	 * un pane
	 * 
	 * @param root
	 *            - owner du monde 3D pour permettre d'avoir des événements
	 *            dessus
	 * @param width
	 *            - largeur du pane root
	 * @param height
	 *            - hauteur du pane root
	 * @return la scène contenant le monde 3D
	 */
	public SubScene buildWorld(Pane root, int width, int height) {
		SubScene scene = new SubScene(world, width, height - 10, true, SceneAntialiasing.BALANCED);
		objGroup = new ToolsThreeD();
		handleControls(root);
		scene.setCamera(camera);
		buildImporter();
		buildCamera();
		buildObj(true);
		return scene;

	}

	/**
	 * Permettant d'updater le groupe contenant l'objet s'il y a un changement
	 * dessus
	 */
	public void changementWorld() {
		world.getChildren().remove(objGroup);
		objGroup.getChildren().clear();
		buildObj(false);
	}

	public Face getFace() {
		return this.face;
	}

	private void buildImporter() {
		try {
			reader = new ObjImporter(getClass().getResource(URL).toExternalForm());
		} catch (IOException e) {
			new MessageAlert("Error loading model " + e.toString());
		}
	}

	/**
	 * Méthode permettant d'importer les .obj et de les mettre dans notre scène
	 * world
	 */
	private void buildObj(boolean firstBuild) {

		Set<String> physionomyGroups = reader.getMeshes();
		/**
		 * groupMeshes contient un MeshView, qui contient les textures et les
		 * normales du fichier .OBJ pour nous permettre de le mettre dans un
		 * NODE
		 */
		Map<String, MeshView> groupMeshes = new HashMap<>();

		/**
		 * On rotate notre MeshView pour qu'il soit droit dans notre vue
		 */
		final Affine affineIni = new Affine();
		affineIni.prepend(new Rotate(-90, Rotate.X_AXIS));
		affineIni.prepend(new Rotate(90, Rotate.Z_AXIS));
		physionomyGroups.stream().forEach(s -> {

			MeshView genomicPart = reader.buildMeshView(s);
			genomicPart.getTransforms().add(affineIni);

			ObservableFloatArray points3DGroup = ((TriangleMesh) genomicPart.getMesh()).getPoints();

			// Ce groupe de code sert à effacer les bouts noirs sur le fichier
			// (erreur de manipulation dans Blender)
			if (s.contains("Bouche")) {
				genomicPart.setCullFace(CullFace.BACK);
			}

			if (s.contains("oeil droit")) {
				genomicPart.setCullFace(CullFace.BACK);
			}

			// Si c'est la première fois qu'on construit l'objet, on ajoute dans
			// les points du visage, les points initiaux (et les groupREM à ne
			// pas bouger lorsque l'on déplace d'autres groupes) pour nous
			// permettre de
			// garder les dimensions correctes pour tard lorsqu'on les
			// modifiera.
			if (firstBuild) {
				face.getPointsVisage().addIni3DPoints(s, points3DGroup);
				BodyPart e = face.findBodyPart(s);
				if (e != null)
					face.getPointsVisage().addGroupREM(s, e.getIgnore());
			}

			// On update les points du visage avec ceux qui ont été modifié lors
			// de la modification d'une valeur d'un slider
			points3DGroup = face.getPointsVisage().getPointsUpdater(s);
			genomicPart.setMaterial(updateColor(s));
			groupMeshes.put(s, genomicPart);
		});

		// Si c'est la première fois qu'on construit l'objet, on trouve les
		// points communs entre chaque groupes.
		if (firstBuild) {
			face.getPointsVisage().findSiblings();
		}
		objGroup.getChildren().addAll(groupMeshes.values());
		world.getChildren().add(objGroup);

	}

	/**
	 * Méthode permettant de mettre de la couleur sur notre objet
	 * 
	 * @param group
	 *            - le groupe a colorier
	 * @return le matériel à appliquer sur le groupe (avec la bonne couleur)
	 */
	private PhongMaterial updateColor(String group) {
		final PhongMaterial material = new PhongMaterial();
		if (group.contains("Couleur oeil")) {
			material.setDiffuseColor(getFace().getEyeL().getCouleurYeux().getColor());
		} else if (group.contains("Blanc oeil")) {
			material.setDiffuseColor(Color.WHITE);
		} else if (group.contains("Noir oeil")) {
			material.setDiffuseColor(Color.BLACK);
		} else if (group.contains("Cheveux")) {
			material.setDiffuseColor(getFace().getHair().getCouleurCheveux().getColor());
		} else if (group.contains("Sourcil droit") || group.contains("Sourcil gauche")) {
			material.setDiffuseColor(getFace().getSourcilsL().getColor().getColor());
		} else {
			material.setDiffuseColor(getFace().getSkinColor().getColor());
		}

		material.setSpecularColor(Color.GRAY);
		material.setSpecularPower(150);
		return material;
	}

	/**
	 * Construit la caméra pour qu'on puisse voir notre objet 3D sous toutes ses
	 * angles
	 */
	private void buildCamera() {
		world.getChildren().add(cameraX);
		cameraX.getChildren().add(cameraY);
		cameraY.getChildren().add(cameraZ);
		cameraZ.getChildren().add(camera);
		cameraZ.setRotateZ(180.0);

		camera.setNearClip(CAMERA_NEAR_CLIP);
		camera.setFarClip(CAMERA_FAR_CLIP);
		camera.setTranslateZ(CAMERA_INITIAL_DISTANCE);
		cameraX.ry.setAngle(CAMERA_INITIAL_Y_ANGLE);
		cameraX.rx.setAngle(CAMERA_INITIAL_X_ANGLE);
	}

	/**
	 * Méthode permettant de manipuler l'objet avec la caméra
	 * 
	 * @param pane
	 *            - le owner de la Scene de BuildWorld(). (C'est moins buggé
	 *            d'utiliser une pane qu'un scene)
	 */
	private void handleControls(Pane pane) {

		pane.setOnMousePressed(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent me) {
				mousePosX = me.getSceneX();
				mousePosY = me.getSceneY();
				mouseOldX = me.getSceneX();
				mouseOldY = me.getSceneY();
			}

		});
		pane.setOnMouseDragged(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent me) {
				mouseOldX = mousePosX;
				mouseOldY = mousePosY;
				mousePosX = me.getSceneX();
				mousePosY = me.getSceneY();
				mouseDeltaX = (mousePosX - mouseOldX);
				mouseDeltaY = (mousePosY - mouseOldY);
				modifier = 1.0;

				if (me.isControlDown()) {
					modifier = CONTROL_MULTIPLIER;
				}
				if (me.isShiftDown()) {
					modifier = SHIFT_MULTIPLIER;
				}
				if (me.isPrimaryButtonDown()) {
					cameraX.ry.setAngle(cameraX.ry.getAngle() - mouseDeltaX * modifier * ROTATION_SPEED);
					cameraX.rx.setAngle(cameraX.rx.getAngle() - mouseDeltaY * modifier * ROTATION_SPEED);
				} else if (me.isSecondaryButtonDown()) {
					cameraY.t.setX(cameraY.t.getX() + mouseDeltaX * MOUSE_SPEED * modifier * TRACK_SPEED);
					cameraY.t.setY(cameraY.t.getY() + mouseDeltaY * MOUSE_SPEED * modifier * TRACK_SPEED);
				}
			}
		});

		pane.setOnScroll(new EventHandler<ScrollEvent>() {
			public void handle(ScrollEvent me) {
				double z = camera.getTranslateZ();
				double newZ = z + me.getDeltaY() * MOUSE_WHEEL_SPEED * modifier;
				camera.setTranslateZ(newZ);
			}
		});
	}
}
