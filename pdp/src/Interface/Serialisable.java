package Interface;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Iterator;
import java.util.List;

import domain.ArcTrajet;

public class Serialisable {

	public static void SaveSchedule() {

		try {
			ObjectOutputStream save = new ObjectOutputStream(new FileOutputStream("src/saves/schedule.dat"));
			save.writeObject(Other.end);
			save.writeObject(Other.listStations.size());
			Iterator<ArcTrajet> it = Other.listStations.iterator();
			while(it.hasNext()) {
				ArcTrajet a = it.next();
				save.writeObject(a);
			}
			save.close();
		}
		catch (Exception e) {
			System.out.println("probleme fichier: " + e.getMessage());
		}

	}

	public static void retainPerturbation(String line) {
		try (BufferedWriter writer = new BufferedWriter(new FileWriter("src/saves/perturbations.txt", true))) { 
			writer.write(line + "\n");
			writer.close();
		}
		catch (Exception e) {
			System.out.println("probleme fichier: " + e.getMessage());
		}

	}

	public static void putPerturbations(List<String> listBanned) {
		try (BufferedWriter writer = new BufferedWriter(new FileWriter("src/saves/perturbations.txt"))) { 
			if(listBanned.size() > 0) {
			
				for(int i = 0; i < listBanned.size(); i++) {
					writer.write(listBanned.get(i) + "\n");
				}
			}
			writer.close();

		}
		catch (Exception e) {
			System.out.println("probleme fichier: " + e.getMessage());
		}

	}
}