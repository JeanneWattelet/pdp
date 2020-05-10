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

import domain.ArcTrajet;

public class Deserialisable  {

	public static void Deserialize() {

		List<ArcTrajet> listStats = new ArrayList<ArcTrajet>();		
		try {
			ObjectInputStream entry = new ObjectInputStream(new FileInputStream("src/saves/schedule.dat"));
			String last = (String) entry.readObject();
			int size = (int) entry.readDouble();

			for (int i=0; i<size; i++) {
				ArcTrajet arc= new ArcTrajet();
				arc = (ArcTrajet) entry.readObject();

				listStats.add(arc);

			}		
			entry.close();
			Other.end = last;
			Other.listStations.clear();
			Other.listStations.addAll(listStats);
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
