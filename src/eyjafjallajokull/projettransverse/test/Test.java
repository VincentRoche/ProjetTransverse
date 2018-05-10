package eyjafjallajokull.projettransverse.test;

import java.util.List;
import java.util.Random;

import eyjafjallajokull.projettransverse.model.Arc;
import eyjafjallajokull.projettransverse.model.Ligne;
import eyjafjallajokull.projettransverse.model.Reseau;
import eyjafjallajokull.projettransverse.model.Station;
import eyjafjallajokull.projettransverse.model.Voyageur;
import eyjafjallajokull.projettransverse.view.FenetrePlan;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Test {

	public static Station recherche(Reseau reseau, String nom){
		for (Station s : reseau.getStations()){
			if (s.getNom().equals(nom)){
				return s;
			}
		}
		return null;        
	}

	public static void main(String[] args) {
		int tailleMax = 500;
		Reseau reseau = new Reseau(tailleMax, tailleMax);

		//Lecture fichier Station
		String pathFichier="station.txt";

		BufferedReader fluxEntree=null;
		try {
			fluxEntree = new BufferedReader(new FileReader(pathFichier));

			String ligneLue = null;
			String station [] = null;

			while((ligneLue = fluxEntree.readLine())!=null){
				station = ligneLue.split(" ");

				reseau.ajouterStation(Integer.parseInt(station[1]), Integer.parseInt(station[2]), station[0]);                         
			}
		}
		catch(IOException exc){
			exc.printStackTrace();
		}
		finally{
			try{
				if(fluxEntree!=null){
					/* Fermeture du flux vers le fichier */
					fluxEntree.close();
				}
			}
			catch(IOException e){
				e.printStackTrace();
			}
		}
		//Lecture fichier trajet
		pathFichier="trajet.txt";

		/*BufferedReader */fluxEntree=null;
		try {
			fluxEntree = new BufferedReader(new FileReader(pathFichier));

			String ligneLue = null;
			String trajet [] = null;

			while((ligneLue = fluxEntree.readLine())!=null){
				trajet = ligneLue.split(" ");

				for (int i=0; i < Integer.parseInt(trajet[2]); i++)
				{
					reseau.ajouterVoyageur(recherche(reseau, trajet[0]), recherche(reseau, trajet[1]));
				}                  
			}
		}
		catch(IOException exc){
			exc.printStackTrace();
		}
		finally{
			try{
				if(fluxEntree!=null){
					/* Fermeture du flux vers le fichier */
					fluxEntree.close();
				}
			}
			catch(IOException e){
				e.printStackTrace();
			}
		}

		// Stations aléatoires
		int nbStations = 10;
		Random rand = new Random();
		/*for (int i=0; i < nbStations; i++)
		{
			reseau.ajouterStation(rand.nextInt(tailleMax), rand.nextInt(tailleMax), "villejuif");
		}*/
		List<Station> stations = reseau.getStations();

		// Ligne qui relie tout
		Ligne l = reseau.ajouterLigne();
		for (int i=1; i < stations.size(); i++)
		{
			reseau.ajouterArc(stations.get(i), stations.get(i - 1), l);
		}
		
		// Ligne entre 3 stations
		Ligne l2 = reseau.ajouterLigne();
		reseau.ajouterArc(stations.get(0), stations.get(2), l2);
		reseau.ajouterArc(stations.get(2), stations.get(8), l2);
		reseau.ajouterArc(stations.get(8), stations.get(6), l2);

		// Voyageurs aléatoires
		/*for (Station s : stations)
		{
			// Nombre aléatoire
			for (int i=0; i < rand.nextInt(10); i++)
			{
				reseau.ajouterVoyageur(s, stations.get(rand.nextInt(stations.size())));
			}
		}*/

		// Affichage
		for (Station s : stations)
		{
			System.out.println(s);
		}
		for (Arc a : reseau.getArcs())
		{
			System.out.println(a);
		}
		for (Voyageur v : reseau.getVoyageurs())
		{
			System.out.println(v);
		}

		// Fenêtre
		FenetrePlan f = new FenetrePlan(reseau);
	}

}
