package domain;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import transport.Ligne;


public class SerializeGrapheTrajet {
	
	public static void serialiserGrapheTrajet(GrapheTrajet gt){
		ObjectOutputStream oos = null;
		
		try {
			final FileOutputStream fichier = new FileOutputStream("src\\saves\\"+gt.getJour()+".ser");
			oos = new ObjectOutputStream(fichier);
			oos.writeObject(gt);
			oos.flush();
		} catch (final java.io.IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (oos != null) {
					oos.flush();
					oos.close();
				}
			} catch (final IOException ex) {
				ex.printStackTrace();
			}
		}
	}
	
	public static void serialiserArcTrajet(List<ArcTrajet> lat, int j){
		ObjectOutputStream oos = null;
		
		try {
			final FileOutputStream fichier = new FileOutputStream("src\\data1\\ListArcTrajetPied"+j+"bus.dat");
			oos = new ObjectOutputStream(fichier);
			oos.writeObject(lat);
			oos.flush();
		} catch (final java.io.IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (oos != null) {
					oos.flush();
					oos.close();
				}
			} catch (final IOException ex) {
				ex.printStackTrace();
			}
		}
	}
	
	public static List<ArcTrajet> deserialiserGrapheTrajet(int jour){
		List<ArcTrajet> l1 = new ArrayList<ArcTrajet>();

		try {
			ObjectInputStream load = new ObjectInputStream(new FileInputStream("src/someData/aretesAttenteMemeEndroitBus" + jour + ".dat"));
			ObjectInputStream load1 = new ObjectInputStream(new FileInputStream("src/someData/aretesTram" + jour + ".dat"));
			ObjectInputStream load2 = new ObjectInputStream(new FileInputStream("src/someData/aretesBus" + jour + ".dat"));
			ObjectInputStream load3 = new ObjectInputStream(new FileInputStream("src/someData/aretesMarcheSimplifier" + jour + ".dat"));
			ObjectInputStream load4 = new ObjectInputStream(new FileInputStream("src/someData/aretesAttenteApresMarche" + jour + ".dat"));

			l1 =(List<ArcTrajet>) load.readObject();
			l1.addAll((List<ArcTrajet>) load1.readObject());
			l1.addAll((List<ArcTrajet>) load2.readObject());
			l1.addAll((List<ArcTrajet>) load3.readObject());
			l1.addAll((List<ArcTrajet>) load4.readObject());

			load.close();
			load1.close();
			load2.close();
			load3.close();
			load4.close();
		}
		catch(Exception e) {
			System.out.println("error : " + e.getMessage());
		}
		
		return l1;
	}
	
	public static void saveTramEtBus(int jour) {
		List<Ligne> tram = new ArrayList<Ligne>();
		List<Ligne> bus = new ArrayList<Ligne>();
		try {
			tram = Donnees.ChargerDonnees("src\\keolis_tram", jour);
			bus = Donnees.ChargerDonnees("src\\keolis_bus", jour);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		try {
			ObjectOutputStream save = new ObjectOutputStream(new FileOutputStream("src/someData/tram" + jour + ".dat"));
			ObjectOutputStream save2 = new ObjectOutputStream(new FileOutputStream("src/someData/bus" + jour + ".dat"));
			save.writeObject(tram);
			save2.writeObject(bus);
			save.close();
			save2.close();
		}
		catch(Exception e) {
			System.out.println("error : " + e.getMessage());
		}
	}
	
	public static List<Ligne> loadTram(int jour){
		List<Ligne> tram = new ArrayList<Ligne>(); 
		try {
			ObjectInputStream save = new ObjectInputStream(new FileInputStream("src/someData/tram" + jour + ".dat"));
			tram = (List<Ligne>) save.readObject();
			save.close();
		} catch(Exception e) {
			System.out.println(e.getMessage());
		}
		return tram;
	}
	
	public static List<Ligne> loadBus(int jour){
		List<Ligne> bus = new ArrayList<Ligne>();
		try {
			ObjectInputStream save2 = new ObjectInputStream(new FileInputStream("src/someData/bus" + jour + ".dat"));
			bus = (List<Ligne>) save2.readObject();
			save2.close();
		} catch(Exception e) {
			System.out.println(e.getMessage());
		}
		return bus;
	}
	
	public static List<ArcTrajet> loadAreteAttenteMemeEndroit(int jour) {
		List<ArcTrajet> l = new ArrayList<ArcTrajet>();
		try {
			ObjectInputStream load = new ObjectInputStream(new FileInputStream("src/someData/aretesAttenteMemeEndroitBus" + jour + ".dat"));
			l = (List<ArcTrajet>) load.readObject();
			load.close();
		}
		catch(Exception e) {
			System.out.println("error : " + e.getMessage());
		}
		return l;
	}
}
