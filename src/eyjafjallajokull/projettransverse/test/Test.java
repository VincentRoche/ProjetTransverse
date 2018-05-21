package eyjafjallajokull.projettransverse.test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import eyjafjallajokull.projettransverse.model.Arc;
import eyjafjallajokull.projettransverse.model.IARoche;
import eyjafjallajokull.projettransverse.model.Reseau;
import eyjafjallajokull.projettransverse.model.Station;
import eyjafjallajokull.projettransverse.view.FenetrePlan;

public class Test {

	public static void main(String[] args) {
		Reseau reseau = new Reseau();

		// Lecture fichier Station
		//String pathFichier = "station.txt";
		String pathFichier = "StationsParis.txt";
		int xMax = 0;
		int yMax = 0;

		BufferedReader fluxEntree=null;
		try {
			fluxEntree = new BufferedReader(new FileReader(pathFichier));

			String ligneLue = null;
			String station [] = null;

			while((ligneLue = fluxEntree.readLine())!=null){
				station = ligneLue.split(" ");

				int x = Integer.parseInt(station[1]);
				int y = Integer.parseInt(station[2]);
				reseau.ajouterStation(x, y, station[0]);    
				xMax = Math.max(x, xMax);
				yMax = Math.max(y, yMax);
			}
		}
		catch(IOException exc) {
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

		reseau.setxMax(xMax + 200);
		reseau.setyMax(yMax + 100);

		// Lecture fichier trajet
		//pathFichier="trajet.txt";
		pathFichier="TrajetsParis.txt";

		fluxEntree=null;
		try {
			fluxEntree = new BufferedReader(new FileReader(pathFichier));

			String ligneLue = null;
			String trajet [] = null;

			while((ligneLue = fluxEntree.readLine())!=null) {
				trajet = ligneLue.split(" ");

				Station s1 = reseau.getStation(trajet[0]);
				Station s2 = reseau.getStation(trajet[1]);

				if (s1 == null)
					System.out.println("Erreur : la station " + trajet[0] + " n'est pas définie.");
				else if (s2 == null)
					System.out.println("Erreur : la station " + trajet[1] + " n'est pas définie.");
				else
				{
					for (int i=0; i < Integer.parseInt(trajet[2]); i++)
					{
						reseau.ajouterVoyageur(s1, s2);
					}               
				}
			}
		}
		catch(IOException exc) {
			exc.printStackTrace();
		}
		finally {
			try {
				if (fluxEntree!=null) {
					// Fermeture du flux vers le fichier
					fluxEntree.close();
				}
			}
			catch(IOException e){
				e.printStackTrace();
			}
		}


		// Placement des lignes avec l'IA
		FenetrePlan f = new FenetrePlan(reseau, 0.6);
		reseau = new IARoche(reseau, 18, 25000, f).placerLignes();
		// Fenêtre
		f.majReseau(null);

		// Affichage des données
		/*for (Station s : reseau.getStations())
		{
			System.out.println(s);
		}
		for (Arc a : reseau.getArcs())
		{
			System.out.println(a);
			System.out.println(a.getLongueur());
		}
		for (Voyageur v : reseau.getVoyageurs())
		{
			System.out.println(v);
		}*/


		System.out.println("Longueur du réseau = " + reseau.getLongueur());
		System.out.println("Moyenne des temps de parcours = " + reseau.evaluer());
	}

}
