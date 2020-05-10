package Interface;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.jgrapht.graph.SimpleWeightedGraph;

import de.fhpotsdam.unfolding.examples.animation.Edge;
import de.fhpotsdam.unfolding.examples.animation.FadeTwoMapsApp;
import de.fhpotsdam.unfolding.examples.animation.PositionInCity;
import de.fhpotsdam.unfolding.geo.Location;
import domain.ArcTrajet;
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
	private int objectif = 0;
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
	private Image imgSave = new Image(getRessourcePathByName("save.jpg"), bestWidth/15, bestHeight/15, false, false);
	private Image imgPrevious = new Image(getRessourcePathByName("previous.jpg"), bestWidth/15, bestHeight/15, false, false);
	private Image imgDetail = new Image(getRessourcePathByName("detail.jpg"), bestWidth/15, bestHeight/15, false, false);

	private Image imgBus = new Image(getRessourcePathByName("bus.png"), bestWidth/12, bestHeight/12, false, false);
	private Image imgTram = new Image(getRessourcePathByName("tram.png"), bestWidth/12, bestHeight/12, false, false);
	private Image imgMarche = new Image(getRessourcePathByName("marche.png"), bestWidth/12, bestHeight/12, false, false);

	// list of images corresponding (schedule0, route1, perturbation2, aboutUs3, other4, carte5, save6, load7, previous8, leave9)
	private List<Image> imgListTransport = new ArrayList<Image>();

	// list of circles 
	private static List<Circle> circles;
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
		int j=3;
		if(a == -1) {
			j--;
		}
		for(int i = 0; i<nbrCercle-2; i++) {
			if(i == a || i == b) {
				circles.get(i).putTo((WIDTH-25)-50*j, HEIGHT-25);
				j--;
			}
			else {
				circles.get(i).putTo(-500, -500);
			}
		}
	}

	// move break to its position
	public void moveCircleTo(int a) {
		if(a == 8) {
			circles.get(a).putTo(WIDTH-75, HEIGHT-25);
		}
		else {
			circles.get(a).putTo(WIDTH-125, HEIGHT-25);
		}
	}

	// move circles to the menu position and exclude the others
	public void moveToMenu() {
		circles.get(0).putTo(bW2/2, bH2/2);
		circles.get(1).putTo(bW2+bW2/2,bH2/2);
		circles.get(2).putTo(bW2/2,bH2+bH2/2);
		circles.get(3).putTo(bW2+bW2/2,bH2+bH2/2);
		circles.get(9).putTo(WIDTH-25,HEIGHT-25);
		for(int i = 4; i<9; i++) {
			circles.get(i).putTo(-500, -500);
		}
	}


	// recall : circles = (schedule0, route1, perturbation2, aboutUs3, other4, carte5, save6, load7, previous8, leave9)
	// move circles at the right place
	private void moveTo() { 

		if(action != "first") {
			if(action == "schedule"){
				moveCirclesTo(-1,7); // OK-Previous-Load
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

	// previous action
	private String previous(){
		if(action == "schedule" || action == "aboutUs" || action == "perturbation" || action == "route"){
			return "first";
		}
		else if(action == "load"){
			return "schedule";
		}
		else if(action == "detailSchedule" || action == "save"){
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

	// test the station validity
	private boolean isValid(String s, int k) {
		return true;
	}

	// test the start and end validity
	private boolean isValid(String s, String t) {

		int k = 0;
		while((startLocation.getLat() == 0 || endLocation.getLat() == 0) && k < allLocations.size()) {
			if(allLocations.get(k).getString().toLowerCase().equals((String) s.toLowerCase())) {
				startLocation = allLocations.get(k).getLocation();
			}
			else if(allLocations.get(k).getString().toLowerCase().equals((String) t.toLowerCase())) {
				endLocation = allLocations.get(k).getLocation();
			}
			k++;
		}

		if(k < allLocations.size()) {
			return true;
		}
		return false;
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

	@SuppressWarnings("unused")
	private void saveCircles() {
		try {
			ObjectOutputStream entry = new ObjectOutputStream(new FileOutputStream("src/data1/allCircles.dat"));
			
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
			
			entry.writeObject(circles);
			entry.close();
		}
		catch(Exception e) {
			System.out.println("Error : " + e.getMessage());
		}
	}

	@SuppressWarnings("unchecked")
	private void loadCircles() {
		try {
			ObjectInputStream entry = new ObjectInputStream(new FileInputStream("src/data1/allCircles.dat"));
			circles = (List<Circle>) entry.readObject();
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

	/*public static void main(String[] args) {
		launch(args);
	}*/

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
		
		chargeGrapheOfWalkTime();

		// initialize all locations
		chargeAllLocations();

		// initalize images transport
		imgListTransport.add(imgBus);
		imgListTransport.add(imgTram);
		imgListTransport.add(imgMarche);

		// load circles
		loadCircles();

		// initialize circle's position
		moveToMenu();

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
				if(action != "first") {
					gc.drawImage(imgPrevious, bestWidth-50, bestHeight); // exit
				}
				

				if(action == "schedule" || action == "route" || action == "perturbation"){
					if(action == "route" || action == "perturbation") {

						if(action == "perturbation") {

							gc.fillText("Une ligne a eviter ??", bestWidth*0.38 , bestHeight*0.2);

							if(listBanned.size() > 0) {
								// draw lines to avoid
								for(int i = 0; i < listBanned.size(); i++) {
									double putX = (bestWidth/10)*(i+1);
									double putY = 1.25*bH2;
									gc.drawImage(new Image(getLogoPathByName(listBanned.get(i)), bestWidth/15, bestHeight/15, false, false), putX, putY);
								}
							}

						}
						else {
							gc.fillText("Chercher un horaire", bestWidth*0.38, bestHeight*0.2);
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
							Button buttonOK = new Button("LAUNCH");
							buttonOK.setOnAction(new EventHandler<ActionEvent>() {
								public void handle(ActionEvent event) {
									line = TextField.getText();

									if(action == "route") {
										action = "horaries";
									}
									else {
										if(listBanned.size() < 9 && !line.isEmpty() && isValid(line, 3) && !listBanned.contains(line)) { // put bus, tram or a line's number to avoid it (limited to 9)
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
							TextField.setTranslateX(bestWidth*0.43);
							TextField.setTranslateY(bestHeight*0.35);
							buttonOK.setTranslateX(bestWidth*0.485);
							buttonOK.setTranslateY(bestHeight*0.5);

							root.getChildren().addAll(TextField, buttonOK);

							wasInSchedule = 1; // used for previous
						}
						countButtonOk++; // added one time to the root

					}
					else if(action == "schedule"){
						gc.drawImage(imgLoad, bestWidth-100, bestHeight);
						gc.fillText("Chercher un itineraire", bestWidth*0.375 , bestHeight*0.2);
						gc.fillText("Plus rapide", bestWidth*0.14, bestHeight*0.495, bestWidth*0.08);
						gc.fillText("Moins de marche", bestWidth*0.36, bestHeight*0.495, bestWidth*0.12);
						gc.fillText("Moins de transport", bestWidth*0.58, bestHeight*0.495, bestWidth*0.12);
						gc.fillText("Moins d'attente", bestWidth*0.8, bestHeight*0.495, bestWidth*0.11);
						gc.fillText("Heure de depart", bestWidth*0.3, bestHeight*0.586, bestWidth*0.11);
						gc.fillText("Ou", bestWidth*0.5, bestHeight*0.64, bestWidth*0.6);
						gc.fillText("Arrivée avant", bestWidth*0.3, bestHeight*0.697, bestWidth*0.10);

						if(countButtonOk == 0) { // added one time to the root

							// text areas
							final TextField TextField = new TextField();
							//TextField.setPromptText(" Adresse de départ ");
							TextField.setText("46 rue jules guesde");
							final TextField TextField2 = new TextField();
							//TextField2.setPromptText(" Adresse d'arrivée ");
							TextField2.setText("29 rue Charles Domercq");
							final TextField TextField3 = new TextField();
							Date date = new Date();
							TextField3.setText(date.getHours() + ":" + date.getMinutes() + ":" + date.getSeconds());
							final TextField TextField4 = new TextField();
							TextField4.setPromptText("00:00:00");

							// to choose the objective
							HBox buttonCheck = new HBox();
							CheckBox check0 = new CheckBox();
							CheckBox check1 = new CheckBox();
							CheckBox check2 = new CheckBox();
							CheckBox check3 = new CheckBox();

							// updating size
							TextField.setMaxWidth(bestWidth*0.5);
							TextField.setMaxHeight(bestHeight/75);
							TextField2.setMaxWidth(bestWidth*0.5);
							TextField2.setMaxHeight(bestHeight/75);
							TextField3.setMaxWidth(bestWidth*0.5);
							TextField3.setMaxHeight(bestHeight/75);
							TextField4.setMaxWidth(bestWidth*0.5);
							TextField4.setMaxHeight(bestHeight/75);
							

							// button launch
							Button buttonOK = new Button("LAUNCH");
							buttonOK.setOnAction(new EventHandler<ActionEvent>() {
								public void handle(ActionEvent event) {

									// useful to find the better route
									start = TextField.getText(); 
									end = TextField2.getText();

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
										objectif = 3;
									}
									else {
										objectif = -1;
									}


									if(isValid(start, end) &&  objectif != -1) { // check validity
										action = "detailSchedule";
										moveTo();
										listStations = new ArrayList<ArcTrajet>();
										//List<ArcTrajet> listStation = astar(start, end, date, objectif); // launch ASTAR or DJIKSTRA

										////////////////////////////////////////////////////////// EXAMPLES ////////////////////////////////////////////////////////////

										

										// nom des stations a changer 60 -> Tram B 
										ArcTrajet e = new ArcTrajet(2, "kkpart%17:00:00&44.804;-0.600", "Arts et Metiers%17:10:00&44.805983;-0.602284", "0");
										ArcTrajet e0 = new ArcTrajet(0, "Arts et Metiers%17:10:00&44.805983;-0.602284", "Quinconces B%17:20:00&44.844469;-0.573792", "60");
										ArcTrajet e1 = new ArcTrajet(2, "Quinconces B%17:20:00&44.844469;-0.573792", "Quinconces C%17:30:00&44.84420;-0.57195", "0");
										ArcTrajet e2 = new ArcTrajet(0, "Quinconces C%17:30:00&44.84420;-0.57195", "Gare St-Jean%17:40:00&44.825918;-0.556807", "61");
										listStations.add(e);
										listStations.add(e0);
										listStations.add(e1);
										listStations.add(e2);
										
										startLocation = new Location(44.804,-0.600);
										
										
										
										///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////									


										listNameStations = new ArrayList<String>();
										listNumeroStations = new ArrayList<String>();
										listHoraire = new ArrayList<String>();
										listCoordStations = new ArrayList<Location>();
										
										listCoordStations.add(startLocation);
										
										for(int i = 0; i < listStations.size(); i++) {
											listNumeroStations.add(listStations.get(i).getNom());
											
											int index1 = listStations.get(i).getFrom().indexOf("%");
											int index2 = listStations.get(i).getFrom().indexOf("&");
											int index3 = listStations.get(i).getFrom().indexOf(";");
											
											listNameStations.add(listStations.get(i).getFrom().substring(0, index1));
											listHoraire.add(listStations.get(i).getFrom().substring(index1+1, index2));
											if(i != 0) {
												listCoordStations.add(new Location(Double.parseDouble(listStations.get(i).getFrom().substring(index2+1, index3)), Double.parseDouble(listStations.get(i).getFrom().substring(index3+1))));
											}
										}
										
										int index1 = listStations.get(listStations.size()-1).getTo().indexOf("%");
										int index2 = listStations.get(listStations.size()-1).getTo().indexOf("&");
										int index3 = listStations.get(listStations.size()-1).getTo().indexOf(";");
										listNameStations.add(listStations.get(listStations.size()-1).getTo().substring(0, index1));
										listHoraire.add(listStations.get(listStations.size()-1).getTo().substring(index1+1, index2));
										listCoordStations.add(new Location(Double.parseDouble(listStations.get(listStations.size()-1).getTo().substring(index2+1, index3)), Double.parseDouble(listStations.get(listStations.size()-1).getTo().substring(index3+1))));


									}
									else { // if one element is not valid 
										action = "schedule";
										moveTo();
									}

								}

							});				        

							// updating position in root
							TextField.setTranslateX(bestWidth*0.43);
							TextField.setTranslateY(bestHeight*0.29);
							TextField2.setTranslateX(bestWidth*0.43);
							TextField2.setTranslateY(bestHeight*0.38);
							TextField3.setTranslateX(bestWidth*0.43);
							TextField3.setTranslateY(bestHeight*0.56);
							TextField4.setTranslateX(bestWidth*0.43);
							TextField4.setTranslateY(bestHeight*0.67);
							
							check0.setTranslateX(bestWidth*0.1);
							check0.setTranslateY(bestHeight*0.47);
							check1.setTranslateX(bestWidth*0.3);
							check1.setTranslateY(bestHeight*0.47);
							check2.setTranslateX(bestWidth*0.5);
							check2.setTranslateY(bestHeight*0.47);
							check3.setTranslateX(bestWidth*0.7);
							check3.setTranslateY(bestHeight*0.47);
							
							buttonOK.setTranslateX(bestWidth*0.48);
							buttonOK.setTranslateY(bestHeight*0.8);

							// linked with root
							buttonCheck.getChildren().addAll(check0, check1, check2, check3);
							root.getChildren().addAll(TextField, buttonOK);
							root.getChildren().addAll(TextField2);
							root.getChildren().addAll(TextField3);
							root.getChildren().addAll(TextField4);
							root.getChildren().addAll(buttonCheck);

							wasInSchedule = 1; // used for previous

						}
						countButtonOk++; // added ont time to the root
					}
				}

				else if (action == "detailSchedule" || action == "load" || action == "save") {
					wasInSchedule = 0;
					root.getChildren().clear();
					root.getChildren().add(canvas);

					int size = listStations.size();
					double pos = 0.9*bestWidth/size;

					gc.strokeLine(0.5*bW2, 0.5*pos, 0.5*bW2, 0.5*pos + pos*(size-1)); // draw a line

					for(int i = 0; i<size-1; i++) {
						// to get the name of stations
						int transport = listStations.get(i).getTransport();
						String source = listNameStations.get(i); 

						String weight = listHoraire.get(i);
						
						gc.drawImage(imgListTransport.get(transport), 0.5*bW2 - imgListTransport.get(transport).getHeight()/2, 0.5*pos + pos*i);

						gc.fillText(source, 0.7*bW2, 0.5*pos + pos*i);
						
						gc.fillText(weight, 1.5*bW2, 0.5*pos + pos*i);
					}

					int transport = listStations.get(size-1).getTransport();
					gc.drawImage(imgListTransport.get(transport), 0.5*bW2 - imgListTransport.get(transport).getHeight()/2, 0.5*pos + pos*(size-1));
					
					gc.fillText(listNameStations.get(size-1), 0.7*bW2, 0.5*pos + pos*(size-1));
					gc.fillText(end, 0.7*bW2, 0.5*pos + pos*(size-1) + 1.5*imgListTransport.get(transport).getHeight());
					
					gc.fillText(listHoraire.get(size-1), 1.5*bW2, 0.5*pos + pos*(size-1));
					gc.fillText(listHoraire.get(size), 1.5*bW2, 0.5*pos + pos*(size-1) + 1.5*imgListTransport.get(transport).getHeight());


					gc.drawImage(imgSave, bestWidth-100, bestHeight);
					gc.drawImage(imgDetail, bestWidth-150, bestHeight);

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

					if(!isValid(line, 3)) {
						countButtonOk = 0;
						action = "route";
					}
					else {
						gc.drawImage(new Image(getHorariesPathByName(line + ".jpg"), WIDTH, HEIGHT, false, false), 0, 0); 
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
					gc.drawImage(imgPrevious, bestWidth-50, bestHeight);
				}
				gc.drawImage(imgLeave, bestWidth , bestHeight);


				if(action == "leave") {
					stage.close();
				}

				// to draw circles 
				for(int i = 0; i < nbrCercle; i++) { // put a circle at his position
					gc.strokeOval(circles.get(i).getCenterX()-circles.get(i).getRadius(), circles.get(i).getCenterY()-circles.get(i).getRadius(), circles.get(i).getRadius()*2, circles.get(i).getRadius()*2);
				}

			}
		};
		animation.start();
	}
}
