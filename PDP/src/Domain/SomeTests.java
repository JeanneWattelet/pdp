package Domain;

//import java.util.HashSet;
//import java.util.Set;

//import infra.Donnees;
//import infra.DonneesAgency;
//import infra.DonneesCalendar;

public class SomeTests {

	public static void main(String[] args) {
		/*int i = 1;
		Donnees donneesAg = new DonneesAgency(i);
		Donnees donneesCal = new DonneesCalendar(i);
		
		Set<Agency> agencies = new HashSet<Agency>();
		Set<Service> services = new HashSet<Service>();
		
		agencies = ((DonneesAgency) donneesAg).load();
		services = ((DonneesCalendar) donneesCal).load();
		
		System.out.println(agencies);
		System.out.println(services);*/
		Graphe g = new Graphe();
		System.out.println(g);
		
		//System.out.println(g.astar());
	}

}
