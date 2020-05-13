package Interface;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.fhpotsdam.unfolding.geo.Location;
import domain.ArcTrajet;

public class Deserialisable  {

	public static void Deserialize() {

		try {
			ObjectInputStream entry = new ObjectInputStream(new FileInputStream("src/saves/schedule.dat"));
			Other.start = (String) entry.readObject();
			Other.end = (String) entry.readObject();
			Other.objectif = (int) entry.readObject();
			Other.listStations = (List<ArcTrajet>) entry.readObject();
			Other.listNameStations = (List<String>) entry.readObject();
			Other.listHoraire = (List<String>) entry.readObject();
			Other.listCoordStations = (List<Location>) entry.readObject();
			Other.listNumeroStations = (List<String>) entry.readObject();
		}
		catch (Exception e){

		}

	}

	public static List<String> takePerturbations() {
		List<String> l = new ArrayList<String>();
		int cpt = 0;
		try (BufferedReader reader = new BufferedReader(new FileReader("src/saves/perturbations.txt"))) {
			String last = reader.readLine();
			cpt++;
			while(!last.isEmpty()) {

				l.add(last);
				last = reader.readLine();

			}
			reader.close();
		}
		catch (Exception e){

		}

		return l;
	}







}
