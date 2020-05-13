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

public class Serialisable {

	public static void SaveSchedule() {

		try {
			ObjectOutputStream save = new ObjectOutputStream(new FileOutputStream("src/saves/schedule.dat"));
			save.writeObject(Other.start);
			save.writeObject(Other.end);
			save.writeObject(Other.objectif);
			save.writeObject(Other.listStations);
			save.writeObject(Other.listNameStations);
			save.writeObject(Other.listHoraire);
			save.writeObject(Other.listCoordStations);
			save.writeObject(Other.listNumeroStations);
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
