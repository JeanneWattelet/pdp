package Transport;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Scanner;

public class SerializeGrapheTrajet {

	public static void serialiserGrapheTrajet(GrapheTrajet gt){
		ObjectOutputStream oos = null;
		int jour;

		System.out.println("Type du jour dont vous voulez donner le graphe :");
		Scanner sc = new Scanner(System.in);
		jour = sc.nextInt();
		sc.close();

		try {
			final FileOutputStream fichier = new FileOutputStream(jour+".ser");
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

	public static GrapheTrajet deserialiserGrapheTrajet(int jour){
		GrapheTrajet gt = null;

		ObjectInputStream ois = null;

		try {
			final FileInputStream fichier = new FileInputStream(jour+".ser");
			ois = new ObjectInputStream(fichier);
			gt = (GrapheTrajet)ois.readObject() ;
		} catch (final java.io.IOException e) {
			e.printStackTrace();
		} catch (final ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				if (ois != null) {
					ois.close();
				}
			} catch (final IOException ex) {
				ex.printStackTrace();
			}
		}
		return gt;
	}
}