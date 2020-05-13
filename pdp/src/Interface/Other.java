package Interface;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.jgrapht.graph.SimpleWeightedGraph;

import de.fhpotsdam.unfolding.examples.animation.Edge;
import de.fhpotsdam.unfolding.examples.animation.FadeTwoMapsApp;
import de.fhpotsdam.unfolding.examples.animation.InfoStation;
import de.fhpotsdam.unfolding.examples.animation.PositionInCity;
import de.fhpotsdam.unfolding.geo.Location;
import domain.ArcTrajet;
import domain.ChercherChemin;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import transport.Coordonnees;

// CLASSE INTERFACE
public class Other extends Application {

	/////////////////////////////////////////////////// PARAMETERS ///////////////////////////////////////////////////

	// width and height of the canvas 
	private final static double WIDTH = 600;
	private final static double HEIGHT = 600;
	// zone of canvas used without menu bar
	private final static double bestWidth = WIDTH*0.9375;
	private final static double bestHeight = HEIGHT*0.9375;
	// middle zone
	private final static double bW2 = bestWidth/2;
	private final static double bH2 = bestHeight/2;

	// check what the user does in the application 
	private String action = "first";
	// if entered in case route
	private int wasInSchedule = 0;
	// draw one time in root
	private int countButtonOk = 0;

	// position start when search a route
	public static String start;
	// position end when search a route
	public static String end;
	// date and route when search a route
	public String date;
	// objective of the solver (0 : fast, 1 : less walking, 2 : less transports, 3 : less waiting time)
	public static int objectif = 0;
	// start location
	private Location startLocation = new Location(0,0);
	// end location
	private Location endLocation = new Location(0,0);

	// tab of station (to load or save)
	public static List<ArcTrajet> listStations;
	// tab of station's name
	public static List<String> listNameStations;
	// tab of station's numero
	public static List<String> listNumeroStations;
	// tab of station's coordinates
	public static List<Location> listCoordStations;
	// tab of route
	public static List<String> listHoraire;
	// list of lines banned by user
	private List<String> listBanned = new ArrayList<String>();
	// all locations possible
	private List<PositionInCity> allLocations = new ArrayList<PositionInCity>();
	// info stations
	private List<String> infoStations = new ArrayList<String>();

	// line chosen in perturbation or schedule
	private String line;

	// zoom or not to check schedule
	private int zoom = 0;

	// path to access to the images
	public static String getRessourcePathByName(String name) {
		return Other.class.getResource("/images/" + name).toString();
	}
	public static String getHorariesPathByName(String name) {
		return Other.class.getResource("/imgHoraries/" + name).toString();
	}
	public static String getLogoPathByName(String name) {
		return Other.class.getResource("/imgLogo/" + name + ".png").toString();
	}

	// all images used
	private Image imgFond = new Image(getRessourcePathByName("fond.jpg"), WIDTH, HEIGHT, false, false);

	private Image imgSchedule = new Image(getRessourcePathByName("schedule.png"), bW2, bH2, false, false);
	private Image imgRoute = new Image(getRessourcePathByName("route.png"), bW2, bH2, false, false);
	private Image imgPerturbation = new Image(getRessourcePathByName("perturbation.png"), bW2, bH2, false, false);
	private Image imgAboutUs = new Image(getRessourcePathByName("aboutUs.png"), bW2, bH2, false, false);
	private Image imgLeave = new Image(getRessourcePathByName("leave.jpg"), bestWidth/15, bestHeight/15, false, false);
	private Image imgLoad = new Image(getRessourcePathByName("load.png"), bestWidth/15, bestHeight/15, false, false);
	private Image imgSave = new Image(getRessourcePathByName("save.png"), bestWidth/15, bestHeight/15, false, false);
	private Image imgPrevious = new Image(getRessourcePathByName("previous.png"), bestWidth/15, bestHeight/15, false, false);
	private Image imgDetail = new Image(getRessourcePathByName("detail.jpg"), bestWidth/15, bestHeight/15, false, false);

	private Image imgBus = new Image(getRessourcePathByName("bus.png"), bestWidth/12, bestHeight/12, false, false);
	private Image imgTram = new Image(getRessourcePathByName("tram.png"), bestWidth/12, bestHeight/12, false, false);
	private Image imgMarche = new Image(getRessourcePathByName("marche.png"), bestWidth/12, bestHeight/12, false, false);

	// list of images corresponding (schedule0, route1, perturbation2, aboutUs3, other4, carte5, save6, load7, previous8, leave9)
	private List<Image> imgListTransport = new ArrayList<Image>();

	// list of circles 
	private static List<Circle> circles;
	// res of best path
	public static int tempsFinal;
	// number of circles
	private final static int nbrCercle = 10;
	// circles of perturbations set
	private List<Circle> circlesPerturb;

	// point clicked
	private Point pointClick;

	// number of offset to zoom
	private int nbrDecalX;
	private int nbrDecalY;

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////	

	/////////////////////////////////////////////////// CLICK AREA ///////////////////////////////////////////////////	
	// no circles in canvas
	public void moveCirclesToNull(int a) {
		for(int i = 0; i<nbrCercle-a; i++) {
			circles.get(i).putTo(-500, -500);
		}
	}

	// move circle leave
	public void moveCircleExit() {
		circles.get(9).putTo(bestWidth+bestWidth/30, bestHeight+bestHeight/30);
	}

	// move circles to its good position
	public void moveCirclesTo(int a, int b) {
		for(int i = 0; i<nbrCercle-2; i++) {
			circles.get(i).putTo(-500, -500);
		}
		circles.get(a).putTo(bestWidth - 2*bestWidth/15, bestHeight + bestWidth/30);
		if(b != -1) {
			circles.get(b).putTo(bestWidth - 3*bestWidth/15, bestHeight + bestWidth/30);
		}
	}

	// move break to its position
	public void moveCircleTo(int a) {
		if(a == 8) {
			circles.get(a).putTo(bestWidth - bestWidth/15, bestHeight + bestWidth/30);
		}
		else {
			circles.get(a).putTo(bestWidth - 2*bestWidth/15, bestHeight + bestWidth/30);
		}
	}

	// move circles to the menu position and exclude the others
	public void moveToMenu() {
		circles.get(0).putTo(bW2/2, bH2/2);
		circles.get(1).putTo(bW2+bW2/2,bH2/2);
		circles.get(2).putTo(bW2/2,bH2+bH2/2);
		circles.get(3).putTo(bW2+bW2/2,bH2+bH2/2);
		circles.get(9).putTo(bestWidth + bestWidth/30, bestHeight + bestWidth/30);
		for(int i = 4; i<9; i++) {
			circles.get(i).putTo(-500, -500);
		}
	}


	// recall : circles = (schedule0, route1, perturbation2, aboutUs3, other4, carte5, save6, load7, previous8, leave9)
	// move circles at the right place
	private void moveTo() { 

		if(action != "first") {
			if(action == "schedule"){
				moveCirclesTo(7, -1); // OK-Previous-Load
			}
			else if(action == "load" || action == "detailSchedule" || action == "save" || action == "detailRoute") {
				moveCirclesTo(5,6); // carte-save-Previous
			}
			else {
				moveCirclesToNull(2); // change position except previous and exit
			}
			moveCircleTo(8); // previous
		}
		else {
			moveToMenu();
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/////////////////////////////////////////////////// USEFUL FUNCTIONS ///////////////////////////////////////////
	
	// transform second on a real time
	private String toTime(String sec) {
		double s = Integer.parseInt(sec);
		int h = 0;
		int m = 0;
		while(s-3600 >= 0) {
			s=s-3600;
			h++;
		}
		while(s-60 >= 0) {
			s=s-60;
			m++;
		}
		return (int)h+"h"+(int)m+"m"+(int)s+"s";
	}
	
	// previous action
	private String previous(){
		if(action == "schedule" || action == "aboutUs" || action == "perturbation" || action == "route"){
			return "first";
		}
		else if(action == "load"){
			return "schedule";
		}
		else if(action == "detailSchedule" || action == "save"){
			reinitializeRoute();
			return "schedule";
		}
		else if(action == "detailRoute"){
			return "route";
		}
		else if(action == "carte"){
			return "detailSchedule";
		}
		else if(action == "horaries"){
			return "route";
		}
		return action;
	}

	// reinitialize data of route
	private void reinitializeRoute() {
		start = "";
		end = "";
		listStations = new ArrayList<ArcTrajet>();
		listNameStations = new ArrayList<String>();
		listHoraire = new ArrayList<String>();
		listCoordStations = new ArrayList<Location>();
	}

	// test the station validity
	private boolean isValid(String s) {
		if(s == "bus" || s == "tram") {
			return true;
		}
		for(int i = 0; i < infoStations.size(); i++) {
			if(s.equals(infoStations.get(i))) {
				return true;
			}
		}
		return false;
	}

	// test the start and end validity
	private boolean isValid(String s, String t, String city, String city2) {
		// need to verify name cities validity before or arrange it if not write correctly

		int k = 0;
		while((startLocation.getLat() == 0 || endLocation.getLat() == 0) && k < allLocations.size()) {
			if(allLocations.get(k).getString().toLowerCase().equals((String) s.toLowerCase() + city.toLowerCase())) {
				startLocation = allLocations.get(k).getLocation();
			}
			else if(allLocations.get(k).getString().toLowerCase().equals((String) t.toLowerCase() + city2.toLowerCase())) {
				endLocation = allLocations.get(k).getLocation();
			}
			k++;
		}

		if(k < allLocations.size()) {
			return true;
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	public void chargeInfoStations() {
		try {
			ObjectInputStream entry = new ObjectInputStream(new FileInputStream("src/data1/infoLignes.dat"));
			infoStations = (List<String>) entry.readObject();
			entry.close();
		}
		catch(Exception e) {
			System.out.println("Error : " + e.getMessage());
		}
	}

	// charge all location possible
	@SuppressWarnings("unchecked")
	private void chargeAllLocations() {
		try {
			ObjectInputStream entry = new ObjectInputStream(new FileInputStream("src/data1/allPositions.dat"));
			allLocations = (List<PositionInCity>) entry.readObject();
			entry.close();
		}
		catch(Exception e) {
			System.out.println("Error : " + e.getMessage());
		}
	}

	@SuppressWarnings("unchecked")
	private void chargeGrapheOfWalkTime() {
		try {
			ObjectInputStream load = new ObjectInputStream(new FileInputStream("src/data1/grapheBx.dat"));
			FadeTwoMapsApp.loadedGraphe = (SimpleWeightedGraph<String, Edge>) load.readObject();
			load.close();
		}
		catch(Exception e) {
			System.out.println("Error " + e.getMessage());
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/////////////////////////////////////////////////// MAIN FUNCTION //////////////////////////////////////////////	

	public static void main(String[] args) {
		launch(args);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/////////////////////////////////////////////////// START APPLICATION //////////////////////////////////////////	

	// start function
	public void start(final Stage stage) {
		
		///////////////////////////////////////////// INITALIZATION ////////////////////////////////////////////

		// initialize canvas and display
		stage.setTitle("Projet de programmation");
		stage.setResizable(false); // can't change size
		final Group root = new Group();
		Scene scene = new Scene(root, WIDTH, HEIGHT);
		final Canvas canvas = new Canvas(WIDTH, HEIGHT);
		root.getChildren().add(canvas);
		final GraphicsContext gc = canvas.getGraphicsContext2D();
		gc.setFont(Font.font("Helvetica", FontWeight.BOLD, 24));
		stage.setScene(scene);
		stage.show();

		// initialize  perturbations 
		listBanned = Deserialisable.takePerturbations();

		// initialize graphe of walk
		chargeGrapheOfWalkTime();

		// initialize informations of stations
		chargeInfoStations();

		// initialize all locations
		chargeAllLocations();

		// initalize images transport
		imgListTransport.add(imgBus);
		imgListTransport.add(imgTram);
		imgListTransport.add(imgMarche);

		// initialize circles
		Circle circleSchedule = new Circle(bW2/2);
		Circle circleRoute = new Circle(bW2/2);
		Circle circlePerturbation = new Circle(bW2/2);
		Circle circleAboutUs = new Circle(bW2/2);
		Circle circleLeave = new Circle(bestWidth/30);
		Circle circleLoad = new Circle(bestWidth/30);
		Circle circleSave = new Circle(bestWidth/30);
		Circle circlePrevious = new Circle(bestWidth/30);
		Circle circleOther = new Circle(bestWidth/30);
		Circle circleDetail = new Circle(bestWidth/30);

		// initialize list of this circles
		circles = new ArrayList<Circle>();
		circles.add(circleSchedule);circles.add(circleRoute);circles.add(circlePerturbation);
		circles.add(circleAboutUs);circles.add(circleOther);circles.add(circleDetail);
		circles.add(circleSave);circles.add(circleLoad);circles.add(circlePrevious);circles.add(circleLeave);

		// initialize circle's position
		moveToMenu();

		// to choose the objective
		HBox buttonCheck = new HBox();
		CheckBox check0 = new CheckBox();
		CheckBox check1 = new CheckBox();
		CheckBox check2 = new CheckBox();
		CheckBox check3 = new CheckBox();

		///////////////////////////////////////////// MOUSE EVENT /////////////////////////////////////////

		//  mouse click
		scene.setOnMouseClicked(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent e) {

				// get the position of the click
				pointClick = new Point(e.getX(), e.getY());

				// Recall : circles = (schedule0, route1, perturbation2, aboutUs3, other4, carte5, save6, load7, previous8, leave9)
				// updating action
				if (circles.get(0).isInside(pointClick)) {
					action = "schedule";
					start = ""; end = ""; date = ""; objectif = 0; // reinitialize values
				}
				else if(circles.get(1).isInside(pointClick)){
					action = "route";

				}
				else if(circles.get(2).isInside(pointClick)){
					action = "perturbation";
					circlesPerturb = new ArrayList<Circle>(); // initialize the list of perturbation's circle
					if(listBanned.size() > 0) {
						for(int i = 0; i < listBanned.size(); i++) {
							Circle c = new Circle(bestWidth/30, new Point((bestWidth/10)*(i+1)+bestWidth/60, 1.25*bH2+bestWidth/60)); // circle's position
							circlesPerturb.add(c);
						}
					}

				}
				else if(circles.get(3).isInside(pointClick)){
					action = "aboutUs";
				}
				else if(circles.get(4).isInside(pointClick)){
					action = "other";
				}
				else if(circles.get(5).isInside(pointClick)){

					FadeTwoMapsApp.OneMain(); // launch the map of the city with the route

				}
				else if(circles.get(6).isInside(pointClick)){
					action = "save";
					Serialisable.SaveSchedule(); // save the route
				}
				else if(circles.get(7).isInside(pointClick)){
					action = "load";

					Deserialisable.Deserialize(); // load the route saved

					if(listStations.size() == 0) {
						action = previous();
					}

				}
				else if(circles.get(8).isInside(pointClick)){
					action = previous();
					countButtonOk = 0; // to delete the root
				}
				else if(circles.get(9).isInside(pointClick)){
					action = "leave";
				}	
				else if(action == "horaries"){
					if(e.getButton().equals(MouseButton.PRIMARY)) {  // double click 
						if(e.getClickCount() == 2) { 
							if(zoom == 0) { // zoom in 
								gc.scale(2.0, 2.0);
								moveCirclesToNull(0); // without circles
								zoom = 1; // zoom is activated
							}
							else {
								moveCircleExit();
								while(nbrDecalX != 0) { // offset limited to the width of the canvas
									gc.translate(0, HEIGHT/4);
									nbrDecalX--;
								}
								while(nbrDecalY != 0) { // offset limited to the height of the canvas
									gc.translate(WIDTH/4, 0);
									nbrDecalY--;
								}
								gc.scale(0.5, 0.5); // zoom out
								zoom = 0; // zoom is inactive
							}
						}
					}
				}
				if(zoom != 1) { // don't add circles if zoom is active 
					moveTo();
				}

				// to delete a perturbation
				if(action == "perturbation") { 
					if(listBanned.size() > 0) {
						for(int i = 0; i < listBanned.size(); i++) {
							if(circlesPerturb.get(i).isInside(pointClick)) {
								listBanned.remove(i);
								circlesPerturb.remove(i);
								Serialisable.putPerturbations(listBanned);
								listBanned = Deserialisable.takePerturbations();
								break;
							}
						}

					}
				}

				System.out.println(action); // display the action
				System.out.println(pointClick); // display the click's coordinates
			}

		});

		// if zoom is active, we retain the place clicked for offset to check the schedules
		scene.setOnMousePressed(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent e) {
				if(zoom == 1) {
					pointClick = new Point(e.getX(), e.getY());
				}
			}
		});

		// scroll to zoom or not (no mouse to test)
		scene.setOnScroll(new EventHandler<ScrollEvent>(){
			public void handle( ScrollEvent event ) {
				if(action == "horaries") {
					if(zoom == 0) {
						gc.scale(2.0, 2.0);
						moveCirclesToNull(0); // without circles
						zoom = 1; // zoom is activated
						moveTo();

					}
					else {
						gc.scale(0.5, 0.5);
						zoom = 0;
					}
				}
			}
		});

		// repair a movement for offset to check the schedules
		EventHandler<MouseEvent> mouse = new EventHandler<MouseEvent>() {
			public void handle(MouseEvent e) {

				if(zoom == 1) { // zoom must be active
					Point pointRelease = new Point(e.getX(), e.getY()); // point release coordinates

					if(!e.isDragDetect()) {

						if(pointClick.getPointY() < pointRelease.getPointY() && Math.abs(pointRelease.getPointX() - pointClick.getPointX()) <= 90) { // to the North
							if(nbrDecalX > 0) {
								gc.translate(0, HEIGHT/4);
								nbrDecalX--;
							}
						}
						else if(pointClick.getPointY() >= pointRelease.getPointY() && Math.abs(pointRelease.getPointX() - pointClick.getPointX()) <= 90) { // to the South
							if(nbrDecalX < 2) {
								gc.translate(0, -HEIGHT/4);
								nbrDecalX++;
							}
						}
						else if(pointClick.getPointX() < pointRelease.getPointX() && Math.abs(pointRelease.getPointY() - pointClick.getPointY()) <= 90) { // to the East
							if(nbrDecalY > 0) {
								gc.translate(WIDTH/4, 0);
								nbrDecalY--;
							}
						}
						else if(pointClick.getPointX() >= pointRelease.getPointX() && Math.abs(pointRelease.getPointY() - pointClick.getPointY()) <= 90) { // to the West
							if(nbrDecalY < 2) {
								gc.translate(-WIDTH/4, 0);
								nbrDecalY++;
							}

						}
					}
				}

			}
		};
		scene.setOnMouseReleased(mouse);

		///////////////////////////////////////////// ANIMATION LAUNCH /////////////////////////////////////

		AnimationTimer animation = new AnimationTimer() {
			@SuppressWarnings("deprecation")
			public void handle(long arg0){

				gc.drawImage(imgFond, 0, 0); // background

				if(action == "schedule" || action == "route" || action == "perturbation"){
					if(action == "route" || action == "perturbation") {

						if(action == "perturbation") {

							gc.fillText("Une ligne a eviter ??", bestWidth*0.35, bestHeight*0.2);

							if(listBanned.size() > 0) {
								// draw lines to avoid
								for(int i = 0; i < listBanned.size(); i++) {
									double putX = (bestWidth/10)*(i+1);
									double putY = 1.25*bH2;
									try {
										gc.drawImage(new Image(getLogoPathByName(listBanned.get(i)), bestWidth/15, bestHeight/15, false, false), putX, putY);
									}
									catch(Exception e) {

									}
								}
							}

						}
						else {
							gc.fillText("Chercher un horaire", bestWidth*0.35, bestHeight*0.2);
						}
						gc.fillText("Ligne", bestWidth*0.5 , bestHeight*0.33, 30);

						if(countButtonOk == 0) { // added one time to the root

							// text area
							final TextField TextField = new TextField(); 
							TextField.setPromptText(" Ligne ");

							// updating size
							TextField.setMaxWidth(bestWidth*0.5);
							TextField.setMaxHeight(bestWidth/75);

							// button to click
							Button buttonOK = new Button("RECHERCHER");
							buttonOK.setOnAction(new EventHandler<ActionEvent>() {
								public void handle(ActionEvent event) {
									line = TextField.getText();

									if(action == "route") {
										action = "horaries";
									}
									else {
										if(listBanned.size() < 9 && !line.isEmpty() && isValid(line) && !listBanned.contains(line)) { // put bus, tram or a line's number to avoid it (limited to 9)
											Serialisable.retainPerturbation(line); // retain perturbation in memory
											listBanned.clear();
											listBanned = Deserialisable.takePerturbations(); // new list of lines banned

											// updating list of circles 
											circlesPerturb.clear();
											if(listBanned.size() > 0) {
												for(int i = 0; i < listBanned.size(); i++) {
													Circle c = new Circle(bestWidth/30, new Point((bestWidth/10)*(i+1)+bestWidth/60, 1.25*bH2+bestWidth/60) );
													circlesPerturb.add(c);
												}
											}
										}
									}
								}
							});

							// updating position in root
							TextField.setTranslateX(bestWidth*0.41);
							TextField.setTranslateY(bestHeight*0.35);
							buttonOK.setTranslateX(bestWidth*0.465);
							buttonOK.setTranslateY(bestHeight*0.5);

							root.getChildren().addAll(TextField, buttonOK);

							wasInSchedule = 1; // used for previous
						}
						countButtonOk++; // added one time to the root

					}
					else if(action == "schedule"){
						gc.drawImage(imgLoad, bestWidth - 2*bestWidth/15 - bestWidth/30, bestHeight);
						gc.fillText("Chercher un itineraire", bestWidth*0.35 , bestHeight*0.2);

						gc.fillText("Plus rapide", bestWidth*0.1*1.35, bestHeight*0.495, bestWidth*0.08);
						gc.fillText("Moins de marche", bestWidth*0.3*1.25, bestHeight*0.495, bestWidth*0.11);
						gc.fillText("Moins de transport", bestWidth*0.5*1.23, bestHeight*0.495, bestWidth*0.11);
						gc.fillText("Moins d'attente", bestWidth*0.7*1.215, bestHeight*0.495, bestWidth*0.11);
						gc.fillText("Heure de depart", bestWidth*0.43*0.65, bestHeight*0.586, bestWidth*0.11);
						gc.fillText("ou", bestWidth*0.52, bestHeight*0.643, bestWidth*0.03);
						gc.fillText("Arrivée avant", bestWidth*0.43*0.7, bestHeight*0.697, bestWidth*0.10);


						if(countButtonOk == 0) { // added one time to the root

							// text areas
							TextField TextField = new TextField();
							TextField TextField2 = new TextField();
							TextField TextField3 = new TextField();
							TextField TextField4 = new TextField();
							TextField TextField5 = new TextField();
							TextField TextField6 = new TextField();

							TextField.setPromptText(" Adresse de depart ");
							//TextField.setText("46 rue jules guesde");
							TextField2.setPromptText(" Adresse d'arrivee ");
							//TextField2.setText("48 rue jules guesde");
							Date d = new Date();
							TextField3.setText(d.getHours() + ":" + d.getMinutes() + ":" + d.getSeconds());
							TextField4.setPromptText("00:00:00");
							TextField5.setText("Bordeaux");
							TextField6.setText("Bordeaux");

							buttonCheck.setPrefSize(bestWidth*0.5, bestHeight/75);

							// button launch
							Button buttonOK = new Button("RECHERCHER");			

							buttonOK.setOnAction(new EventHandler<ActionEvent>() {
								public void handle(ActionEvent event) {

									// useful to find the better route
									start = TextField.getText(); 
									end = TextField2.getText();
									date = TextField3.getText(); 

									if(check0.isSelected()) {
										objectif = 0;
									}
									else if(check1.isSelected()) {
										objectif = 1;
									}
									else if(check2.isSelected()) {
										objectif = 2;
									}
									else if(check3.isSelected()) {
										objectif = 4;
									}
									else {
										objectif = -1;
									}

									if(isValid(start, end, TextField5.getText(), TextField6.getText()) &&  objectif != -1) { // check validity
										action = "detailSchedule";
										moveTo();
										listStations = new ArrayList<ArcTrajet>();
										String dateEnd = TextField4.getText();

										if(objectif != 4) {
											listNameStations = new ArrayList<String>();
											listNumeroStations = new ArrayList<String>();
											listHoraire = new ArrayList<String>();
											listCoordStations = new ArrayList<Location>();

											Coordonnees p1 = new Coordonnees(startLocation.getLat(), startLocation.getLon());
											boolean partirA = true;
											if(dateEnd == "") {
												partirA = false;
											}
												
											
											
											ChercherChemin c = new ChercherChemin();
											c.chercherChemin(start, end, dateEnd, 7, objectif, partirA);
										
											
											////////////////////////////////////////////////////////// EXAMPLES ////////////////////////////////////////////////////////////
											
											// sourceT not present
											/*ArcTrajet startArc = new ArcTrajet(2, start + "%" + p1 + "%17:00:00", "Arts et Metiers%44.805983;-0.602284%17:10:00", "marche");

											ArcTrajet e0 = new ArcTrajet(0, "Arts et Metiers%44.805983;-0.602284%17:10:00", "Quinconces B%44.844469;-0.573792%17:20:00", "Tram B");
											ArcTrajet e1 = new ArcTrajet(2, "Quinconces B%44.844469;-0.573792%17:20:00", "Quinconces C%44.84420;-0.57195%17:30:00", "marche");
											ArcTrajet e2 = new ArcTrajet(0, "Quinconces C%44.84420;-0.57195%17:30:00", "Gare St-Jean%44.825918;-0.556807%17:40:00", "Tram C");

											listStations.add(startArc);
											listStations.add(e0);
											listStations.add(e1);
											listStations.add(e2);
											
											listNameStations.add(start); listNameStations.add("Arts et Metier"); listNameStations.add("Quinconces B"); listNameStations.add("Quinconces C"); listNameStations.add("Gare St_Jean");
											listCoordStations.add(startLocation); listCoordStations.add(new Location(44.805983,-0.602284)); listCoordStations.add(new Location(44.844469,-0.573792)); listCoordStations.add(new Location(44.84420,-0.57195)); listCoordStations.add(new Location(44.825918,-0.556807));
											listHoraire.add("600");listHoraire.add("600");listHoraire.add("600");listHoraire.add("600");
											listNumeroStations.add("marche");listNumeroStations.add("Tram B");listNumeroStations.add("marche");listNumeroStations.add("Tram C");
											date = "17:00:00";*/

											///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////									

											// to check !!
											listCoordStations.add(startLocation);

											for(int i = 0; i < listStations.size(); i++) {
												listNumeroStations.add(listStations.get(i).getNom());
												
												String[] m = ((String) listStations.get(i).getSourceT()).split("%");
												int index = m[1].indexOf(";");
												Double d1 = Double.parseDouble(m[1].substring(0, index));
												Double d2 = Double.parseDouble(m[1].substring(index+1));

												listNameStations.add(m[0]);
												listHoraire.add(listStations.get(i).getWeightT() + "");
												if(i != 0) {
													listCoordStations.add(new Location(d1,d2));
												}
											}

											String[] m = ((String) listStations.get(listStations.size()-1).getSourceT()).split("%");
											int index = m[1].indexOf(";");
											Double d1 = Double.parseDouble(m[1].substring(0, index));
											Double d2 = Double.parseDouble(m[1].substring(index+1));
											
											listNameStations.add(m[0]);
											listCoordStations.add(new Location(d1, d2));
											
											System.out.println(listStations);
											System.out.println(listNameStations);
											System.out.println(listCoordStations);
											System.out.println(listHoraire);
											System.out.println(listNumeroStations);
											
											for(int i = 0; i < listHoraire.size(); i++) {
												tempsFinal += Integer.parseInt(listHoraire.get(i)); 
												listHoraire.set(i, toTime(listHoraire.get(i)));
											}
										}
										else {
											listCoordStations = new ArrayList<Location>();
											listCoordStations.add(startLocation);
											listCoordStations.add(endLocation);
											FadeTwoMapsApp.OneMain();
											action = "schedule";
										}
									}
									else { // if one element is not valid 
										action = "schedule";
										moveTo();
									}

								}

							});				        

							// updating position in root
							TextField.setTranslateX(bestWidth*0.2);
							TextField.setTranslateY(bestHeight*0.29);
							TextField2.setTranslateX(bestWidth*0.2);
							TextField2.setTranslateY(bestHeight*0.38);
							TextField3.setTranslateX(bestWidth*0.43);
							TextField3.setTranslateY(bestHeight*0.56);
							TextField4.setTranslateX(bestWidth*0.43);
							TextField4.setTranslateY(bestHeight*0.67);
							TextField5.setTranslateX(bestWidth*0.6);
							TextField5.setTranslateY(bestHeight*0.29);
							TextField6.setTranslateX(bestWidth*0.6);
							TextField6.setTranslateY(bestHeight*0.38);

							check0.setTranslateX(bestWidth*0.1);
							check0.setTranslateY(bestHeight*0.47);
							check1.setTranslateX(bestWidth*0.3);
							check1.setTranslateY(bestHeight*0.47);
							check2.setTranslateX(bestWidth*0.5);
							check2.setTranslateY(bestHeight*0.47);
							check3.setTranslateX(bestWidth*0.7);
							check3.setTranslateY(bestHeight*0.47);

							buttonOK.setTranslateX(bestWidth*0.46);
							buttonOK.setTranslateY(bestHeight*0.8);

							// linked with root
							check0.setSelected(true);
							if(!buttonCheck.getChildren().contains(check0)) {
								buttonCheck.getChildren().addAll(check0, check1, check2, check3);
							}
							
							root.getChildren().addAll(buttonCheck);

							root.getChildren().addAll(TextField, buttonOK);
							root.getChildren().addAll(TextField2);
							root.getChildren().addAll(TextField3);
							root.getChildren().addAll(TextField4);
							root.getChildren().addAll(TextField5);
							root.getChildren().addAll(TextField6);

							wasInSchedule = 1; // used for previous

						}
						if(check0.isArmed() || check1.isArmed() || check2.isArmed() || check3.isArmed()) {
							check0.setSelected(false);
							check1.setSelected(false);
							check2.setSelected(false);
							check3.setSelected(false);
						}
						countButtonOk++; // added ont time to the root
					}
				}

				else if (action == "detailSchedule" || action == "load" || action == "save") {
					wasInSchedule = 0;
					root.getChildren().clear();
					root.getChildren().add(canvas);

					int size = listStations.size();
					double pos = 0.9*bestHeight/size;

					gc.strokeLine(0.5*bW2, 0.5*pos, 0.5*bW2, 0.5*pos + pos*(size-1)); // draw a line

					for(int i = 0; i<size-1; i++) {
						// to get the name of stations
						int transport = listStations.get(i).getTransport();
						String source = listNameStations.get(i); 

						String weight = listHoraire.get(i);

						gc.drawImage(imgListTransport.get(transport), 0.5*bW2 - imgListTransport.get(transport).getHeight()/2, 0.5*pos + pos*i);

						gc.fillText(source, 0.7*bW2, 0.3*pos + pos*i, bestWidth*0.2);

						gc.fillText(weight, 1.5*bW2, 0.5*pos + pos*i, bestWidth*0.1);
					}

					int transport = listStations.get(size-1).getTransport();
					gc.drawImage(imgListTransport.get(transport), 0.5*bW2 - imgListTransport.get(transport).getHeight()/2, 0.5*pos + pos*(size-1));

					gc.fillText(listNameStations.get(size-1), 0.7*bW2, 0.3*pos + pos*(size-1), bestWidth*0.2);
					gc.fillText(end, 0.7*bW2, 0.4*pos + pos*(size-1) + 1.5*imgListTransport.get(transport).getHeight(), bestWidth*0.2);

					gc.fillText(listHoraire.get(size-1), 1.5*bW2, 0.5*pos + pos*(size-1), bestWidth*0.1);


					gc.drawImage(imgSave, bestWidth - 3*bestWidth/15 - bestWidth/30, bestHeight);
					gc.drawImage(imgDetail, bestWidth - 2*bestWidth/15 - bestWidth/30, bestHeight);


					if (action == "load") {
						gc.fillText("LOADED", WIDTH/40, HEIGHT -10);
					}
					if(action == "save") {
						gc.fillText("SAVED", WIDTH/40, HEIGHT -10);
					}
				}

				else if(action == "aboutUs") {
					gc.fillText("En cours de construction", 0.7*bW2, 100);
				}
				else if(action == "horaries") { 
					root.getChildren().clear();
					root.getChildren().add(canvas);

					if(countButtonOk == 1) {
						countButtonOk = 0;
						action = "route";
					}
					else {
						try {
							gc.drawImage(new Image(getHorariesPathByName(line + ".jpg"), WIDTH, HEIGHT, false, false), 0, 0); 
						}
						catch(Exception ex) {
							action = previous();
							countButtonOk = 0;
						}
					}

				}

				else { // menu

					if(wasInSchedule == 1) {
						wasInSchedule = 0;
						root.getChildren().clear();
						root.getChildren().add(canvas);
					}
					countButtonOk = 0;
					gc.drawImage(imgSchedule, 0,0);
					gc.drawImage(imgRoute, bW2, 0);
					gc.drawImage(imgPerturbation, 0, bH2);
					gc.drawImage(imgAboutUs, bW2 , bH2);

				}

				if(action != "first") {
					gc.drawImage(imgPrevious, bestWidth*0.9, bestHeight);
				}
				gc.drawImage(imgLeave, bestWidth , bestHeight);


				if(action == "leave") {
					stage.close();
				}

				// to draw circles 
				/*for(int i = 0; i < nbrCercle; i++) { // put a circle at his position
					gc.strokeOval(circles.get(i).getCenterX()-circles.get(i).getRadius(), circles.get(i).getCenterY()-circles.get(i).getRadius(), circles.get(i).getRadius()*2, circles.get(i).getRadius()*2);
				}*/

			}
		};
		animation.start();
	}
	
}
