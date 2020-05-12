package domain;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import transport.*;

public class Transforme {

	public Map<String, List<String>> TabaleauRoutes(String chemin) throws IOException {
		Map<String, List<String>> Routes = new HashMap<String, List<String>>();
		String ligne;
		String[] str = null;

		Path p = Paths.get(chemin+"\\routes.txt");
		BufferedReader read = Files.newBufferedReader(p);

		ligne = read.readLine();
		while( ( ligne = read.readLine() ) != null) {
			str = ligne.split(",") ;
			List<String> TMP = new ArrayList<String>();

			TMP.add(str[2]); // Name
			TMP.add(str[3]); // vehicule_type

			Routes.put(str[0], TMP); // put(id , List<String> ) ;
		}
		return Routes;
	} 
	///////////////////////////////////////////////////////////////////////////////////////////////////
	public List<List<String>> TabaleauTrips(String chemin) throws IOException {
		List<List<String>> trips = new ArrayList<List<String>>();

		String ligne;
		String[] str = null;

		Path p = Paths.get(chemin+"\\trips.txt");
		BufferedReader read = Files.newBufferedReader(p);

		ligne = read.readLine();
		while( ( ligne = read.readLine() ) != null) {
			str = ligne.split(",") ;
			List<String> TMP = new ArrayList<String>();

			TMP.add(str[0]);  // ligne du trajet
			TMP.add(str[1]); // service_id
			TMP.add(str[2]); // trip_id
			TMP.add(str[3]); // direction du trajet
			trips.add(TMP);
		}
		return trips;
	} 
///////////////////////////////////////////////////////////////////////////////////////////////////////
	public Map<String, List<String>> TabaleauCalendrier(String chemin) throws IOException {

		Map<String, List<String>> Calendrier = new HashMap<String, List<String>>();
		String ligne;
		String[] str = null;

		Path p = Paths.get(chemin+"\\calendar.txt");
		BufferedReader read = Files.newBufferedReader(p);

		ligne = read.readLine();
		while( ( ligne = read.readLine() ) != null) {
			str = ligne.split(",") ;
			List<String> TMP = new ArrayList<String>();

			for(int i=1; i<8; i++) // les jours de service
				TMP.add(str[i]);

			Calendrier.put(str[0], TMP ); // put(Service_id , sa semaine)
		}
		return Calendrier;
	} 
///////////////////////////////////////////////////////////////////////////////////////////////////////
	public List<List<String>> TabaleauStopTimes(String chemin) throws IOException {
		List<List<String>> StopTimes = new ArrayList<List<String>>();

		String ligne;
		String[] str = null;

		Path p = Paths.get(chemin+"\\stop_times.txt");
		BufferedReader read = Files.newBufferedReader(p);

		ligne = read.readLine();
		while( ( ligne = read.readLine() ) != null) {
			str = ligne.split(",") ;
			List<String> TMP = new ArrayList<String>();

			TMP.add(str[0]); // trip_id
			TMP.add(str[1]); // heure 
			TMP.add(str[3]); // stop_id

			StopTimes.add(TMP);
		}
		return StopTimes;
	} 

}
